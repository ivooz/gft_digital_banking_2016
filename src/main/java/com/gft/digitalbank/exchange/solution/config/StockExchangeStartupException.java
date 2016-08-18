package com.gft.digitalbank.exchange.solution.config;

/**
 * Signifies StockExchange startup problems.
 * <p>
 * Created by Ivo Zieliński on 2016-08-11.
 */
public class StockExchangeStartupException extends Exception {

    public StockExchangeStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
