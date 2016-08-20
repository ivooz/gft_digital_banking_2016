package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.test.utils.ModificationPojoFactory;
import com.gft.digitalbank.exchange.solution.test.utils.OrderPojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
public class ModificationTest {

    private static final int MODIFIED_ORDER_ID = 5;
    private static final int PRICE = 5;
    private static final int AMOUNT = 5;

    private ModificationPojoFactory modificationPojoFactory;
    private Modification sut;

    @Before
    public void initialize() {
        modificationPojoFactory = new ModificationPojoFactory();
        sut = modificationPojoFactory.createModificationWithModifiedOrderIdAmountAndPrice(OrderPojoFactory.MODIFIED_ORDER_ID, AMOUNT, PRICE);
    }


    @Test
    public void getModifiedOrderId_whenSetToValue_thenGetShouldReturnThatValue() {
        assertEquals(MODIFIED_ORDER_ID, sut.getModifiedOrderId());
    }

    @Test
    public void getDetails_whenSetToValue_thenGetShouldReturnThatValue() {
        Details details = sut.getDetails();
        assertEquals(PRICE, details.getPrice());
        assertEquals(AMOUNT, details.getAmount());
    }

    @Test
    public void equals_whenPassedItsOwnReference_shouldReturnTrue() {
        assertTrue(sut.equals(sut));
    }

    @Test
    public void equals_whenPassedModificationWithTheSameData_shouldReturnTrue() {
        Modification modification = modificationPojoFactory.createDefault();
        Modification modificationWithTheSameData = modificationPojoFactory.createDefault();
        assertTrue(modification.equals(modificationWithTheSameData));
        assertTrue(modificationWithTheSameData.equals(modification));
    }

    @Test
    public void equals_whenPassedModificationWithUnequalData_shouldReturnFalse() {
        Modification modification = modificationPojoFactory.createDefault();
        assertFalse(sut.equals(modification));
        assertFalse(modification.equals(sut));
    }

    @Test
    public void equals_whenPassedNull_shouldReturnFalse() {
        assertFalse(sut.equals(null));
    }

    @Test
    public void hashCode_whenTwoAreEqual_theyShouldReturnTheSameHashCode() {
        Modification modification = modificationPojoFactory.createDefault();
        Modification modificationWithTheSameData = modificationPojoFactory.createDefault();
        assertTrue(modification.equals(modificationWithTheSameData));
        assertTrue(modificationWithTheSameData.equals(modification));
        assertEquals(modification.hashCode(),modificationWithTheSameData.hashCode());
    }

}