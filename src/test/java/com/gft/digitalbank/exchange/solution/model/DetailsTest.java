package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.DetailsPojoFactory;
import com.gft.digitalbank.exchange.solution.utils.OrderPojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Ivo Zieliński on 2016-08-11.
 */
@Category(UnitTest.class)
public class DetailsTest {

    private static final int AMOUNT = 5;
    private Details sut;

    @Before
    public void initialize() {
        DetailsPojoFactory detailsPojoFactory = new DetailsPojoFactory();
        this.sut = detailsPojoFactory.createDefaultDetails();
    }

    @Test
    public void copyingConstructor_whenPassedDetails_shouldProduceNewObject() {
        Details copiedDetails = new Details(sut);
        assertThat(copiedDetails, is(not(sameInstance(sut))));
    }

    @Test
    public void getSetAmount_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setAmount(AMOUNT);
        assertEquals(AMOUNT, sut.getAmount());
    }
}