package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.OrderExecutionTaskProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class OrderProcessingTaskFactory {

    @Inject
    private OrderExecutionTaskProcessor orderTaskProcessor;

    public OrderProcessingTask createOrderTask(Order order) {
        return new OrderProcessingTask(orderTaskProcessor, order);
    }
}
