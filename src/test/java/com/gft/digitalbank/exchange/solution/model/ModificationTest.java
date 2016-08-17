package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

/**
 * Created by iozi on 2016-08-17.
 */
@Category(UnitTest.class)
public class ModificationTest {

    public static final int MODIFIED_ORDER_ID = 5;
    public static final int PRICE = 5;
    public static final int AMOUNT = 5;

    private Modification sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        pojoFactory = new PojoFactory();
        sut = pojoFactory.createModification(PojoFactory.MODIFIED_ORDER_ID, AMOUNT, PRICE);
    }


    @Test
    public void getModifiedOrderId_whenSetToValue_thenGetShouldReturnThatValue() throws Exception {
        assertEquals(MODIFIED_ORDER_ID,sut.getModifiedOrderId());
    }

    @Test
    public void getDetails_whenSetToValue_thenGetShouldReturnThatValue() throws Exception {
        Details details = sut.getDetails();
        assertEquals(PRICE,details.getPrice());
        assertEquals(AMOUNT,details.getAmount());
    }

}