package com.gft.digitalbank.exchange.solution.service.monitoring;

/**
 * Thrown whenever a problem occurs during the shutdown procedure.
 *
 * Created by iozi on 2016-08-10.
 */
public class ProcessingShutdownException extends Exception {

    public ProcessingShutdownException() {
    }

    public ProcessingShutdownException(String message) {
        super(message);
    }

    public ProcessingShutdownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingShutdownException(Throwable cause) {
        super(cause);
    }
}
