package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.events.OrderNotFoundMessageBroker;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ModificationExecutionTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class ModificationSchedulingTaskFactory {

    @Inject
    private ProductExchangeIndex productExchangeIndex;

    @Inject
    private ExecutionTaskScheduler executionTaskScheduler;

    @Inject
    private IdProductIndex idProductIndex;

    @Inject
    private ModificationExecutionTaskFactory modificationExecutionTaskFactory;

    @Inject
    private OrderNotFoundMessageBroker orderNotFoundMessageBroker;

    public ModificationSchedulingTask createModificationTask(Modification modification) {
        return new ModificationSchedulingTask(productExchangeIndex, idProductIndex, executionTaskScheduler,
                orderNotFoundMessageBroker,modificationExecutionTaskFactory.createModificationTask(modification));
    }
}
