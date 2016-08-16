package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by iozi on 2016-08-11.
 */
@Category(UnitTest.class)
public class DetailsTest {

    private Details sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        pojoFactory = new PojoFactory();
        this.sut = pojoFactory.createDetails();
    }

    @Test
    public void copyingConstructor_whenPassedDetails_shouldProduceNewObject() {
        Details copiedDetails = new Details(sut);
        assertThat(copiedDetails, is(not(sameInstance(sut))));
    }
}