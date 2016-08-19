package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
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
    private static final int PRICE = 5;
    private Details sut;

    @Before
    public void initialize() {
        PojoFactory pojoFactory = new PojoFactory();
        this.sut = pojoFactory.createDetails();
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

    @Test
    public void getSetPrice_whenSetToValue_thenGetShouldReturnThatValue() {
        sut.setPrice(PRICE);
        assertEquals(PRICE, sut.getPrice());
    }

}