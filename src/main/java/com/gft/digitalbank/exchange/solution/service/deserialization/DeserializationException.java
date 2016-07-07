package com.gft.digitalbank.exchange.solution.service.deserialization;

/**
 * Created by iozi on 2016-06-28.
 */
public class DeserializationException extends Throwable {

    public DeserializationException() {
        super();
    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeserializationException(Throwable cause) {
        super(cause);
    }
}
