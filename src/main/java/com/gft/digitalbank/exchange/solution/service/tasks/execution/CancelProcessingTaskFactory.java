package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.service.processing.CancelExecutionTaskProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class CancelProcessingTaskFactory {

    @Inject
    CancelExecutionTaskProcessor cancelTaskProcessor;

    public CancelProcessingTask createCancelTask(Cancel cancel) {
        return new CancelProcessingTask(cancelTaskProcessor, cancel);
    }
}
