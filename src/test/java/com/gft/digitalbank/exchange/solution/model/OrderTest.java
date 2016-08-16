package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.SimpleTradingMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by iozi on 2016-08-11.
 */
@Category({UnitTest.class})
public class OrderTest {

    private Order sut;
    private SimpleTradingMessageFactory simpleTradingMessageFactory;

    @Before
    public void initialize() {
        this.simpleTradingMessageFactory = new SimpleTradingMessageFactory();
        this.sut = simpleTradingMessageFactory.getSimpleOrder();
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
        assertThat(sut.isFullyProcessed(), is(true));
    }

    @Test
    public void isFullyTraded_whenOrderHasAmountOfNonZero_shouldReturnFalse() {
        sut.getDetails().setAmount(1);
        assertThat(sut.isFullyProcessed(), is(false));
    }

    @Test
    public void compareTo_whenGivenOrderWithLowerTimestamp_returnsPositiveValue() {
        sut.setTimestamp(10);
        Order orderWithLowerTimestamp = simpleTradingMessageFactory.getSimpleOrder();
        orderWithLowerTimestamp.setTimestamp(0);
        assertThat(sut.compareTo(orderWithLowerTimestamp),is(OrderingComparison.));
    }

}