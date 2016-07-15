package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.CancelProcessingTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class CancelSchedulingTaskFactory {

    @Inject
    private ProductExchangeIndex productMessageQueuesHolder;

    @Inject
    private ExecutionTaskScheduler executionTaskScheduler;

    @Inject
    private IdProductIndex idProductIndex;

    @Inject
    private CancelProcessingTaskFactory cancelSchedulingTaskFactory;

    public CancelSchedulingTask createCancelTask(Cancel cancel) {
        return new CancelSchedulingTask(productMessageQueuesHolder, executionTaskScheduler, idProductIndex,
                cancelSchedulingTaskFactory.createCancelTask(cancel));
    }
}
