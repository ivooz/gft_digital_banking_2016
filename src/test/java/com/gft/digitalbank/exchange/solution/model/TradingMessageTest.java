package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
public class TradingMessageTest {

    private static final int ID = 5;
    private static final String BROKER = "B";
    private static final int TIMESTAMP = 1;
    
    private TradingMessage sut;

    @Before
    public void initialize() {
        PojoFactory pojoFactory = new PojoFactory();
        sut = pojoFactory.createOrder();
    }

    @Test
    public void getSetId_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setId(ID);
        assertEquals(ID,sut.getId());
    }

    @Test
    public void getSetBroker_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setBroker(BROKER);
        assertEquals(BROKER,sut.getBroker());
    }

    @Test
    public void getSetTimestamp_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setTimestamp(TIMESTAMP);
        assertEquals(TIMESTAMP,sut.getTimestamp());
    }
}