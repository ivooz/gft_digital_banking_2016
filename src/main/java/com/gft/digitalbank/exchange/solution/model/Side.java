package com.gft.digitalbank.exchange.solution.model;

/**
 * Represents the Order transaction type.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
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

    /**
     * @return the opposite Side
     */
    public abstract Side opposite();
}
