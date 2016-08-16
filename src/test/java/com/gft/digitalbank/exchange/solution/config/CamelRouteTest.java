package com.gft.digitalbank.exchange.solution.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;

import java.util.Arrays;

/**
 * Created by Ivo on 15/08/16.
 */
public abstract class CamelRouteTest extends CamelTestSupport {

    protected static final String MOCK_ENDPOINT_PREFIX = "mock:";

    protected static final String MOCK_ORDERS_ENDPOINT_NAME =
            MOCK_ENDPOINT_PREFIX + CamelRouteBuilder.ORDERS_ENDPOINT_NAME;
    protected static final String MOCK_MODIFICATIONS_ENDPOINT_NAME =
            MOCK_ENDPOINT_PREFIX + CamelRouteBuilder.MODIFICATIONS_ENDPOINT_NAME;
    protected static final String MOCK_CANCELS_ENDPOINT_NAME =
            MOCK_ENDPOINT_PREFIX + CamelRouteBuilder.CANCELS_ENDPOINT_NAME;
    protected static final String MOCK_SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME =
            MOCK_ENDPOINT_PREFIX + CamelRouteBuilder.SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME;
    protected static final String MOCK_SCHEDULING_TASKS_ENDPOINT_NAME =
            MOCK_ENDPOINT_PREFIX + CamelRouteBuilder.SCHEDULING_TASKS_ENDPOINT_NAME;

    public abstract void mockEndpoints() throws Exception;

    @After
    public void tearDown() throws Exception {
        context.stop();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        CamelRouteBuilder camelRouteBuilder = injector.getInstance(CamelRouteBuilder.class);
        camelRouteBuilder.setDestinations(Arrays.asList());
        camelRouteBuilder.setContext(context);
        return camelRouteBuilder;
    }
}
