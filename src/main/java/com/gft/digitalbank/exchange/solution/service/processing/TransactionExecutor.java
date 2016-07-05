package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-07-05.
 */
@Singleton
public class TransactionExecutor {

    @Inject
    TransactionFactory transactionFactory;

    public void matchAndClearOrder(Order processedOrder,ProductLedger productLedger) {
        Side processedOrderSide = processedOrder.getSide();
        Side passiveOrderSide = processedOrderSide.opposite();
        while (true) {
            Optional<Order> passiveOrderOptional = productLedger.peekNextOrder(passiveOrderSide);
            if (!passiveOrderOptional.isPresent()) {
                productLedger.addOrder(processedOrder);
                return;
            }
            Order passiveOrder = passiveOrderOptional.get();
            if (ordersMatch(processedOrder, passiveOrder)) {
                int processedOrderAmount = processedOrder.getAmount();
                int passiveOrderAmount = passiveOrder.getAmount();
                int amountTraded = processedOrderAmount > passiveOrderAmount ? passiveOrderAmount : processedOrderAmount;
                productLedger.addTransaction(transactionFactory.createTransaction(processedOrder, passiveOrder,
                        amountTraded, productLedger.getTransactionCount() + 1));
                if (processedOrderAmount == passiveOrderAmount) {
                    productLedger.markOrderAsComplete(passiveOrder);
                    break;
                }
                int newAmountToProcess = processedOrderAmount - amountTraded;
                if (newAmountToProcess == 0) {
                    passiveOrder.getDetails().setAmount(passiveOrderAmount - amountTraded);
                    break;
                } else {
                    productLedger.markOrderAsComplete(passiveOrder);
                    processedOrder.getDetails().setAmount(newAmountToProcess);
                }
            } else {
                productLedger.addOrder(processedOrder);
                break;
            }
        }
    }

    private boolean ordersMatch(Order processedOrder, Order passiveOrder) {
        int comparison = processedOrder.getPrice() - passiveOrder.getPrice();
        return processedOrder.getSide() == Side.BUY ? comparison >= 0 : comparison <= 0;
    }
}
