package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.OrderExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;

/**
 * Created by iozi on 2016-06-28.
 */
public class OrderProcessingTask implements ProcessingTask {

    private final OrderExecutionTaskProcessor orderTaskProcessor;
    private final Order order;


    public OrderProcessingTask(OrderExecutionTaskProcessor orderTaskProcessor, Order order) {
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
