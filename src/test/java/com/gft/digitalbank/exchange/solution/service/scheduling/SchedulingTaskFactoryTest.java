package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 13/08/16.
 */
@Category(UnitTest.class)
public class SchedulingTaskFactoryTest {

    @Inject
    private SchedulingTaskFactory<Order> orderSchedulingTaskFactory;

    @Inject
    private SchedulingTaskFactory<Modification> modificationSchedulingTaskFactory;

    @Inject
    private SchedulingTaskFactory<Cancel> cancelSchedulingTaskFactory;

    private ProcessingTask processingTask;
    private TradingMessage tradingMessage;

    @Before
    public void initialize() {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        injector.injectMembers(this);
        processingTask = Mockito.mock(ProcessingTask.class);
        tradingMessage = Mockito.mock(TradingMessage.class);
        when(processingTask.getTradingMessage()).thenReturn(tradingMessage);
    }

    @Test
    public void create_whenOfTypedAsOrder_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        SchedulingTask<Order> schedulingTask = orderSchedulingTaskFactory.create(processingTask);
        assertThat(schedulingTask.getTradingMessage(), is(sameInstance(tradingMessage)));
    }

    @Test
    public void create_whenOfTypedAsModification_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        SchedulingTask<Modification> schedulingTask = modificationSchedulingTaskFactory.create(processingTask);
        assertThat(schedulingTask.getTradingMessage(), is(sameInstance(tradingMessage)));
    }

    @Test
    public void create_whenOfTypedAsCancel_shouldCreateTypedSchedulingTaskEncapsulatingTheMessage() {
        SchedulingTask<Cancel> schedulingTask = cancelSchedulingTaskFactory.create(processingTask);
        assertThat(schedulingTask.getTradingMessage(), is(sameInstance(tradingMessage)));
    }

    @Test(expected = ProvisionException.class)
    public void create_whenOfTypedAsOrderAndPassedNull_shouldThrowProvisionException() {
        orderSchedulingTaskFactory.create(null);
    }

    @Test(expected = ProvisionException.class)
    public void create_whenOfTypedAsModificationAndPassedNull_shouldThrowProvisionException() {
        modificationSchedulingTaskFactory.create(null);
    }

    @Test(expected = ProvisionException.class)
    public void create_whenOfTypedAsCancelAndPassedNull_shouldThrowProvisionException() {
        cancelSchedulingTaskFactory.create(null);
    }
}