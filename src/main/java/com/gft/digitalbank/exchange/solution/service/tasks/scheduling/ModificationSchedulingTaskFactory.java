package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ModificationProcessingTaskFactory;
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
    private ModificationProcessingTaskFactory modificationProcessingTaskFactory;

    public ModificationSchedulingTask createModificationTask(Modification modification) {
        return new ModificationSchedulingTask(productExchangeIndex, idProductIndex, executionTaskScheduler,
                modificationProcessingTaskFactory.createModificationTask(modification));
    }
}
