package com.gft.digitalbank.exchange.solution.service.processing;

/**
 * Created by iozi on 2016-08-01.
 */
public class OrderProcessingException extends Exception {

    public OrderProcessingException() {
    }

    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderProcessingException(Throwable cause) {
        super(cause);
    }
}
