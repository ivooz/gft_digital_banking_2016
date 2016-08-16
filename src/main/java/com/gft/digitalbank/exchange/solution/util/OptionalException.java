package com.gft.digitalbank.exchange.solution.util;

import lombok.NonNull;

import java.util.Optional;

/**
 * The sole purpose of this class is to preserve the type information for the encapsulated Optionals for exceptions returned
 * in lambdas' catch block.
 * <p>
 * Created by Ivo Zieliński on 2016-08-02.
 */
public class OptionalException {

    private Optional<Exception> exception;

    private OptionalException(Optional<Exception> exception) {
        this.exception = exception;
    }

    /**
     * @return an empty Optional
     */
    public static OptionalException empty() {
        return new OptionalException(Optional.empty());
    }

    /**
     * @param exception to be wrapped
     * @return Exception wrapped in the Optional
     */
    public static OptionalException of(@NonNull Exception exception) {
        return new OptionalException(Optional.of(exception));
    }

    /**
     * @return true if the encapsulate Optional is empty
     */
    public boolean isPresent() {
        return exception.isPresent();
    }

    /**
     * @return the wrapped Exception
     */
    public Exception get() {
        return exception.get();
    }
}
