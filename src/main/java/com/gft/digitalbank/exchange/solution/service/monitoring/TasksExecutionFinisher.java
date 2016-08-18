package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.solution.service.exchange.ExchangeShutdownException;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.util.ExceptionOptional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for finishing the execution of outstanding ProcessingTasks.
 * <p>
 * Created by Ivo Zieliński on 2016-07-01.
 */
@Singleton
public class TasksExecutionFinisher {

    private final ProductExchangeIndex productExchangeIndex;

    @Inject
    public TasksExecutionFinisher(ProductExchangeIndex productExchangeIndex) {
        this.productExchangeIndex = productExchangeIndex;
    }

    /**
     * Shuts down ExecutorServices and initiates the processing of all the outstanding ProcessingTasks
     * from the ProductExchange ProcessingTasksQueues.
     *
     * @return list of exceptions that occurred during the execution
     */
    public List<Exception> finishAllTasks() {
        return productExchangeIndex.getAllExchanges().parallelStream()
                .map(productExchange -> {
                    try {
                        productExchange.executeRemainingTasksAndShutDown();
                        return ExceptionOptional.empty();
                    } catch (ExchangeShutdownException ex) {
                        return ExceptionOptional.of(ex);
                    }
                }).filter(ExceptionOptional::isPresent)
                .map(ExceptionOptional::get)
                .collect(Collectors.toList());
    }
}
