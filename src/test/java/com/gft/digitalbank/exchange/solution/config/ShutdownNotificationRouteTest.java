package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.utils.ResourceLoader;
import org.junit.experimental.categories.Category;

/**
 * Created by Ivo on 15/08/16.
 */
@Category(UnitTest.class)
public class ShutdownNotificationRouteTest extends CamelRouteTest {
    private ResourceLoader resourceLoader = new ResourceLoader();

    @Override
    public void mockEndpoints() throws Exception {

    }

//    @Produce(uri = CamelRouteBuilder.SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME)
//    private ProducerTemplate template;
//
//    @EndpointInject(uri = MOCK_SCHEDULING_TASKS_ENDPOINT_NAME)
//    private MockEndpoint schedulingTasksEndpoint;
//
//    @Before
//    @Override
//    public void mockEndpoints() throws Exception {
//        AdviceWithRouteBuilder cancelRouteBuilder = new AdviceWithRouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                interceptSendToEndpoint(CamelRouteBuilder.SCHEDULING_TASKS_ENDPOINT_NAME)
//                        .skipSendToOriginalEndpoint()
//                        .to(MOCK_SCHEDULING_TASKS_ENDPOINT_NAME);
//            }
//        };
//        context.getRouteDefinition(CamelRouteBuilder.SHUTDOWN_ROUTE_ID)
//                .adviceWith(context, cancelRouteBuilder);
//        context.start();
//    }
//
//    @Test
//    public void shutdownNotification_whenPassedShutdownNotificationJson_shouldCallShutdownNotificationListener() throws Exception {
//        schedulingTasksEndpoint.setExpectedCount(1);
//        try {
//            template.sendBody(resourceLoader.readStringFromResourceFile("shutdownNotification.json"));
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//        schedulingTasksEndpoint.assertIsSatisfied();
//        Mockito.verify(shutdownNotificationListener,times(1)).handleShutdownNotification();
//        context.stop();
//    }
//
//    @Override
//    protected RouteBuilder createRouteBuilder() throws Exception {
//        return super.createRouteBuilder();
//    }
}
