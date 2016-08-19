package com.gft.digitalbank.exchange.solution.util;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Created by Ivo Zieliński on 2016-08-16.
 */
@Category(UnitTest.class)
public class ExceptionOptionalTest {


    @Test
    public void empty_whenCreatedViaThisMethod_shouldHaveNotExceptionPresent() {
        ExceptionOptional exceptionOptional = ExceptionOptional.empty();
        assertFalse(exceptionOptional.isPresent());
    }

    @Test
    public void of_whenPassedAnException_shouldHaveNotExceptionPresent() {
        Exception exception = new Exception();
        ExceptionOptional exceptionOptional = ExceptionOptional.of(exception);
        assertThat(exceptionOptional.get(), is(sameInstance(exception)));
    }

    @Test(expected = NullPointerException.class)
    public void of_whenPassedNull_shouldThrowNullPointerException() {
        ExceptionOptional.of(null);
    }

}