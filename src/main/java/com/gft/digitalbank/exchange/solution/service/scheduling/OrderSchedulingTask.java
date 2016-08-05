package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.execution.ProcessingTask;

/**
 * Created by iozi on 2016-06-28.
 */
public class OrderSchedulingTask implements SchedulingTask<Order> {

    private final ProductExchangeIndex productExchangeIndex;
    private final IdProductIndex idProductIndex;
    private final ProcessingTask<Order> orderProcessingTask;

    public OrderSchedulingTask(ProductExchangeIndex productMessageQueuesHolder,
                               IdProductIndex idProductIndex,
                               ProcessingTask<Order> orderProcessingTask) {
        this.productExchangeIndex = productMessageQueuesHolder;
        this.idProductIndex = idProductIndex;
        this.orderProcessingTask = orderProcessingTask;
    }

    @Override
    public void execute() {
        Order order = orderProcessingTask.getTradingMessage();
        idProductIndex.put(order.getId(), order.getProduct());
        productExchangeIndex.getLedger(order.getProduct()).addTask(orderProcessingTask);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return orderProcessingTask.getTradingMessage();
    }
}
