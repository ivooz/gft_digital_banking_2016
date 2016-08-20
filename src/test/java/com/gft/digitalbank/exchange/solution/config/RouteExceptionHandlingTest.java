package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.scheduling.OrderNotFoundException;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskExecutor;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;


/**
 * Created by Ivo on 15/08/16.
 */
@Category(UnitTest.class)
public class RouteExceptionHandlingTest extends CamelRouteTest {

    private static final String SCHEDULING_TASK_EXECUTOR = "schedulingTaskExecutor";

    private SchedulingTaskExecutor schedulingTaskExecutor = Mockito.mock(SchedulingTaskExecutor.class);
    private SchedulingTask schedulingTask;
    private int maximumRedeliveriesOnFailure;
    private int redeliveryDelayOnFailure;

    @Before
    @Override
    public void mockEndpoints() throws Exception {
        schedulingTask = Mockito.mock(SchedulingTask.class);
        doThrow(OrderNotFoundException.class).when(schedulingTaskExecutor).executeSchedulingTask(anyObject());
    }

    @Test
    public void exceptionHandling_whenSchedulingTaskExecutorThrowsException_theMessageShouldBeResubmitted() throws Exception {
        sendBody(CamelRouteBuilder.SCHEDULING_TASKS_ENDPOINT_NAME, schedulingTask);
        int expectedExecutorCalls = 1 + maximumRedeliveriesOnFailure;
        //Wait till all redeliveries complete
        Thread.sleep(expectedExecutorCalls * redeliveryDelayOnFailure);
        Mockito.verify(schedulingTaskExecutor, times(expectedExecutorCalls)).executeSchedulingTask(eq(schedulingTask));
        context.stop();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        CamelRouteBuilder routeBuilder = (CamelRouteBuilder) super.createRouteBuilder();
        Whitebox.setInternalState(routeBuilder, SCHEDULING_TASK_EXECUTOR, schedulingTaskExecutor);
        maximumRedeliveriesOnFailure = routeBuilder.getMaximumRedeliveriesOnFailure();
        redeliveryDelayOnFailure = routeBuilder.getRedeliveryDelayOnFailure();
        return routeBuilder;
    }
}
