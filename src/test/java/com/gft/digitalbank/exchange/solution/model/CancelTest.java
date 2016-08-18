package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
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
    private Cancel sut;

    @Before
    public void initialize() {
        sut = new Cancel();
    }

    @Test
    public void getSetCancelledOrderId_whenSetToValue_thenGetShouldReturnThatValue() throws Exception {
        sut.setCancelledOrderId(ID);
        assertEquals(ID,sut.getCancelledOrderId());
    }
}