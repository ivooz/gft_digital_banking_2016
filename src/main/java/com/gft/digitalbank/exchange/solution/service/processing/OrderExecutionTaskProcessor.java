package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;

/**
 * @inheritDoc
 * Created by Ivo Zieliński on 2016-06-30.
 */
@Singleton
public class OrderExecutionTaskProcessor implements TradingMessageProcessor<Order> {

    private final OrderMatcher orderMatcher;

    @Inject
    public OrderExecutionTaskProcessor(OrderMatcher orderMatcher) {
        this.orderMatcher = orderMatcher;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void processTradingMessage(@NonNull Order processedOrder,@NonNull ProductExchange productExchange) throws OrderProcessingException {
        orderMatcher.matchOrder(processedOrder, productExchange);
    }
}
