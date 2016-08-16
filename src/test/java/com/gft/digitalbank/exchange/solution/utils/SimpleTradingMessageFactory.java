package com.gft.digitalbank.exchange.solution.utils;

import com.gft.digitalbank.exchange.solution.model.Details;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;

/**
 * Created by iozi on 2016-08-11.
 */
public class SimpleTradingMessageFactory {

    public Order getSimpleOrder() {
        return Order.builder().details(getSimpleDetails()).id(1)
                .broker("broker")
                .side(Side.BUY)
                .timestamp(1)
                .client("client")
                .product("product")
                .scheduledForDeletion(false)
                .build();
    }

    public Details getSimpleDetails() {
        return Details.builder().price(1).amount(1).build();
    }
}
