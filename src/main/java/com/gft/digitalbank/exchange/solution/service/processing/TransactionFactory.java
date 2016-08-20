package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import lombok.NonNull;

/**
 * Responsible from creating a Transaction object resulting from matching two Orders.
 * <p>
 * Created by Ivo Zieliński on 2016-06-30.
 */
public class TransactionFactory {

    /**
     * Creates a Transaction from two Orders. Orders are not modified.
     *
     * @param processedOrder that is being handled
     * @param orderFromQueue that was matched
     * @param amountTraded   of the product
     * @param id             of the Transaction
     * @return the resulting Transaction
     */
    public Transaction createTransaction(@NonNull Order processedOrder, @NonNull Order orderFromQueue,
                                         int amountTraded, int id) {
        boolean processedOrderIsBuying = processedOrder.getSide() == Side.BUY;
        return Transaction.builder().amount(amountTraded)
                .brokerBuy(processedOrderIsBuying ? processedOrder.getBroker() : orderFromQueue.getBroker())
                .brokerSell(processedOrderIsBuying ? orderFromQueue.getBroker() : processedOrder.getBroker())
                .clientBuy(processedOrderIsBuying ? processedOrder.getClient() : orderFromQueue.getClient())
                .clientSell(processedOrderIsBuying ? orderFromQueue.getClient() : processedOrder.getClient())
                .product(processedOrder.getProduct())
                .price(orderFromQueue.getPrice())
                .id(id).build();
    }
}
