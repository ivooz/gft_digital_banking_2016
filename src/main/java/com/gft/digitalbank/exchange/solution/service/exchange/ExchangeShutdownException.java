package com.gft.digitalbank.exchange.solution.service.exchange;

/**
 * Thrown on encountering problems with shutting down the exchange.
 * <p>
 * Created by Ivo Zieliński on 2016-08-01.
 */
public class ExchangeShutdownException extends Exception {

    public ExchangeShutdownException() {
    }

    public ExchangeShutdownException(String message) {
        super(message);
    }

    public ExchangeShutdownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeShutdownException(Throwable cause) {
        super(cause);
    }
}
