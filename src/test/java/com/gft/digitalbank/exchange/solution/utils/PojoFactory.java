package com.gft.digitalbank.exchange.solution.utils;

import com.gft.digitalbank.exchange.solution.model.Side;

/**
 * Created by iozi on 2016-08-19.
 */
public abstract class PojoFactory {

    public static final int MODIFIED_ORDER_ID = 5;
    public static final Side DEFAULT_SIDE = Side.BUY;
    public static final int DEFAULT_ID = 0;
    public static final String DEFAULT_BROKER = "broker";
    public static final String DEFAULT_CLIENT = "client";
    public static final int DEFAULT_TIMESTAMP = 0;
    public static final String DEFAULT_PRODUCT = "product";
    public static final boolean DEFAULT_SCHEDULED_FOR_DELETION = false;
    public static final int DEFAULT_MODIFIED_ORDER_ID = 1;
    public static final String DEFAULT_BROKER_SELL = "brokerSell";
    public static final String DEFAULT_CLIENT_BUY = "clientBuy";
    public static final String DEFAULT_CLIENT_SELL = "clientSell";
    public static final int DEFAULT_AMOUNT = 1;
    public static final int DEFAULT_PRICE = 1;

    protected int counter;
}
