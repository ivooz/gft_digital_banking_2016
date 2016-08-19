package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.OrderPojoFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Ivo Zieliński on 2016-08-11.
 */
@Category({UnitTest.class})
@RunWith(JUnitParamsRunner.class)
public class OrderTest {

    private static final String CLIENT = "CLIENT";
    public static final String PRODUCT = "PR";
    private static final int PRICE = 5;
    private static final int AMOUNT = 5;
    private Order sut;
    private OrderPojoFactory orderPojoFactory;

    @Before
    public void initialize() {
        this.orderPojoFactory = new OrderPojoFactory();
        this.sut = orderPojoFactory.createDefaultOrder();
    }

    @Test
    public void copyConstructor_whenGivenOrder_shouldReturnNewInstance() {
        Order copiedOrder = new Order(sut, orderPojoFactory.DEFAULT_TIMESTAMP);
        assertThat(sut, is(not(sameInstance(copiedOrder))));
    }

    @Test
    public void copyConstructor_whenGivenOrder_shouldReturnEqualOrder() {
        Order copiedOrder = new Order(sut, orderPojoFactory.DEFAULT_TIMESTAMP);
        assertThat(sut, is(equalTo(copiedOrder)));
    }

    @Test
    public void isFullyTraded_whenOrderHasAmountOfZero_shouldReturnTrue() {
        sut.getDetails().setAmount(0);
        assertThat(sut.isFullyTraded(), is(true));
    }

    @Test
    public void isFullyTraded_whenOrderHasAmountOfNonZero_shouldReturnFalse() {
        sut.getDetails().setAmount(1);
        assertThat(sut.isFullyTraded(), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void compareTo_whenGivenNull_shouldThrowNullPointerException() {
        sut.compareTo(null);
    }

    @Test
    public void compareTo_whenGivenOrderWithTheSamePriceAndLowerTimestamp_returnsPositiveValue() {
        sut = orderPojoFactory.createOrderWithTimestamp(10);
        Order orderWithLowerTimestamp = orderPojoFactory.createOrderWithTimestamp(0);
        assertThat(sut.compareTo(orderWithLowerTimestamp), is(greaterThan(0)));
    }

    @Test
    public void compareTo_whenGivenOrderWithTheSamePriceAndHigherTimestamp_returnsNegativeValue() {
        sut = orderPojoFactory.createOrderWithTimestamp(0);
        Order orderWithLowerTimestamp = orderPojoFactory.createOrderWithTimestamp(10);
        assertThat(sut.compareTo(orderWithLowerTimestamp), is(lessThan(0)));
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void compareTo_whenGivenSameSideOrderWithHigherPrice_returnsPositiveForBuyAndNegativeForSell(Side side) {
        sut = orderPojoFactory.createOrderWithPriceAndSide(side,0);
        Order orderWithLowerTimestamp = orderPojoFactory.createOrderWithPriceAndSide(side,10);
        if (side == Side.BUY) {
            assertThat(sut.compareTo(orderWithLowerTimestamp), is(greaterThan(0)));
        } else {
            assertThat(sut.compareTo(orderWithLowerTimestamp), is(lessThan(0)));
        }
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void compareTo_whenGivenSameSideOrderWithLowerPrice_returnsNegativeForBuyAndPositiveForSell(Side side) {
        sut = orderPojoFactory.createOrderWithPriceAndSide(side,10);
        Order orderWithLowerPrice = orderPojoFactory.createOrderWithPriceAndSide(side,0);
        if (side == Side.BUY) {
            assertThat(sut.compareTo(orderWithLowerPrice), is(lessThan(0)));
        } else {
            assertThat(sut.compareTo(orderWithLowerPrice), is(greaterThan(0)));
        }
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void compareTo_whenGivenSameSideOrderWithEqualTimestampAndPrice_returnsZeroForBothSides(Side side) {
        sut = orderPojoFactory.createOrderWithTimestampPriceAndSide(side,10,10);
        Order orderWithEqual = orderPojoFactory.createOrderWithTimestampPriceAndSide(side,10,10);
        assertThat(sut.compareTo(orderWithEqual), is(equalTo(0)));
    }

    @Test
    public void compareTo_whenComparedToItself_returnsZero() {
        assertThat(sut.compareTo(sut), is(equalTo(0)));
    }

    @Test
    public void equals_whenPassedItsOwnReference_shouldReturnTrue() {
        boolean result = sut.equals(sut);
        assertEquals(true,result);
    }

    @Test
    public void equals_whenPassedOrderWithTheSameData_shouldReturnTrue() {
        Order order = orderPojoFactory.createDefaultOrder();
        Order orderWithTheSameData = orderPojoFactory.createDefaultOrder();
        boolean result = order.equals(orderWithTheSameData);
        assertEquals(true,result);
    }

    @Test
    public void equals_whenPassedOrderWithUnequalData_shouldReturnFalse() {
        Order order = orderPojoFactory.createOrderWithTimestamp(1000);
        boolean result = sut.equals(order);
        assertEquals(false,result);
    }

    @Test
    public void equals_whenPassedNull_shouldReturnFalse() {
        boolean result = sut.equals(null);
        assertEquals(false,result);
    }

    private Object buyAndSellSides() {
        return new Object[]{
                Side.BUY,
                Side.SELL
        };
    }

}