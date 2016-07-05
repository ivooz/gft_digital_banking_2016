package com.gft.digitalbank.exchange.solution.service.deserialization;

/**
 * Created by iozi on 2016-06-27.
 */
public interface MessageDeserializer {

    DeserializationResult deserialize(String message) throws DeserializationException;
}
