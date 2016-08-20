package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.scheduling.ModificationSchedulingTask;
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;

/**
 * Created by Ivo on 15/08/16.
 */
@Category(UnitTest.class)
public class ModificationRouteTest extends CamelRouteTest {

    private static final String MODIFICATION_JSON_FILENAME = "modification.json";
    
    private ResourceLoader resourceLoader = new ResourceLoader();

    @Produce(uri = CamelRouteBuilder.MODIFICATIONS_ENDPOINT_NAME)
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
        context.getRouteDefinition(CamelRouteBuilder.MODIFICATIONS_ROUTE_ID)
                .adviceWith(context, cancelRouteBuilder);
        context.start();
    }

    @Test
    public void modification_whenPassedModificationJson_shouldDeserializeAndCreateModificationSchedulingTask() throws Exception {
        schedulingTasksEndpoint.setExpectedCount(1);
        try {
            template.sendBody(resourceLoader.readStringFromResourceFile(MODIFICATION_JSON_FILENAME));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        schedulingTasksEndpoint.assertIsSatisfied();
        Object bodyReceived = schedulingTasksEndpoint.getExchanges().get(0).getIn().getBody();
        assertThat(bodyReceived, is(instanceOf(ModificationSchedulingTask.class)));
        context.stop();
    }
}
