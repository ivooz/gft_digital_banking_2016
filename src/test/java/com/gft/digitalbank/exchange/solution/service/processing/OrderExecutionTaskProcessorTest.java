package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeFactory;
import com.gft.digitalbank.exchange.solution.test.utils.OrderPojoFactory;
import javafx.util.Pair;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(JUnitParamsRunner.class)
public class OrderExecutionTaskProcessorTest {

    private static final String PRODUCT_NAME = "product";
    private static final int BUFFER_SIZE = 5;

    private OrderProcessor sut;
    private ProductExchange productExchange;
    private OrderPojoFactory orderPojoFactory;

    @Before
    public void initialize() {
        sut = new OrderProcessor();
        orderPojoFactory = new OrderPojoFactory();
        ProductExchangeFactory productExchangeFactory = new ProductExchangeFactory(BUFFER_SIZE);
        productExchange = productExchangeFactory.createProductExchange(PRODUCT_NAME);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullCancel_shouldThrowNullPointerException() {
        sut.processTradingMessage(null, productExchange);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullProductExchange_shouldThrowNullPointerException() {
        sut.processTradingMessage(orderPojoFactory.createNextOrder(), null);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNulls_shouldThrowNullPointerException() {
        sut.processTradingMessage(null, null);
    }

    @Test
    @Parameters(method = "transactionCounts")
    public void processTradingMessage_whenMultipleIdenticalMatchingOrderWithOppositeSideEnqueuedAndOrderPassed_allShouldGetFullyTradedAndNoOrderShallBecomeEnqueued
            (int transactionCount) {
        List<Pair<Order, Order>> matchingOrdersList = IntStream
                .range(0, transactionCount)
                .mapToObj(index -> orderPojoFactory.createIdenticalBuyAndSellOrders())
                .collect(Collectors.toList());
        matchingOrdersList.forEach(matchingOrderPair -> {
            Order processedOrder = matchingOrderPair.getKey();
            Order enqueuedOrder = matchingOrderPair.getValue();
            productExchange.enqueueOrder(enqueuedOrder);
            sut.processTradingMessage(processedOrder, productExchange);

        });
        matchingOrdersList.forEach(matchingOrderPair -> {
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
    public void processTradingMessage_whenNoMatchingOrderEnqueued_theProcessedOrderShouldBeEnqueued(Side side) {
        Order processedOrder = orderPojoFactory.createNextOrderWithSide(side);
        sut.processTradingMessage(processedOrder, productExchange);
        Optional<Order> orderInQueue = productExchange.peekNextOrder(side);
        assertThat(orderInQueue.get(), is(sameInstance(processedOrder)));
    }

    @Test
    @Parameters(method = "enqueuedOrderCountsAmountsAndProcessedOrderAmountsSides")
    public void processTradingMessage_whenMultipleSmallerMatchingOrderWithOppositeSideEnqueuedAndOrderPassed_thenOnlyOrdersWithAmountsSummingToProcessedOrderAmountShallBeTransacted
            (int enqueuedOrderCount, int enqueuedOrderAmount, int processedOrderAmount, Side processedOrderSide) {
        int expectedTransactionCount = Math.min(processedOrderAmount / enqueuedOrderAmount, enqueuedOrderCount);
        IntStream.range(0, enqueuedOrderCount)
                .forEach(value -> {
                    Order orderToEnqueue = orderPojoFactory.createNextOrderWithAmountAndSide(enqueuedOrderAmount
                            , processedOrderSide.opposite());
                    productExchange.enqueueOrder(orderToEnqueue);
                });
        Order processedOrder = orderPojoFactory.createNextOrderWithAmountAndSide(processedOrderAmount, processedOrderSide);
        sut.processTradingMessage(processedOrder, productExchange);
        List<Transaction> transactions = productExchange.getTransactions();
        assertThat(transactions.size(), is(equalTo(expectedTransactionCount)));
    }

    @Test
    @Parameters(method = "buySellSides")
    public void processTradingMessage_whenNoOrdersEnqueuedButDontMatch_theProcessedOrderShouldBeEnqueued(Side side) {
        int processedOrderPrice = side == Side.BUY ? OrderPojoFactory.DEFAULT_PRICE - 1 : OrderPojoFactory.DEFAULT_PRICE + 1;
        Order processedOrder = orderPojoFactory.createOrderWithPriceAndSide(side,processedOrderPrice);
        Order orderInQueue = orderPojoFactory.createNextOrderWithSide(side.opposite());
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