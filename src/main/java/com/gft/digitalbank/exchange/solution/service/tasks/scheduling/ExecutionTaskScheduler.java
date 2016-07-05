package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ExecutionTask;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-07-01.
 */
@Singleton
public class ExecutionTaskScheduler {

    private static final int maxExecutionTaskQueueSize = 5;

    public void scheduleExecutionTask(ExecutionTask executionTask, ProductLedger productLedger) {
        productLedger.addTask(executionTask);
        if (productLedger.getTaskToExecuteCount() == maxExecutionTaskQueueSize) {
            Optional<ExecutionTask> nextTaskToExecute = productLedger.getNextTaskToExecute();
            if(!nextTaskToExecute.isPresent()) {
                return;
            }
            nextTaskToExecute.get().execute(productLedger);
        }
    }
}