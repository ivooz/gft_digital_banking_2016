package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-07-05.
 */
@Singleton
public class TransactionExecutor {

    public void matchAndClearOrder(Order processedOrder, ProductExchange productExchange) {
        Side processedOrderSide = processedOrder.getSide();
        Side passiveOrderSide = processedOrderSide.opposite();
        while (true) {
            Optional<Order> passiveOrderOptional = productExchange.peekNextOrder(passiveOrderSide);
            if (!passiveOrderOptional.isPresent()) {
                productExchange.addOrder(processedOrder);
                return;
            }
            Order passiveOrder = passiveOrderOptional.get();
            if (ordersMatch(processedOrder, passiveOrder)) {
                int processedOrderAmount = processedOrder.getAmount();
                int passiveOrderAmount = passiveOrder.getAmount();
                int amountTraded = processedOrderAmount > passiveOrderAmount ? passiveOrderAmount : processedOrderAmount;
                productExchange.addTransaction(processedOrder, passiveOrder, amountTraded);
                if (processedOrderAmount == passiveOrderAmount) {
                    productExchange.markOrderAsComplete(passiveOrder);
                    break;
                }
                int newAmountToProcess = processedOrderAmount - amountTraded;
                if (newAmountToProcess == 0) {
                    passiveOrder.getDetails().setAmount(passiveOrderAmount - amountTraded);
                    break;
                } else {
                    productExchange.markOrderAsComplete(passiveOrder);
                    processedOrder.getDetails().setAmount(newAmountToProcess);
                }
            } else {
                productExchange.addOrder(processedOrder);
                break;
            }
        }
    }

    private boolean ordersMatch(Order processedOrder, Order passiveOrder) {
        int comparison = processedOrder.getPrice() - passiveOrder.getPrice();
        return processedOrder.getSide() == Side.BUY ? comparison >= 0 : comparison <= 0;
    }
}
