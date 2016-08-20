package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.test.utils.OrderPojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
public class TradingMessageTest {

    private TradingMessage sut;
    private OrderPojoFactory orderPojoFactory;

    @Before
    public void initialize() {
        orderPojoFactory = new OrderPojoFactory();
        sut = orderPojoFactory.createDefault();
    }

    @Test
    public void getId_whenSetToValue_thenGetShouldReturnThatValue() {
        assertEquals(OrderPojoFactory.DEFAULT_ID, sut.getId());
    }

    @Test
    public void getBroker_whenSetToValue_thenGetShouldReturnThatValue() {
        assertEquals(OrderPojoFactory.DEFAULT_BROKER, sut.getBroker());
    }

    @Test
    public void getTimestamp_whenSetToValue_thenGetShouldReturnThatValue() {
        assertEquals(OrderPojoFactory.DEFAULT_TIMESTAMP, sut.getTimestamp());
    }
}