package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.common.base.Preconditions;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-07-05.
 */
@Singleton
public class OrderMatcher {

    public void matchOrder(Order processedOrder, ProductExchange productExchange) throws OrderProcessingException {
        Preconditions.checkNotNull(productExchange,"Product exchange cannot be null!");
        Preconditions.checkNotNull(processedOrder,"Processed order cannot be null!");

        Side processedOrderSide = processedOrder.getSide();
        Side passiveOrderSide = processedOrderSide.opposite();

        while (!processedOrder.isFullyProcessed()) {

            Optional<Order> passiveOrderOptional = productExchange.peekNextOrder(passiveOrderSide);

            if (!passiveOrderOptional.isPresent()) {
                productExchange.enqueue(processedOrder);
                return;
            }

            Order passiveOrder = passiveOrderOptional.get();

            if (ordersMatch(processedOrder,passiveOrder)) {
                productExchange.executeTransaction(processedOrder, passiveOrder);
            } else {
                productExchange.enqueue(processedOrder);
                return;
            }
        }
    }

    private boolean ordersMatch(Order processedOrder, Order matchedOrder) throws OrderProcessingException {
        if(processedOrder.getSide() == matchedOrder.getSide()) {
            throw new OrderProcessingException("Attempted to matched orders of the same type.");
        }
        int comparison = processedOrder.getPrice() - matchedOrder.getPrice();
        return processedOrder.getSide() == Side.BUY ? comparison >= 0 : comparison <= 0;
    }
}
