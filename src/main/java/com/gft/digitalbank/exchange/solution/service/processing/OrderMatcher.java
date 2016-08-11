package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.Optional;

/**
 * Defines the logic of matching an incoming Order message with those residing in OrderQueues.
 * <p>
 * Created by Ivo Zieliński on 2016-07-05.
 */
@Singleton
public class OrderMatcher {

    private static final String SAME_ORDER_SIDE_EXCEPTION_MESSAGE = "Attempted to matched orders of the same type.";

    /**
     * Applies an incoming Order message to the ProductExchange. Creates Transactions as long as there is a matching
     * Order in the ProductExchange queues. If no matching Order is found it is enqueued. All the 'fully traded' Orders
     * extracted from the ProductExchange queues are removed.
     *
     * @param processedOrder  the new Order that handled
     * @param productExchange to apply the Order message against
     * @throws OrderProcessingException
     */
    public void matchOrder(@NonNull Order processedOrder, @NonNull ProductExchange productExchange) throws OrderProcessingException {
        Side processedOrderSide = processedOrder.getSide();
        Side passiveOrderSide = processedOrderSide.opposite();

        while (!processedOrder.isFullyProcessed()) {

            Optional<Order> passiveOrderOptional = productExchange.peekNextOrder(passiveOrderSide);

            if (!passiveOrderOptional.isPresent()) {
                productExchange.enqueue(processedOrder);
                return;
            }

            Order passiveOrder = passiveOrderOptional.get();

            if (ordersMatch(processedOrder, passiveOrder)) {
                productExchange.executeTransaction(processedOrder, passiveOrder);
            } else {
                productExchange.enqueue(processedOrder);
                return;
            }
        }
    }

    private boolean ordersMatch(Order processedOrder, Order matchedOrder) throws OrderProcessingException {
        if (processedOrder.getSide() == matchedOrder.getSide()) {
            throw new OrderProcessingException(SAME_ORDER_SIDE_EXCEPTION_MESSAGE);
        }
        int comparison = processedOrder.getPrice() - matchedOrder.getPrice();
        return processedOrder.getSide() == Side.BUY ? comparison >= 0 : comparison <= 0;
    }
}
