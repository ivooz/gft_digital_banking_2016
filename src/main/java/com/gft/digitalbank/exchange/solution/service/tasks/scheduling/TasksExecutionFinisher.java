package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.service.processing.ProductExchangeIndex;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-07-01.
 */
@Singleton
public class TasksExecutionFinisher {

    @Inject
    private ProductExchangeIndex productExchangeIndex;

    public void finishAllTasks() {
        productExchangeIndex.getAllExchanges().parallelStream()
                .forEach(productExchange -> productExchange.executeRemainingTasks());
    }
}
