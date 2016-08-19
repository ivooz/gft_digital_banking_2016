package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.model.SolutionResult;
import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.TransactionFactory;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.utils.OrderPojoFactory;
import com.gft.digitalbank.exchange.solution.utils.TransactionPojoFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo Zieliński on 2016-08-16.
 */
@RunWith(JUnitParamsRunner.class)
@Category(UnitTest.class)
public class ResultsGathererTest {

    private final static String PRODUCT_NAME = "product";

    private ResultsGatherer sut;
    private ProductExchangeIndex productExchangeIndex;
    private OrderPojoFactory orderPojoFactory;
    private TransactionPojoFactory transactionPojoFactory;

    @Before
    public void initialize() {
        productExchangeIndex = Mockito.mock(ProductExchangeIndex.class);
        transactionPojoFactory = new TransactionPojoFactory();
        OrderEntryConverter orderEntryConverter = new OrderEntryConverter();
        sut = new ResultsGatherer(productExchangeIndex, orderEntryConverter);
        orderPojoFactory = new OrderPojoFactory();
    }

    @Test
    public void gatherResults_whenNoProductExchanges_thenSolutionResultsShouldBeEmpty() {
        when(productExchangeIndex.getAllExchanges()).thenReturn(Collections.emptyList());
        SolutionResult solutionResult = sut.gatherResults();
        assertThat(solutionResult.getOrderBooks(), is(empty()));
        assertThat(solutionResult.getTransactions(), is(empty()));
    }

    @Test
    public void gatherResults_whenProductExchangesHaveNoTransactionsAndOrders_thenSolutionResultsShouldBeEmpty() {
        ProductExchange productExchange = Mockito.mock(ProductExchange.class);
        when(productExchange.getTransactions()).thenReturn(Collections.emptyList());
        SolutionResult solutionResult = sut.gatherResults();
        assertThat(solutionResult.getOrderBooks(), is(empty()));
        assertThat(solutionResult.getTransactions(), is(empty()));
    }

    @Test
    @Parameters(method = "orderCountsAndSides")
    public void gatherResults_whenProductExchangeReturnsManyOrders_theyShouldHaveIdEqualToTheirPositionInQueueStartingFromOne(int orderCountBuy, int orderCountSell) {
        ProductExchange productExchange = Mockito.mock(ProductExchange.class);
        when(productExchangeIndex.getAllExchanges()).thenReturn(Collections.singletonList(productExchange));
        when(productExchange.getTransactions()).thenReturn(Collections.emptyList());
        List<Order> buyOrders = mockTradingOrders(orderCountBuy, productExchange, Side.BUY);
        List<Order> sellOrders = mockTradingOrders(orderCountSell, productExchange, Side.SELL);
        when(productExchange.getProductName()).thenReturn(PRODUCT_NAME);
        SolutionResult solutionResult = sut.gatherResults();
        solutionResult.getOrderBooks().forEach(orderBook -> {
            List<OrderEntry> buyEntries = orderBook.getBuyEntries();
            List<OrderEntry> sellEntries = orderBook.getSellEntries();
            verifyBookEntry(buyOrders, buyEntries);
            verifyBookEntry(sellOrders, sellEntries);
        });
    }

    @Test
    @Parameters(method = "transactionCounts")
    public void gatherResults_whenProductExchangeHasTransactions_theResultingSolutionShouldContainIdenticalCollection(int transactionCount) {
        List<Transaction> transactions = new ArrayList<>();
        IntStream.range(0, transactionCount)
                .forEach(value -> transactions.add(transactionPojoFactory.createNextTransaction()));
        ProductExchange productExchange = Mockito.mock(ProductExchange.class);
        when(productExchange.getProductName()).thenReturn(PRODUCT_NAME);
        when(productExchange.getTransactions()).thenReturn(transactions);
        when(productExchange.pollNextOrder(anyObject())).thenReturn(Optional.empty());
        when(productExchangeIndex.getAllExchanges()).thenReturn(Collections.singletonList(productExchange));
        SolutionResult solutionResult = sut.gatherResults();
        assertThat(solutionResult.getTransactions().size(), is(equalTo(transactions.size())));
    }

    private void verifyBookEntry(List<Order> buyOrders, List<OrderEntry> buyEntries) {
        IntStream.range(0, buyEntries.size()).forEach(index -> {
            OrderEntry orderEntry = buyEntries.get(index);
            int orderEntryId = orderEntry.getId();
            assertTrue(orderEqualsOrderEntry(buyOrders.get(orderEntryId - 1), orderEntry));
        });
    }

    private List<Order> mockTradingOrders(int orderCountBuy, ProductExchange productExchange, Side side) {
        List<Order> orders = new ArrayList<>();
        IntStream.range(0, orderCountBuy)
                .forEach(value -> orders.add(orderPojoFactory.createNextOrderWithSide(side)));
        OngoingStubbing<Optional<Order>> ongoingStubbing = when(productExchange.pollNextOrder(side));
        for (int i = 0; i < orderCountBuy; i++) {
            ongoingStubbing = ongoingStubbing.thenReturn(Optional.of(orders.get(i)));
        }
        ongoingStubbing.thenReturn(Optional.empty());
        return orders;
    }

    private boolean orderEqualsOrderEntry(Order order, OrderEntry orderEntry) {
        if (!order.getBroker().equals(orderEntry.getBroker())) {
            return false;
        }
        if (order.getDetails().getAmount() != orderEntry.getAmount()) {
            return false;
        }
        if (order.getDetails().getPrice() != orderEntry.getPrice()) {
            return false;
        }
        return order.getClient().equals(orderEntry.getClient());
    }

    private Object[] transactionCounts() {
        return new Object[]{
                1, 2, 4, 7, 10, 20, 100, 1000
        };
    }

    private Object[] orderCountsAndSides() {
        return new Object[]{
                new Object[]{1, 0},
                new Object[]{0, 1},
                new Object[]{100, 50},
                new Object[]{50, 100},
                new Object[]{111, 111},
                new Object[]{1000, 999}
        };
    }
}