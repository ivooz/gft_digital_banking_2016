package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ProcessingTask;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-07-01.
 */
@Singleton
public class ExecutionTaskScheduler {

    public void scheduleExecutionTask(ProcessingTask processingTask, ProductExchange productExchange) {
        productExchange.addTask(processingTask);
    }
}