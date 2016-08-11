package com.gft.digitalbank.exchange.solution.service.scheduling;

/**
 * Thrown whenever an Order to cancel/modify could not be located.
 *
 * Created by Ivo Zieliński on 2016-07-15.
 */
public class OrderNotFoundException extends Exception {

    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }
}
