package com.gft.digitalbank.exchange.solution.model;

/**
 * Represents the Order type.
 *
 * Created by iozi on 2016-06-27.
 */
public enum Side {

    BUY {
        @Override
        public Side opposite() {
            return SELL;
        }
    }, SELL {
        @Override
        public Side opposite() {
            return BUY;
        }
    };

    public abstract Side opposite();
}
