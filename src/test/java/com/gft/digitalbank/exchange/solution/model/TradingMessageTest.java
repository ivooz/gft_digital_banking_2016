package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
public class TradingMessageTest {

    private static final int ID = 5;
    private static final String BROKER = "B";
    private static final int TIMESTAMP = 1;

    private TradingMessage sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        pojoFactory = new PojoFactory();
        sut = pojoFactory.createOrder();
    }

    @Test
    public void getSetId_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setId(ID);
        assertEquals(ID, sut.getId());
    }

    @Test
    public void getSetBroker_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setBroker(BROKER);
        assertEquals(BROKER, sut.getBroker());
    }

    @Test
    public void getSetTimestamp_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setTimestamp(TIMESTAMP);
        assertEquals(TIMESTAMP, sut.getTimestamp());
    }

    @Test
    public void equals_whenPassedItsOwnReference_shouldReturnTrue() {
        boolean result = sut.equals(sut);
        assertEquals(result, true);
    }

    @Test
    public void equals_whenPassedOrderWithTheSameData_shouldReturnTrue() {
        TradingMessage order = pojoFactory.createOrder();
        TradingMessage orderWithTheSameData = pojoFactory.createOrder();
        boolean result = order.equals(orderWithTheSameData);
        assertEquals(result, true);
    }

    @Test
    public void equals_whenPassedOrderWithUnequalData_shouldReturnFalse() {
        TradingMessage order = pojoFactory.createOrder();
        order.setTimestamp(1000);
        boolean result = sut.equals(order);
        assertEquals(result, false);
    }

    @Test
    public void equals_whenPassedNull_shouldReturnFalse() {
        boolean result = sut.equals(null);
        assertEquals(result, false);
    }
}