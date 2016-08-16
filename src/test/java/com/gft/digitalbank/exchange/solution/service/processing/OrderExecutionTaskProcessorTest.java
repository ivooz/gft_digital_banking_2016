package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeFactory;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.util.Pair;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(JUnitParamsRunner.class)
public class OrderExecutionTaskProcessorTest {

    public static final String PRODUCT_NAME = "product";
    private OrderExecutionTaskProcessor sut;
    private ProductExchange productExchange;
    private PojoFactory pojoFactory;

    @Inject
    private ProductExchangeFactory productExchangeFactory;

    @Before
    public void initialize() {
        sut = new OrderExecutionTaskProcessor();
        pojoFactory = new PojoFactory();
        Injector injector = Guice.createInjector(new StockExchangeModule());
        injector.injectMembers(this);
        productExchange = productExchangeFactory.createProductExchange(PRODUCT_NAME);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullCancel_shouldThrowNullPointerException()
            throws OrderProcessingException {
        sut.processTradingMessage(null, productExchange);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullProductExchange_shouldThrowNullPointerException()
            throws OrderProcessingException {
        sut.processTradingMessage(pojoFactory.createNextOrder(), null);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNulls_shouldThrowNullPointerException()
            throws OrderProcessingException {
        sut.processTradingMessage(null, null);
    }

    @Test
    @Parameters(method = "transactionCounts")
    public void processTradingMessage_whenMultipleIdenticalMatchingOrderWithOppositeSideEnqueuedAndOrderPassed_allShouldGetFullyTradedAndNoOrderShallBecomeEnqueued
            (int transactionCount) throws OrderProcessingException {
        List<Pair<Order, Order>> matchingOrdersList = IntStream
                .range(0, transactionCount)
                .mapToObj(index -> pojoFactory.createIdenticalBuyAndSellOrders())
                .collect(Collectors.toList());
        matchingOrdersList.stream()
                .forEach(matchingOrderPair -> {
                    Order processedOrder = matchingOrderPair.getKey();
                    Order enqueuedOrder = matchingOrderPair.getValue();
                    productExchange.enqueueOrder(enqueuedOrder);
                    try {
                        sut.processTradingMessage(processedOrder, productExchange);
                    } catch (OrderProcessingException e) {
                        fail(e.getMessage());
                    }

                });
        matchingOrdersList.stream()
                .forEach(matchingOrderPair -> {
                    Order processedOrder = matchingOrderPair.getKey();
                    Order enqueuedOrder = matchingOrderPair.getValue();
                    assertThat(enqueuedOrder.isFullyTraded(), is(equalTo(true)));
                    assertThat(processedOrder.isFullyTraded(), is(equalTo(true)));
                });
        Optional<Order> orderInQueue = productExchange.peekNextOrder(Side.BUY);
        assertThat(orderInQueue, is(sameInstance(Optional.empty())));
        orderInQueue = productExchange.peekNextOrder(Side.SELL);
        assertThat(orderInQueue, is(sameInstance(Optional.empty())));
        List<Transaction> transactionsList = productExchange.getTransactions();
        assertThat(transactionsList.size(), is(equalTo(transactionCount)));
    }

    @Test
    @Parameters(method = "buySellSides")
    public void processTradingMessage_whenNoMatchingOrderEnqueued_theProcessedOrderShouldBeEnqueued(Side side)
            throws OrderProcessingException {
        Order processedOrder = pojoFactory.createNextOrderWithSide(side);
        sut.processTradingMessage(processedOrder, productExchange);
        Optional<Order> orderInQueue = productExchange.peekNextOrder(side);
        assertThat(orderInQueue.get(), is(sameInstance(processedOrder)));
    }

    @Test
    @Parameters(method = "enqueuedOrderCountsAmountsAndProcessedOrderAmountsSides")
    public void processTradingMessage_whenMultipleSmallerMatchingOrderWithOppositeSideEnqueuedAndOrderPassed_thenOnlyOrdersWithAmountsSummingToProcessedOrderAmountShallBeTransacted
            (int enqueuedOrderCount, int enqueuedOrderAmount, int processedOrderAmount, Side processedOrderSide)
            throws OrderProcessingException {
        int expectedTransactionCount = Math.min(processedOrderAmount / enqueuedOrderAmount,enqueuedOrderCount);
//        if ((expectedTransactionCount * enqueuedOrderAmount) < processedOrderAmount) {
//            //There shall be one extra transaction partially consuming a queued Order
//            expectedTransactionCount++;
//        }
        IntStream.range(0, enqueuedOrderCount)
                .forEach(value -> {
                    Order orderToEnqueue = pojoFactory.createNextOrderWithAmountAndSide(enqueuedOrderAmount
                            ,processedOrderSide.opposite());
                    productExchange.enqueueOrder(orderToEnqueue);
                });
        Order processedOrder = pojoFactory.createNextOrderWithAmountAndSide(processedOrderAmount,processedOrderSide);
        sut.processTradingMessage(processedOrder,productExchange);
        List<Transaction> transactions = productExchange.getTransactions();
        assertThat(transactions.size(),is(equalTo(expectedTransactionCount)));
    }

    @Test
    @Parameters(method = "buySellSides")
    public void processTradingMessage_whenNoOrdersEnqueuedButDontMatch_theProcessedOrderShouldBeEnqueued(Side side)
            throws OrderProcessingException {
        Order processedOrder = pojoFactory.createNextOrderWithSide(side);
        Order orderInQueue = pojoFactory.createNextOrderWithSide(side.opposite());
        int orderInQueuePrice = orderInQueue.getPrice();
        processedOrder.getDetails().setPrice(side == Side.BUY ? orderInQueuePrice - 1 : orderInQueuePrice + 1);
        productExchange.enqueueOrder(orderInQueue);
        sut.processTradingMessage(processedOrder, productExchange);
        orderInQueue = productExchange.peekNextOrder(side).get();
        assertThat(orderInQueue, is(sameInstance(processedOrder)));
    }

    private Object[] transactionCounts() {
        return new Object[]{
                1, 2, 4, 7, 10, 20, 100, 1000
        };
    }

    private Object[] buySellSides() {
        return new Object[]{
                Side.BUY, Side.SELL
        };
    }

    private Object[] enqueuedOrderCountsAmountsAndProcessedOrderAmountsSides() {
        return new Object[]{
                new Object[]{10, 1, 10, Side.BUY},
                new Object[]{10, 1, 10, Side.SELL},
                new Object[]{1, 1, 1, Side.BUY},
                new Object[]{1, 1, 1, Side.SELL},
                new Object[]{100, 1, 1000, Side.BUY},
                new Object[]{100, 1, 1000, Side.SELL},
                new Object[]{5000, 1, 100, Side.BUY},
                new Object[]{5000, 1, 100, Side.SELL},
                new Object[]{200, 50, 1000, Side.BUY},
                new Object[]{200, 50, 1000, Side.SELL}
        };
    }


}