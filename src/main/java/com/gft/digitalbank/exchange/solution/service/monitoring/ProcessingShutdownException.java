package com.gft.digitalbank.exchange.solution.service.monitoring;

/**
 * Thrown whenever a problem occurs during the shutdown procedure.
 * <p>
 * Created by Ivo Zieliński on 2016-08-10.
 */
public class ProcessingShutdownException extends Exception {

    public ProcessingShutdownException(String message, Throwable cause) {
        super(message, cause);
    }
}
