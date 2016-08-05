package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
        Order orderToModify = productExchange.getById(modification.getModifiedOrderId())
                .orElseThrow(() -> new NullPointerException("Unable to find order to modify!"));
        Order copy = new Order(orderToModify);
        copy.setDetails(modification.getDetails());
        productExchange.remove(orderToModify);
        copy.setTimestamp(modification.getTimestamp());
        orderMatcher.matchOrder(copy, productExchange);
    }
}
