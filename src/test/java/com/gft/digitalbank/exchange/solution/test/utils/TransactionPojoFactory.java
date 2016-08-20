package com.gft.digitalbank.exchange.solution.test.utils;

import com.gft.digitalbank.exchange.model.Transaction;

/**
 * Created by Ivo Zieliński on 2016-08-19.
 */
public class TransactionPojoFactory extends PojoFactory<Transaction> {

    /**
     * {@inheritDoc}
     */
    public Transaction createDefault() {
        return getBuilderWithDefaultValues().build();
    }

    public Transaction createNextTransaction() {
        return getBuilderWithDefaultValues()
                .id(++counter).build();
    }

    private Transaction.TransactionBuilder getBuilderWithDefaultValues() {
        return Transaction.builder()
                .amount(DEFAULT_AMOUNT)
                .brokerBuy(DEFAULT_BROKER)
                .brokerSell(DEFAULT_BROKER_SELL)
                .id(++counter)
                .clientBuy(DEFAULT_CLIENT_BUY)
                .clientSell(DEFAULT_CLIENT_SELL)
                .price(DEFAULT_PRICE)
                .product(DEFAULT_PRODUCT);
    }
}
