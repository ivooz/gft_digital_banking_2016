package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class CancelExecutionTaskProcessor implements TradingMessageProcessor<Cancel> {

    @Override
    public void processTradingMessage(Cancel processedCancel, ProductExchange productExchange) {
        int cancelledOrderId = processedCancel.getCancelledOrderId();
        //TODO, check brokers!
        Optional<Order> orderToCancel = productExchange.getById(cancelledOrderId);
        if(!orderToCancel.isPresent()) {
            //The Order has already been cancelled or fully processed
            return;
        }
        Order order = orderToCancel.get();
        if(!processedCancel.getBroker().equals(order.getBroker())) {
            return;
        }
        productExchange.remove(order);
    }
}
