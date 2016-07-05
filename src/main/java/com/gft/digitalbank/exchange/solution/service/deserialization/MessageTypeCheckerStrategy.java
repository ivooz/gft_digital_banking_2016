package com.gft.digitalbank.exchange.solution.service.deserialization;

/**
 * Created by iozi on 2016-06-28.
 */
public interface MessageTypeCheckerStrategy {

    MessageType checkMessageType(String message) throws DeserializationException;
}
