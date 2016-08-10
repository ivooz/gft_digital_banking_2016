package com.gft.digitalbank.exchange.solution.service.scheduling;

/**
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
