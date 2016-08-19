package com.gft.digitalbank.exchange.solution.utils;

import com.gft.digitalbank.exchange.model.Transaction;

/**
 * Created by iozi on 2016-08-19.
 */
public class TransactionPojoFactory extends PojoFactory {

    public Transaction createNextTransaction() {
        return Transaction.builder()
                .amount(DEFAULT_AMOUNT)
                .brokerBuy(DEFAULT_BROKER)
                .brokerSell(DEFAULT_BROKER_SELL)
                .id(++counter)
                .clientBuy(DEFAULT_CLIENT_BUY)
                .clientSell(DEFAULT_CLIENT_SELL)
                .price(DEFAULT_PRICE)
                .product(DEFAULT_PRODUCT).build();
    }
}
