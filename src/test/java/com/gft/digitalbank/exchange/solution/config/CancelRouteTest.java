package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.scheduling.CancelSchedulingTask;
import com.gft.digitalbank.exchange.solution.utils.ResourceLoader;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;


/**
 * Created by Ivo on 15/08/16.
 */
@Category(UnitTest.class)
public class CancelRouteTest extends CamelRouteTest {

    private ResourceLoader resourceLoader = new ResourceLoader();

    @Produce(uri = CamelRouteBuilder.CANCELS_ENDPOINT_NAME)
    private ProducerTemplate template;

    @EndpointInject(uri = MOCK_SCHEDULING_TASKS_ENDPOINT_NAME)
    private MockEndpoint schedulingTasksEndpoint;

    @Before
    @Override
    public void mockEndpoints() throws Exception {
        AdviceWithRouteBuilder cancelRouteBuilder = new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint(CamelRouteBuilder.SCHEDULING_TASKS_ENDPOINT_NAME)
                        .skipSendToOriginalEndpoint()
                        .to(MOCK_SCHEDULING_TASKS_ENDPOINT_NAME);
            }
        };
        context.getRouteDefinition(CamelRouteBuilder.CANCELS_ROUTE_ID)
                .adviceWith(context, cancelRouteBuilder);
        context.start();
    }

    @Test
    public void cancel_whenPassedCancelJson_shouldDeserializeAndCreateCancelSchedulingTask() throws Exception {
        schedulingTasksEndpoint.setExpectedCount(1);
        try {
            template.sendBody(resourceLoader.readStringFromResourceFile("cancel.json"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        schedulingTasksEndpoint.assertIsSatisfied();
        Object bodyReceived = schedulingTasksEndpoint.getExchanges().get(0).getIn().getBody();
        assertThat(bodyReceived, is(instanceOf(CancelSchedulingTask.class)));
        context.stop();
    }
}
