package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;

/**
 * Created by iozi on 2016-06-30.
 */
public class TransactionFactory {

    public Transaction createTransaction(Order processedOrder, Order passiveOrder, int amountTraded, int id) {
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
