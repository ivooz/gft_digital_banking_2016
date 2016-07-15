package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.OrderProcessingTask;

/**
 * Created by iozi on 2016-06-28.
 */
public class OrderSchedulingTask implements SchedulingTask {

    private final ProductExchangeIndex productExchangeIndex;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final OrderProcessingTask orderProcessingTask;

    public OrderSchedulingTask(ProductExchangeIndex productMessageQueuesHolder,
                               IdProductIndex idProductIndex,
                               ExecutionTaskScheduler executionTaskScheduler,
                               OrderProcessingTask orderProcessingTask) {
        this.productExchangeIndex = productMessageQueuesHolder;
        this.idProductIndex = idProductIndex;
        this.executionTaskScheduler = executionTaskScheduler;
        this.orderProcessingTask = orderProcessingTask;
    }

    @Override
    public void execute() {
        Order order = orderProcessingTask.getOrder();
        idProductIndex.put(order.getId(), order.getProduct());
        ProductExchange productExchange = productExchangeIndex.getLedger(order.getProduct());
        executionTaskScheduler.scheduleExecutionTask(orderProcessingTask, productExchange);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return orderProcessingTask.getTradingMessage();
    }
}
