package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.OrderProcessingTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class OrderSchedulingTaskFactory {

    @Inject
    private ProductExchangeIndex productExchangeIndex;

    @Inject
    private ExecutionTaskScheduler executionTaskScheduler;

    @Inject
    private IdProductIndex idProductIndex;

    @Inject
    private OrderProcessingTaskFactory orderProcessingTaskFactory;

    public OrderSchedulingTask createOrderTask(Order order) {
        return new OrderSchedulingTask(productExchangeIndex, idProductIndex, executionTaskScheduler,
                orderProcessingTaskFactory.createOrderTask(order));
    }
}
