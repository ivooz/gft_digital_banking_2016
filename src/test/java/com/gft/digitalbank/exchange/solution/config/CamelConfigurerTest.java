package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.monitoring.ShutdownNotificationListener;
import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

/**
 * Created by Ivo Zieliński on 2016-08-16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class CamelConfigurerTest {

    private static final String BROKER_URL = "vm://localhost";
    private CamelConfigurer sut;

    @Mock
    private CamelContext camelContext;

    @Mock
    private CamelRouteBuilder camelRouteBuilder;

    @Mock
    private ShutdownNotificationListener shutdownNotificationListener;

    @Mock
    private ProcessingListener processingListener;

    @Before
    public void initialize() {
        sut = new CamelConfigurer(camelContext, camelRouteBuilder, shutdownNotificationListener);
    }

    @Test(expected = NullPointerException.class)
    public void configure_whenPasseNullDestinations_shouldThrowNullPointerException() throws Exception {
        sut.configure(null, BROKER_URL);
    }

    @Test(expected = NullPointerException.class)
    public void configure_whenPasseNullBrokerUrl_shouldThrowNullPointerException() throws Exception {
        sut.configure(Collections.emptyList(), null);
    }

    @Test(expected = NullPointerException.class)
    public void configure_whenPasseNulls_shouldThrowNullPointerException() throws Exception {
        sut.configure(null, null);
    }

    @Test
    public void configure_whenDestinationsAndBrokerUrlPassed_shouldAddAMQComponentAndRoutesAndBrokerCount() throws Exception {
        List<String> destinations = Collections.emptyList();
        sut.configure(destinations, BROKER_URL);
        Mockito.verify(camelContext, times(1)).addComponent(anyObject(), anyObject());
        Mockito.verify(shutdownNotificationListener, times(1)).setBrokerCount(destinations.size());

    }

    @Test(expected = StockExchangeStartupException.class)
    public void configure_whenCamelContextThrowsExceptionDuringAddingRoutes_shouldWrapAndRethrow() throws Exception {
        doThrow(Exception.class).when(camelContext).addRoutes(anyObject());
        sut.configure(Collections.emptyList(), BROKER_URL);
    }

    @Test
    public void registerProcessingListener_whenPassedProcessingListener_shouldPassItToShutdownNotificationListener() throws Exception {
        sut.registerProcessingListener(processingListener);
        Mockito.verify(shutdownNotificationListener, times(1)).setProcessingListener(processingListener);
    }

    @Test
    public void start_whenCalled_shouldStartCamelContext() throws Exception {
        sut.start();
        Mockito.verify(camelContext, times(1)).start();
    }

    @Test(expected = StockExchangeStartupException.class)
    public void start_whenCamelContextThrowsExceptionDuringStart_shouldWrapAndRethrow() throws Exception {
        doThrow(Exception.class).when(camelContext).start();
        sut.start();
    }
}