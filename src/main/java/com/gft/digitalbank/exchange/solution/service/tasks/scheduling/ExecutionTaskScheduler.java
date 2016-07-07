package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.service.processing.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ExecutionTask;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-07-01.
 */
@Singleton
public class ExecutionTaskScheduler {

    public void scheduleExecutionTask(ExecutionTask executionTask, ProductExchange productExchange) {
        productExchange.addTask(executionTask);
    }
}