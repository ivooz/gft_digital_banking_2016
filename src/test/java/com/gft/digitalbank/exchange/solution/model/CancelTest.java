package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.CancelPojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
public class CancelTest {

    private static final int ID = 5;
    private Cancel sut;
    private CancelPojoFactory cancelPojoFactory;

    @Before
    public void initialize() {
        cancelPojoFactory = new CancelPojoFactory();
        sut = cancelPojoFactory.createDefaultCancel();
    }

    @Test
    public void getSetCancelledOrderId_whenSetToValue_thenGetShouldReturnThatValue() throws Exception {
        sut = cancelPojoFactory.createCancelWithCancelledOrderId(ID);
        assertEquals(ID, sut.getCancelledOrderId());
    }
}