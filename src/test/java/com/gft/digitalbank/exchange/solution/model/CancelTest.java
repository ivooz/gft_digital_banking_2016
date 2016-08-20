package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.test.utils.CancelPojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
public class CancelTest {

    private static final int ID = 5;
    public static final int DIFFERENT_CANCELLED_ORDER_ID = 6;
    private Cancel sut;
    private CancelPojoFactory cancelPojoFactory;

    @Before
    public void initialize() {
        cancelPojoFactory = new CancelPojoFactory();
        sut = cancelPojoFactory.createDefault();
    }

    @Test
    public void getSetCancelledOrderId_whenSetToValue_thenGetShouldReturnThatValue() throws Exception {
        sut = cancelPojoFactory.createCancelWithCancelledOrderId(ID);
        assertEquals(ID, sut.getCancelledOrderId());
    }

    @Test
    public void equals_whenPassedItsOwnReference_shouldReturnTrue() {
        assertTrue(sut.equals(sut));
    }

    @Test
    public void equals_whenPassedCancelWithTheSameData_shouldReturnTrue() {
        Cancel cancel = cancelPojoFactory.createDefault();
        Cancel cancelWithTheSameData = cancelPojoFactory.createDefault();
        assertTrue(cancel.equals(cancelWithTheSameData));
        assertTrue(cancelWithTheSameData.equals(cancel));
    }

    @Test
    public void equals_whenPassedCancelWithUnequalData_shouldReturnFalse() {
        Cancel cancel = cancelPojoFactory.createCancelWithCancelledOrderId(DIFFERENT_CANCELLED_ORDER_ID);
        assertFalse(sut.equals(cancel));
        assertFalse(cancel.equals(sut));
    }

    @Test
    public void equals_whenPassedNull_shouldReturnFalse() {
        assertFalse(sut.equals(null));
    }

    @Test
    public void hashCode_whenTwoAreEqual_theyShouldReturnTheSameHashCode() {
        Cancel cancel = cancelPojoFactory.createDefault();
        Cancel cancelWithTheSameData = cancelPojoFactory.createDefault();
        assertTrue(cancel.equals(cancelWithTheSameData));
        assertTrue(cancelWithTheSameData.equals(cancel));
        assertEquals(cancel.hashCode(),cancelWithTheSameData.hashCode());
    }
}