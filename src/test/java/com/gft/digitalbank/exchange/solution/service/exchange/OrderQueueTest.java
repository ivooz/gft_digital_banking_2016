package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.utils.PairListBuilder;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import com.gft.digitalbank.exchange.solution.utils.RandomIntPairListFactory;
import javafx.util.Pair;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Ivo on 11/08/16.
 */
@Category(UnitTest.class)
@RunWith(JUnitParamsRunner.class)
public class OrderQueueTest {

    private OrderQueue sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        this.sut = new OrderQueue();
        this.pojoFactory = new PojoFactory();
    }

    @Test(expected = NullPointerException.class)
    public void pushOrder_whenNullPassed_NullPointerExceptionShouldBeThrown() {
        sut.pushOrder(null);
    }

    @Test(expected = NullPointerException.class)
    public void peekNextOrder_whenPassedNull_shouldThrowNullPointerException() {
        sut.peekNextOrder(null);
    }

    @Parameters(method = "buyAndSellSides")
    @Test
    public void peekNextOrder_whenQueueIsEmpty_itShouldReturnEmptyOptionalsForSide(Side side) {
        Optional<Order> orderOptional = sut.peekNextOrder(side);
        assertThat(orderOptional, is(equalTo(Optional.empty())));
    }

    @Test(expected = NullPointerException.class)
    public void pollNextOrder_whenPassedNull_shouldThrowNullPointerException() {
        sut.pollNextOrder(null);
    }

    @Parameters(method = "buyAndSellSides")
    @Test
    public void pollNextOrder_whenQueueIsEmpty_itShouldReturnEmptyOptionalsForSide(Side side) {
        Optional<Order> orderOptional = sut.pollNextOrder(side);
        assertThat(orderOptional, is(equalTo(Optional.empty())));
    }

    @Parameters(method = "buyAndSellSides")
    @Test
    public void pollOrder_whenOnlyOrdersScheduledForDeletionAreAdded_itShouldReturnEmptyOptional(Side side) {
        addOrdersScheduledForDeletionWithSidesToQueue(side, 10);
        Optional<Order> orderOptional = sut.pollNextOrder(side);
        assertThat(orderOptional, is(equalTo(Optional.empty())));
    }

    @Parameters(method = "buyAndSellSides")
    @Test
    public void peekOrder_whenOnlyOrdersScheduledForDeletionAreAdded_itShouldReturnEmptyOptional(Side side) {
        addOrdersScheduledForDeletionWithSidesToQueue(side, 10);
        Optional<Order> orderOptional = sut.peekNextOrder(side);
        assertThat(orderOptional, is(equalTo(Optional.empty())));
    }

    @Parameters(method = "orderPrices")
    @Test
    public void peekNextOrder_whenVariousBuyOrdersAdded_theOneWithHighestPriceIsReturned(int... prices) {
        addOrdersWithPricesToQueue(Side.BUY, prices);
        Order order = sut.peekNextOrder(Side.BUY).get();
        int maximumPrice = IntStream.of(prices).max().getAsInt();
        assertThat(order.getPrice(), is(equalTo(maximumPrice)));
    }

    @Parameters(method = "orderPrices")
    @Test
    public void peekNextOrder_whenVariousSellOrdersAdded_theOneWithLowestPriceIsReturned(int... prices) {
        addOrdersWithPricesToQueue(Side.SELL, prices);
        Order order = sut.peekNextOrder(Side.SELL).get();
        int minimalPrice = IntStream.of(prices).min().getAsInt();
        assertThat(order.getPrice(), is(equalTo(minimalPrice)));
    }

    @Parameters(method = "orderPrices")
    @Test
    public void pollNextOrder_whenVariousBuyOrdersAdded_theOneWithHighestPriceIsReturned(int... prices) {
        addOrdersWithPricesToQueue(Side.BUY, prices);
        Order order = sut.pollNextOrder(Side.BUY).get();
        int maximumPrice = IntStream.of(prices).max().getAsInt();
        assertThat(order.getPrice(), is(equalTo(maximumPrice)));
    }

    @Parameters(method = "orderPrices")
    @Test
    public void pollNextOrder_whenVariousSellOrdersAdded_theOneWithLowestPriceIsReturned(int... prices) {
        addOrdersWithPricesToQueue(Side.SELL, prices);
        Order order = sut.pollNextOrder(Side.SELL).get();
        int minimalPrice = IntStream.of(prices).min().getAsInt();
        assertThat(order.getPrice(), is(equalTo(minimalPrice)));
    }

    @Parameters(method = "orderPrices")
    @Test
    public void pollNextOrder_whenVariousBuyOrdersAddedAndThenRemoved_returnsEmptyOptional(int... prices) {
        addOrdersWithPricesToQueue(Side.BUY, prices);
        removeOrdersFromQueue(Side.BUY, prices.length);
        Optional<Order> orderOptional = sut.pollNextOrder(Side.BUY);
        assertThat(orderOptional, is(equalTo(Optional.empty())));
    }

    @Parameters(method = "orderPrices")
    @Test
    public void pollNextOrder_whenVariousSellOrdersAddedAndThenRemoved_returnsEmptyOptional(int... prices) {
        addOrdersWithPricesToQueue(Side.SELL, prices);
        removeOrdersFromQueue(Side.SELL, prices.length);
        Optional<Order> orderOptional = sut.pollNextOrder(Side.SELL);
        assertThat(orderOptional, is(equalTo(Optional.empty())));
    }

    @Parameters(method = "orderPricesAndTimestamps")
    @Test
    public void pollNextOrder_whenBuyOrdersWithTheSamePriceAdded_returnsTheOneWithLowestTimestamp
            (Side side, List<Pair<Integer, Integer>> priceAndTimestampPairs) {
        priceAndTimestampPairs.forEach(pair -> sut.pushOrder(pojoFactory
                .buildOrderWithTimestampPriceAndSide(side, pair.getKey(), pair.getValue())));
        int minTimestamp = priceAndTimestampPairs.stream().mapToInt(Pair::getKey)
                .min().getAsInt();
        Order order = sut.pollNextOrder(side).get();
        assertThat(order.getTimestamp(), is(equalTo((long) minTimestamp)));
    }


    private void removeOrdersFromQueue(Side side, int count) {
        IntStream.range(0, count)
                .forEach(price -> sut.pollNextOrder(side));
    }

    private void addOrdersWithPricesToQueue(Side side, int[] prices) {
        IntStream.of(prices)
                .forEach(price -> sut.pushOrder(pojoFactory.buildOrderWithPriceAndSide(side, price)));
    }

    private void addOrdersScheduledForDeletionWithSidesToQueue(Side side, int count) {
        IntStream.range(0, count)
                .forEach(price -> sut.pushOrder(pojoFactory.createOrderScheduledForDeletion(side)));
    }


    private Object buyAndSellSides() {
        return new Object[]{
                Side.BUY,
                Side.SELL
        };
    }

    private Object orderPrices() {
        return new Object[]{
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100000},
                new int[]{1, 553, 7, 1, 6, 9, 3, 353, 78, 13},
                new int[]{1},
                new int[]{0x7fffffff, 1, 0, 0, 0}};
    }

    private Object orderPricesAndTimestamps() {
        return new Object[]{
                new Object[]{Side.BUY, new PairListBuilder<Integer, Integer>().append(1, 1).append(1, 4).append(1, 5)
                        .append(1, 0).append(1, 8).append(1, 1).append(1, 0).build()},
                new Object[]{Side.SELL, new PairListBuilder<Integer, Integer>().append(1, 1).append(1, 4).append(1, 5)
                        .append(1, 0).append(1, 8).append(1, 1).append(1, 0).build()},
                new Object[]{Side.BUY, new PairListBuilder<Integer, Integer>().append(1, 5).append(1, 19).append(1, 1458)
                        .append(1, 1551).append(1, 131).append(1, 141).append(1, 31).build()},
                new Object[]{Side.SELL, new PairListBuilder<Integer, Integer>().append(1, 5).append(1, 19).append(1, 1458)
                        .append(1, 1551).append(1, 131).append(1, 141).append(1, 31).build()},
                new Object[]{Side.BUY, new PairListBuilder<Integer, Integer>().append(0, 1).append(0, 0).append(0, 0)
                        .append(0, 0).append(0, 0).append(0, 10000).append(0, 10000).build()},
                new Object[]{Side.SELL, new PairListBuilder<Integer, Integer>().append(0, 1).append(0, 0).append(0, 0)
                        .append(0, 0).append(0, 0).append(0, 10000).append(0, 10000).build()},
                new Object[]{Side.BUY, new RandomIntPairListFactory().createWithConstantKey(100, 1000)},
                new Object[]{Side.SELL, new RandomIntPairListFactory().createWithConstantKey(100, 1000)},
                new Object[]{Side.BUY, new RandomIntPairListFactory().createWithConstantKey(10000, 10000)},
                new Object[]{Side.SELL, new RandomIntPairListFactory().createWithConstantKey(10000, 10000)}
        };
    }
}