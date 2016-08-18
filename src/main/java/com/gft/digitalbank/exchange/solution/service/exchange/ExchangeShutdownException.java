package com.gft.digitalbank.exchange.solution.service.exchange;

/**
 * Thrown on encountering problems with shutting down the exchange.
 * <p>
 * Created by Ivo Zieliński on 2016-08-01.
 */
public class ExchangeShutdownException extends Exception {

    public ExchangeShutdownException(String message, Throwable cause) {
        super(message, cause);
    }
}
