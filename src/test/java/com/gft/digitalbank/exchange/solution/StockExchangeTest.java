package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.config.CamelConfigurer;
import com.gft.digitalbank.exchange.solution.config.StockExchangeStartupException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by iozi on 2016-08-17.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class StockExchangeTest {

    private StockExchange sut;

    @Mock
    private CamelConfigurer camelConfigurer;

    @Mock
    private Logger logger;

    @Before
    public void initialize() {
        sut = new StockExchange();
        Whitebox.setInternalState(sut,"camelConfigurer",camelConfigurer);
    }

    @Test(expected = NullPointerException.class)
    public void register_whenPassedNullProductExchange_shouldThrowNullPointerException() {
        sut.register(null);
    }

    @Test
    public void register_whenPassedProcessingListener_itShouldBePassedToCamelConfigurer() {
        ProcessingListener processingListener = Mockito.mock(ProcessingListener.class);
        sut.register(processingListener);
        Mockito.verify(camelConfigurer,times(1)).registerProcessingListener(eq(processingListener));
    }

    @Test(expected = NullPointerException.class)
    public void setDestinations_whenPassedNullProductExchange_shouldThrowNullPointerException()  {
        sut.setDestinations(null);
    }

    @Test
    public void setDestinations_whenPassedListOfDestinations_shouldPassItToCamelConfigurer()  {
        List<String> destinations = Arrays.asList();
        sut.setDestinations(destinations);
        try {
            Mockito.verify(camelConfigurer,times(1)).configure(eq(destinations),anyObject());
        } catch (StockExchangeStartupException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void setDestinations_whenCamelConfigurerThrowsException_itShouldNotBeRethrown() throws StockExchangeStartupException {
        List<String> destinations = Arrays.asList();
        doThrow(StockExchangeStartupException.class)
                .when(camelConfigurer).configure(eq(destinations),anyObject());
        sut.setDestinations(destinations);
    }

    @Test
    public void start_whenCalled_shouldStartCamelConfigurer() throws StockExchangeStartupException {
        sut.start();
        Mockito.verify(camelConfigurer,times(1)).start();
    }

    @Test
    public void start_whenCamelConfigurerThrowsException_itShouldNotBeRethrown() throws StockExchangeStartupException {
        doThrow(StockExchangeStartupException.class).when(camelConfigurer).start();
        sut.start();
    }
}