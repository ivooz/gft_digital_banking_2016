package com.gft.digitalbank.exchange.solution.util;

import java.util.Optional;

/**
 * Created by iozi on 2016-08-02.
 */
public class OptionalException {

    private Optional<Exception> exception;

    private OptionalException(Optional<Exception> exception) {
        this.exception = exception;
    }

    public static OptionalException empty() {
        return new OptionalException(Optional.empty());
    }

    public static OptionalException of(Exception exception) {
        return new OptionalException(Optional.of(exception));
    }

    public boolean isPresent() {
        return exception.isPresent();
    }

    public Exception get() {
        return exception.get();
    }
}
