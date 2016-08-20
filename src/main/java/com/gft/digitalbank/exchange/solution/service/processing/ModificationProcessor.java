package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.Optional;

/**
 * Defines how a Modification message is applied to the ProductExchange.
 *
 * Created by Ivo Zieliński on 2016-06-30.
 */
@Singleton
public class ModificationProcessor implements TradingMessageProcessor<Modification> {

    private final TradingMessageProcessor<Order> orderTradingMessageProcessor;

    @Inject
    public ModificationProcessor(TradingMessageProcessor<Order> orderTradingMessageProcessor) {
        this.orderTradingMessageProcessor = orderTradingMessageProcessor;
    }

    /**
     * {@inheritDoc}
     * Retrieves the Order to modify from the ProductExchange cache, if it is not there it means that it has already been
     * fully processed or cancelled.
     * Copy is created and the old Order is removed from ProductExchange.
     * The modified Order os then processed like a newly incoming Order by the OrderProcessor.
     */
    @Override
    public void processTradingMessage(@NonNull Modification modification, @NonNull ProductExchange productExchange) {
        Optional<Order> orderToModify = productExchange.getById(modification.getModifiedOrderId());
        if (!orderToModify.isPresent()) {
            return;
        }
        Order order = orderToModify.get();
        if (!modification.getBroker().equals(order.getBroker())) {
            return;
        }
        Order copy = new Order(order,modification.getTimestamp());
        copy.setDetails(modification.getDetails());
        productExchange.remove(order);
        orderTradingMessageProcessor.processTradingMessage(copy, productExchange);
    }
}
