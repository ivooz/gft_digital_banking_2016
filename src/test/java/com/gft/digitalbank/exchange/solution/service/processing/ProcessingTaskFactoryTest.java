package com.gft.digitalbank.exchange.solution.service.processing;

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
public class ProcessingTaskFactoryTest {

    @Inject
    private ProcessingTaskFactory<Order> orderProcessingTaskFactory;

    @Inject
    private ProcessingTaskFactory<Modification> modificationProcessingTaskFactory;

    @Inject
    private ProcessingTaskFactory<Cancel> cancelProcessingTaskFactory;

    @Before
    public void initialize() {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        injector.injectMembers(this);
    }

    @Test
    public void create_whenOfTypedAsOrder_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        Order order = Mockito.mock(Order.class);
        ProcessingTask<Order> processingTask = orderProcessingTaskFactory.createProcessingTask(order);
        assertThat(processingTask.getTradingMessage(), is(sameInstance(order)));
    }

    @Test
    public void create_whenOfTypedAsModification_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        Modification modification = Mockito.mock(Modification.class);
        ProcessingTask<Modification> processingTask = modificationProcessingTaskFactory.createProcessingTask(modification);
        assertThat(processingTask.getTradingMessage(), is(sameInstance(modification)));
    }

    @Test
    public void create_whenOfTypedAsCancel_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        Cancel cancel = Mockito.mock(Cancel.class);
        ProcessingTask<Cancel> processingTask = cancelProcessingTaskFactory.createProcessingTask(cancel);
        assertThat(processingTask.getTradingMessage(), is(sameInstance(cancel)));
    }

    @Test(expected = NullPointerException.class)
    public void create_whenOfTypedAsOrderAndPassedNull_shouldThrowNullPointerException() {
        orderProcessingTaskFactory.createProcessingTask(null);
    }

    @Test(expected = NullPointerException.class)
    public void create_whenOfTypedAsModificationAndPassedNull_shouldThrowNullPointerException() {
        modificationProcessingTaskFactory.createProcessingTask(null);
    }

    @Test(expected = NullPointerException.class)
    public void create_whenOfTypedAsCancelAndPassedNull_shouldThrowNullPointerException() {
        cancelProcessingTaskFactory.createProcessingTask(null);
    }
}