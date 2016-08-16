package com.gft.digitalbank.exchange.solution.util;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by iozi on 2016-08-16.
 */
@Category(UnitTest.class)
public class OptionalExceptionTest {


    @Test
    public void empty_whenCreatedViaThisMethod_shouldHaveNotExceptionPresent() {
        OptionalException optionalException = OptionalException.empty();
        assertFalse(optionalException.isPresent());
    }

    @Test
    public void of_whenPassedAnException_shouldHaveNotExceptionPresent() {
        Exception exception = new Exception();
        OptionalException optionalException = OptionalException.of(exception);
        assertThat(optionalException.get(),is(sameInstance(exception)));
    }

    @Test(expected = NullPointerException.class)
    public void of_whenPassedNull_shouldThrowNullPointerException() {
        OptionalException.of(null);
    }

}