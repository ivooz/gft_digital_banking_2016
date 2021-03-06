package com.gft.digitalbank.exchange.solution.util;

import lombok.NonNull;

import java.util.Optional;

/**
 * The sole purpose of this class is to preserve the type information for the encapsulated Optionals for exceptions returned
 * in lambdas' catch block.
 * <p>
 * Created by Ivo Zieliński on 2016-08-02.
 */
public class ExceptionOptional {

    private Optional<Exception> exception;

    private ExceptionOptional(Optional<Exception> exception) {
        this.exception = exception;
    }

    /**
     * @return an empty Optional
     */
    public static ExceptionOptional empty() {
        return new ExceptionOptional(Optional.empty());
    }

    /**
     * @param exception to be wrapped
     * @return Exception wrapped in the Optional
     */
    public static ExceptionOptional of(@NonNull Exception exception) {
        return new ExceptionOptional(Optional.of(exception));
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
