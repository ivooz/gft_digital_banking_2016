package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

/**
 * Created by iozi on 2016-08-11.
 */
@Category({UnitTest.class})
@RunWith(JUnitParamsRunner.class)
public class OrderTest {

    private Order sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        this.pojoFactory = new PojoFactory();
        this.sut = pojoFactory.createNextOrder();
    }

    @Test
    public void copyConstructor_whenGivenOrder_shouldReturnNewInstance() {
        Order copiedOrder = new Order(sut);
        assertThat(sut, is(not(sameInstance(copiedOrder))));
    }

    @Test
    public void copyConstructor_whenGivenOrder_shouldReturnEqualOrder() {
        Order copiedOrder = new Order(sut);
        assertThat(sut, is(copiedOrder));
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
        sut.setTimestamp(10);
        Order orderWithLowerTimestamp = pojoFactory.createNextOrder();
        orderWithLowerTimestamp.setTimestamp(0);
        assertThat(sut.compareTo(orderWithLowerTimestamp), is(greaterThan(0)));
    }

    @Test
    public void compareTo_whenGivenOrderWithTheSamePriceAndHigherTimestamp_returnsNegativeValue() {
        sut.setTimestamp(0);
        Order orderWithLowerTimestamp = pojoFactory.createNextOrder();
        orderWithLowerTimestamp.setTimestamp(10);
        assertThat(sut.compareTo(orderWithLowerTimestamp), is(lessThan(0)));
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void compareTo_whenGivenSameSideOrderWithHigherPrice_returnsPositiveForBuyAndNegativeForSell(Side side) {
        sut.setSide(side);
        sut.getDetails().setPrice(0);
        Order orderWithLowerTimestamp = pojoFactory.createNextOrderWithSide(side);
        orderWithLowerTimestamp.getDetails().setPrice(10);
        if (side == Side.BUY) {
            assertThat(sut.compareTo(orderWithLowerTimestamp), is(greaterThan(0)));
        } else {
            assertThat(sut.compareTo(orderWithLowerTimestamp), is(lessThan(0)));
        }
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void compareTo_whenGivenSameSideOrderWithLowerPrice_returnsNegativeForBuyAndPositiveForSell(Side side) {
        sut.setSide(side);
        sut.getDetails().setPrice(10);
        Order orderWithLowerPrive = pojoFactory.createNextOrderWithSide(side);
        orderWithLowerPrive.getDetails().setPrice(0);
        if (side == Side.BUY) {
            assertThat(sut.compareTo(orderWithLowerPrive), is(lessThan(0)));
        } else {
            assertThat(sut.compareTo(orderWithLowerPrive), is(greaterThan(0)));
        }
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void compareTo_whenGivenSameSideOrderWithEqualTimestampAndPrice_returnsZeroForBothSides(Side side) {
        sut.setSide(side);
        sut.getDetails().setPrice(10);
        sut.setTimestamp(10);
        Order orderWithEqual = pojoFactory.createNextOrderWithSide(side);
        orderWithEqual.getDetails().setPrice(10);
        orderWithEqual.setTimestamp(10);
        assertThat(sut.compareTo(orderWithEqual), is(equalTo(0)));
    }

    @Test
    public void compareTo_whenComparedToItself_returnsZero() {
        assertThat(sut.compareTo(sut), is(equalTo(0)));
    }

    private Object buyAndSellSides() {
        return new Object[]{
                Side.BUY,
                Side.SELL
        };
    }

}