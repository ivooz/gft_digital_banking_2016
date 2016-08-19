package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.Optional;

/**
 * @inheritDoc Created by Ivo Zieliński on 2016-06-30.
 */
@Singleton
public class ModificationProcessor implements TradingMessageProcessor<Modification> {

    private final TradingMessageProcessor<Order> orderTradingMessageProcessor;

    @Inject
    public ModificationProcessor(TradingMessageProcessor<Order> orderTradingMessageProcessor) {
        this.orderTradingMessageProcessor = orderTradingMessageProcessor;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void processTradingMessage(@NonNull Modification modification, @NonNull ProductExchange productExchange) {
        Optional<Order> orderToModify = productExchange.getById(modification.getModifiedOrderId());
        if (!orderToModify.isPresent()) {
            //The order has already been fully processed or cancelled
            return;
        }
        Order order = orderToModify.get();
        if (!modification.getBroker().equals(order.getBroker())) {
            return;
        }
        Order copy = new Order(order,modification.getTimestamp());
        copy.setDetails(modification.getDetails());
        //Copy is created and the old Order is eagerly removed from the cache so that we don't have to search OrderQueues
        //for it. It will be lazily removed during retrieval.
        productExchange.remove(order);
        //We treat the modified Order just like an incoming new Order.
        orderTradingMessageProcessor.processTradingMessage(copy, productExchange);
    }
}
