package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.Optional;

/**
 * @inheritDoc Created by iozi on 2016-06-30.
 */
@Singleton
public class CancelExecutionTaskProcessor implements TradingMessageProcessor<Cancel> {

    /**
     * @inheritDoc
     */
    @Override
    public void processTradingMessage(@NonNull Cancel cancel, @NonNull ProductExchange productExchange) {
        Optional<Order> orderToCancel = productExchange.getById(cancel.getCancelledOrderId());
        if (!orderToCancel.isPresent()) {
            //The Order has already been cancelled or fully processed
            return;
        }
        Order order = orderToCancel.get();
        if (!cancel.getBroker().equals(order.getBroker())) {
            return;
        }
        productExchange.remove(order);
    }
}
