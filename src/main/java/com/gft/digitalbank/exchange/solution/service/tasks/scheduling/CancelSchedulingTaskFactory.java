package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.service.events.ExchangeEventBus;
import com.gft.digitalbank.exchange.solution.service.events.OrderNotFoundMessageBroker;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedgerIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.CancelExecutionTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class CancelSchedulingTaskFactory {

    @Inject
    private ProductLedgerIndex productMessageQueuesHolder;

    @Inject
    private ExecutionTaskScheduler executionTaskScheduler;

    @Inject
    private IdProductIndex idProductIndex;

    @Inject
    private CancelExecutionTaskFactory cancelSchedulingTaskFactory;

    @Inject
    private OrderNotFoundMessageBroker orderNotFoundMessageBroker;

    public CancelSchedulingTask createCancelTask(Cancel cancel) {
        return new CancelSchedulingTask(productMessageQueuesHolder, executionTaskScheduler, idProductIndex, orderNotFoundMessageBroker,
                cancelSchedulingTaskFactory.createCancelTask(cancel));
    }
}
