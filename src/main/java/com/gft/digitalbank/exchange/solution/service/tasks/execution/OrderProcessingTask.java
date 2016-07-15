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
    private ProductExchange productExchange;

    public OrderProcessingTask(OrderExecutionTaskProcessor orderTaskProcessor, Order order) {
        this.orderTaskProcessor = orderTaskProcessor;
        this.order = order;
    }

    @Override
    public void run() {
        orderTaskProcessor.processOrder(order, productExchange);
    }

    @Override
    public void setProductExchange(ProductExchange productExchange) {
        this.productExchange = productExchange;
    }

    @Override
    public TradingMessage getTradingMessage() {
        return order;
    }

    public Order getOrder() {
        return order;
    }
}
