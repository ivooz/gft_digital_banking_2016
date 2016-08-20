package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;

/**
 * Created by Ivo on 13/08/16.
 */
@Category(UnitTest.class)
public class SchedulingTaskCreatorTest {

    @Inject
    private SchedulingTaskCreator<Order> orderSchedulingTaskCreator;

    @Inject
    private SchedulingTaskCreator<Modification> modificationSchedulingTaskCreator;

    @Inject
    private SchedulingTaskCreator<Cancel> cancelSchedulingTaskCreator;

    @Before
    public void initialize() {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        injector.injectMembers(this);
    }

    @Test
    public void createSchedulingTask_whenOfTypedAsOrder_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        Order order = Mockito.mock(Order.class);
        SchedulingTask<Order> schedulingTask = orderSchedulingTaskCreator.createSchedulingTask(order);
        assertThat(schedulingTask.getTradingMessage(), is(sameInstance(order)));
    }

    @Test
    public void createSchedulingTask_whenOfTypedAsModification_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        Modification modification = Mockito.mock(Modification.class);
        SchedulingTask<Modification> schedulingTask = modificationSchedulingTaskCreator.createSchedulingTask(modification);
        assertThat(schedulingTask.getTradingMessage(), is(sameInstance(modification)));
    }

    @Test
    public void createSchedulingTask_whenOfTypedAsCancel_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        Cancel cancel = Mockito.mock(Cancel.class);
        SchedulingTask<Cancel> schedulingTask = cancelSchedulingTaskCreator.createSchedulingTask(cancel);
        assertThat(schedulingTask.getTradingMessage(), is(sameInstance(cancel)));
    }

    @Test(expected = NullPointerException.class)
    public void createSchedulingTask_whenOfTypedAsOrderAndPassedNull_shouldThrowNullPointerException() {
        orderSchedulingTaskCreator.createSchedulingTask(null);
    }

    @Test(expected = NullPointerException.class)
    public void createSchedulingTask_whenOfTypedAsModificationAndPassedNull_shouldThrowNullPointerException() {
        modificationSchedulingTaskCreator.createSchedulingTask(null);
    }

    @Test(expected = NullPointerException.class)
    public void createSchedulingTask_whenOfTypedAsCancelAndPassedNull_shouldThrowNullPointerException() {
        cancelSchedulingTaskCreator.createSchedulingTask(null);
    }
}