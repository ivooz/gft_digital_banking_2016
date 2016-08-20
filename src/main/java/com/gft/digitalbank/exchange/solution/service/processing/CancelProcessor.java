package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.Optional;

/**
 * Defines how a Cancel message is applied to the ProductExchange.
 *
 * Created by Ivo Zieliński on 2016-07-15.
 */
@Singleton
public class CancelProcessor implements TradingMessageProcessor<Cancel> {

    /**
     * {@inheritDoc}
     * Retrieves the Order to modify from the ProductExchange cache, if it is not there it means that it has already been
     * fully processed or cancelled.
     * The retrieved Order is removed from ProductExchange.
     */
    @Override
    public void processTradingMessage(@NonNull Cancel cancel, @NonNull ProductExchange productExchange) {
        Optional<Order> orderToCancel = productExchange.getById(cancel.getCancelledOrderId());
        if (!orderToCancel.isPresent()) {
            return;
        }
        Order order = orderToCancel.get();
        if (!cancel.getBroker().equals(order.getBroker())) {
            return;
        }
        productExchange.remove(order);
    }
}
