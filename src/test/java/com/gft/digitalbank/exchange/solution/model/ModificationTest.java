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
public class ModificationTest {

    private static final int MODIFIED_ORDER_ID = 5;
    private static final int PRICE = 5;
    private static final int AMOUNT = 5;

    private Modification sut;

    @Before
    public void initialize() {
        PojoFactory pojoFactory = new PojoFactory();
        sut = pojoFactory.createModification(PojoFactory.MODIFIED_ORDER_ID, AMOUNT, PRICE);
    }


    @Test
    public void getModifiedOrderId_whenSetToValue_thenGetShouldReturnThatValue() throws Exception {
        assertEquals(MODIFIED_ORDER_ID, sut.getModifiedOrderId());
    }

    @Test
    public void getDetails_whenSetToValue_thenGetShouldReturnThatValue() throws Exception {
        Details details = sut.getDetails();
        assertEquals(PRICE, details.getPrice());
        assertEquals(AMOUNT, details.getAmount());
    }

}