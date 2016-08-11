package com.gft.digitalbank.exchange.solution.config;

/**
 * Signifies StockExchange startup problems.
 *
 * Created by iozi on 2016-08-11.
 */
public class StockExchangeStartupException extends Exception {

    public StockExchangeStartupException() {
    }

    public StockExchangeStartupException(String message) {
        super(message);
    }

    public StockExchangeStartupException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockExchangeStartupException(Throwable cause) {
        super(cause);
    }
}
