package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedgerIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.OrderExecutionTask;

/**
 * Created by iozi on 2016-06-28.
 */
public class OrderSchedulingTask implements SchedulingTask,Runnable {

    private final ProductLedgerIndex productLedgerIndex;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final OrderExecutionTask orderExecutionTask;

    public OrderSchedulingTask(ProductLedgerIndex productMessageQueuesHolder,
                               IdProductIndex idProductIndex,
                               ExecutionTaskScheduler executionTaskScheduler,
                               OrderExecutionTask orderExecutionTask) {
        this.productLedgerIndex = productMessageQueuesHolder;
        this.idProductIndex = idProductIndex;
        this.executionTaskScheduler = executionTaskScheduler;
        this.orderExecutionTask = orderExecutionTask;
    }

    @Override
    public void run() {
        Order order = orderExecutionTask.getOrder();
        idProductIndex.put(order.getId(), order.getProduct());
        ProductLedger productLedger = productLedgerIndex.getLedger(order.getProduct());
        synchronized (productLedger) {
            executionTaskScheduler.scheduleExecutionTask(orderExecutionTask, productLedger);
        }
    }

    @Override
    public TradingMessage getTradingMessage() {
        return orderExecutionTask.getTradingMessage();
    }
}
