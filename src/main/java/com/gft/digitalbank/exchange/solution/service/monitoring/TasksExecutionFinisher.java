package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.solution.service.exchange.ExchangeShutdownException;
import com.gft.digitalbank.exchange.solution.service.processing.OrderProcessingException;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.util.OptionalException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for finishing the execution of outstanding ProcessingTasks.
 *
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
     * @return list of exceptions that occured during the execution
     */
    public List<Exception> finishAllTasks() {
        return productExchangeIndex.getAllExchanges().parallelStream()
                .map(productExchange -> {
                    try {
                        productExchange.executeRemainingTasksAndShutDown();
                        return OptionalException.empty();
                    } catch (ExchangeShutdownException | OrderProcessingException ex) {
                        return OptionalException.of(ex);
                    }
                }).filter(OptionalException::isPresent)
                .map(OptionalException::get)
                .collect(Collectors.toList());
    }
}
