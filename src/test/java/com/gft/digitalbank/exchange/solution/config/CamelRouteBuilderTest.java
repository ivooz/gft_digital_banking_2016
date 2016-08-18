package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.monitoring.ShutdownNotificationListener;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskCreator;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.lang.IllegalStateException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class CamelRouteBuilderTest {

    private static final int MAXIMUM_REDELIVERIES_ON_FAILURE = 1;
    private static final int REDELIVERY_DELAY_ON_FAILURE = 1;

    private CamelRouteBuilder sut;

    @Mock
    ShutdownNotificationListener shutdownNotificationListener;
    
    @Mock
    SchedulingTaskCreator<Order> orderSchedulingTaskCreator;

    @Mock
    SchedulingTaskCreator<Cancel> cancelSchedulingTaskCreator;

    @Mock
    SchedulingTaskCreator<Modification> modificationSchedulingTaskCreator;

    @Mock
    SchedulingTaskExecutor schedulingTaskExecutor;

    @Before
    public void initialize() {
        sut = new CamelRouteBuilder(shutdownNotificationListener,orderSchedulingTaskCreator,cancelSchedulingTaskCreator,
                modificationSchedulingTaskCreator,schedulingTaskExecutor, MAXIMUM_REDELIVERIES_ON_FAILURE,
                REDELIVERY_DELAY_ON_FAILURE);
    }


    @Test(expected = IllegalStateException.class)
    public void configure_whenDestinationsNotSet_shouldThrowIllegalStateException() throws Exception {
        sut.configure();
    }

    @Test(expected = NullPointerException.class)
    public void setDestinations_whenSetToNull_shouldThrowNullPointerException() throws Exception {
        sut.setDestinations(null);
    }

    @Test
    public void getMaximumRedeliveriesOnFailure_whenCalled_shouldReturnValuePassedToConstructor(){
        int maximumRedeliveriesOnFailure = sut.getMaximumRedeliveriesOnFailure();
        assertEquals(MAXIMUM_REDELIVERIES_ON_FAILURE,maximumRedeliveriesOnFailure);
    }

    @Test
    public void getRedeliveryDelayOnFailure_whenCalled_shouldReturnValuePassedToConstructor(){
        int redeliveryDelayOnFailure = sut.getRedeliveryDelayOnFailure();
        assertEquals(REDELIVERY_DELAY_ON_FAILURE,redeliveryDelayOnFailure);
    }
}