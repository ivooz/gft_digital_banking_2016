package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.OrderExecutionTaskProcessor;

/**
 * Created by iozi on 2016-06-28.
 */
public class OrderExecutionTask implements ExecutionTask {

    private final OrderExecutionTaskProcessor orderTaskProcessor;
    private final Order order;


    public OrderExecutionTask(OrderExecutionTaskProcessor orderTaskProcessor, Order order) {
        this.orderTaskProcessor = orderTaskProcessor;
        this.order = order;
    }

    @Override
    public void execute(ProductExchange productExchange) {
        orderTaskProcessor.processOrder(order, productExchange);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return order;
    }

    public Order getOrder() {
        return order;
    }
}
