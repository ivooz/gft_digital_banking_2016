package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.test.utils.ResourceLoader;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

/**
 * Created by Ivo on 15/08/16.
 */
@Category(UnitTest.class)
public class CamelContentBasedRoutingTest extends CamelRouteTest {

    public static final String IGNORE_MESSAGE_ENDPOINT = "mock:ignore";
    public static final String ORDER_JSON_FILENAME = "order.json";
    public static final String MODIFICATION_JSON_FILENAME = "modification.json";
    public static final String CANCEL_JSON_FILENAME = "cancel.json";
    public static final String SHUTDOWN_NOTIFICATION_JSON_FILENAME = "shutdownNotification.json";

    private final ResourceLoader resourceLoader = new ResourceLoader();

    @Produce(uri = CamelRouteBuilder.AMQ_MESSAGE_ENDPOINT)
    private ProducerTemplate template;

    @EndpointInject(uri = MOCK_ORDERS_ENDPOINT_NAME)
    private MockEndpoint ordersEndpoint;

    @EndpointInject(uri = MOCK_CANCELS_ENDPOINT_NAME)
    private MockEndpoint cancelsEndpoint;

    @EndpointInject(uri = MOCK_MODIFICATIONS_ENDPOINT_NAME)
    private MockEndpoint modificationsEndpoint;

    @EndpointInject(uri = MOCK_SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME)
    private MockEndpoint shutdownNotificationsEndpoint;

    @Before
    @Override
    public void mockEndpoints() throws Exception {
        AdviceWithRouteBuilder routingRouteBuilder = new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                interceptSendToEndpoint(CamelRouteBuilder.ORDERS_ENDPOINT_NAME)
                        .skipSendToOriginalEndpoint()
                        .to(MOCK_ORDERS_ENDPOINT_NAME);
                interceptSendToEndpoint(CamelRouteBuilder.MODIFICATIONS_ENDPOINT_NAME)
                        .skipSendToOriginalEndpoint()
                        .to(MOCK_MODIFICATIONS_ENDPOINT_NAME);
                interceptSendToEndpoint(CamelRouteBuilder.CANCELS_ENDPOINT_NAME)
                        .skipSendToOriginalEndpoint()
                        .to(MOCK_CANCELS_ENDPOINT_NAME);
                interceptSendToEndpoint(CamelRouteBuilder.SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME)
                        .skipSendToOriginalEndpoint()
                        .to(MOCK_SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME);
                interceptSendToEndpoint(CamelRouteBuilder.SCHEDULING_TASKS_ENDPOINT_NAME)
                        .skipSendToOriginalEndpoint()
                        .to(IGNORE_MESSAGE_ENDPOINT);
            }
        };
        context.getRouteDefinition(CamelRouteBuilder.DYNAMIC_ROUTING_ROUTE_ID)
                .adviceWith(context, routingRouteBuilder);
        context.start();
    }


    @Test
    public void dynamicRouting_whenPassedOrderJson_shouldRouteToOrdersEndpoint() throws Exception {
        ordersEndpoint.setExpectedCount(1);
        try {
            template.sendBody(resourceLoader.readStringFromResourceFile(ORDER_JSON_FILENAME));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        ordersEndpoint.assertIsSatisfied();
    }

    @Test
    public void contentBasedRouting_whenPassedModificationJson_shouldRouteToModificationsEndpoint() throws Exception {
        modificationsEndpoint.setExpectedCount(1);
        try {
            template.sendBody(resourceLoader.readStringFromResourceFile(MODIFICATION_JSON_FILENAME));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        modificationsEndpoint.assertIsSatisfied();
    }

    @Test
    public void contentBasedRouting_whenPassedCancelJson_shouldRouteToCancelsEndpoint() throws Exception {
        cancelsEndpoint.setExpectedCount(1);
        try {
            template.sendBody(resourceLoader.readStringFromResourceFile(CANCEL_JSON_FILENAME));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        cancelsEndpoint.assertIsSatisfied();
    }

    @Test
    public void contentBasedRouting_whenPassedShutdownNotificationJson_shouldRouteToShutdownNotificationsEndpoint()
            throws Exception {
        shutdownNotificationsEndpoint.setExpectedCount(1);
        try {
            template.sendBody(resourceLoader.readStringFromResourceFile(SHUTDOWN_NOTIFICATION_JSON_FILENAME));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        shutdownNotificationsEndpoint.assertIsSatisfied();
    }


}
