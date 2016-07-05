package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.service.processing.CancelExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class CancelExecutionTaskFactory {

    @Inject
    CancelExecutionTaskProcessor cancelTaskProcessor;

    @Inject
    private IdProductIndex idProductIndex;

    public CancelExecutionTask createCancelTask(Cancel cancel) {
        return new CancelExecutionTask(cancelTaskProcessor, cancel);
    }
}
