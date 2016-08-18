package com.gft.digitalbank.exchange.solution.service.scheduling.indexing;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
public class IdProductIndexTest {

    private final static int ID = 1;
    private final static String PRODUCT_NAME = "product";

    private IdProductIndex sut;

    @Before
    public void initialize() {
        sut = new IdProductIndex();
    }

    @Test(expected = NullPointerException.class)
    public void put_whenNullOrderPassed_shouldThrowNullPointerException() {
        sut.put(ID, null);
    }

    @Test
    public void put_whenNamePassed_itShouldBeRetrievableViaGet() {
        sut.put(ID, PRODUCT_NAME);
        Optional<String> productName = sut.get(ID);
        assertThat(productName.get(), is(equalTo(PRODUCT_NAME)));
    }

    @Test
    public void put_whenNameNotPassed_itShouldReturnEmptyOptional() {
        Optional<String> productName = sut.get(ID);
        assertThat(productName, is(sameInstance(Optional.empty())));
    }


}