package com.gft.digitalbank.exchange.solution.service.deserialization;

import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class StringContainsMessageTypeCheckerStrategy implements MessageTypeCheckerStrategy {

    @Override
    public MessageType checkMessageType(String message) throws DeserializationException {
        if (message.toLowerCase().contains("type\":\"o")) {
            return MessageType.ORDER;
        }
        if (message.toLowerCase().contains("type\":\"m")) {
            return MessageType.MODIFICATION;
        }
        if (message.toLowerCase().contains("type\":\"c")) {
            return MessageType.CANCEL;
        }
        if (message.toLowerCase().contains("type\":\"s")) {
            return MessageType.SHUTDOWN;
        }
        throw new DeserializationException("Unable to determine message type");
    }
}
