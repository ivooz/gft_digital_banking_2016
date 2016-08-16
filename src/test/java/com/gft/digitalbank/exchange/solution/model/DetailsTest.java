package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.SimpleTradingMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;

/**
 * Created by iozi on 2016-08-11.
 */
@Category({UnitTest.class})
public class DetailsTest {

    private Details sut;

    @Before
    public void initialize() {
        this.sut = new SimpleTradingMessageFactory().getSimpleDetails();
    }
s
    @Test
    public void copyingConstructor_whenPassedDetails_shouldProduceNewObject() {
        Details copiedDetails = new Details(sut);
        assertThat(copiedDetails,is(not(sameInstance(sut))));
    }
}