package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class ModificationExecutionTaskProcessor implements TradingMessageProcessor<Modification> {

    private final OrderMatcher orderMatcher;

    @Inject
    public ModificationExecutionTaskProcessor(OrderMatcher orderMatcher) {
        this.orderMatcher = orderMatcher;
    }

    @Override
    public void processTradingMessage(Modification modification, ProductExchange productExchange) throws OrderProcessingException {
        Optional<Order> orderToModify = productExchange.getById(modification.getModifiedOrderId());
        if(!orderToModify.isPresent()) {
            //The order has already been fully processed or cancelled
            return;
        }
        Order order = orderToModify.get();
        if(!modification.getBroker().equals(order.getBroker())) {
            return;
        }
        Order copy = new Order(order);
        copy.setDetails(modification.getDetails());
        productExchange.remove(order);
        copy.setTimestamp(modification.getTimestamp());
        orderMatcher.matchOrder(copy, productExchange);
    }
}
