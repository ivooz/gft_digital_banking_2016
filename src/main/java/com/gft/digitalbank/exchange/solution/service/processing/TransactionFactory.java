package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.google.common.base.Preconditions;
import lombok.NonNull;

/**
 * Responsible from creating a Transaction object resulting from matching two Orders.
 * <p>
 * Created by Ivo Zieliński on 2016-06-30.
 */
public class TransactionFactory {

    public Transaction createTransaction(@NonNull Order processedOrder, @NonNull Order passiveOrder, int amountTraded, int id) {
        Preconditions.checkState(processedOrder.getSide() != passiveOrder.getSide(),
                "Cannot transact Orders of the same type!");
        boolean processedOrderIsBuying = processedOrder.getSide() == Side.BUY;
        return Transaction.builder().amount(amountTraded)
                .brokerBuy(processedOrderIsBuying ? processedOrder.getBroker() : passiveOrder.getBroker())
                .brokerSell(processedOrderIsBuying ? passiveOrder.getBroker() : processedOrder.getBroker())
                .clientBuy(processedOrderIsBuying ? processedOrder.getClient() : passiveOrder.getClient())
                .clientSell(processedOrderIsBuying ? passiveOrder.getClient() : processedOrder.getClient())
                .product(processedOrder.getProduct())
                .price(passiveOrder.getPrice())
                .id(id).build();
    }
}
