package com.gft.digitalbank.exchange.solution.service.deserialization;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-27.
 */
@Singleton
public class GsonMessageDeserializer implements MessageDeserializer {

    @Inject
    private MessageTypeCheckerStrategy messageTypeCheckerStrategy;

    private final Gson gson = new Gson();

    @Override
    public DeserializationResult deserialize(String message) throws DeserializationException {
        switch (messageTypeCheckerStrategy.checkMessageType(message)) {
            case ORDER:
                return DeserializationResult.builder().messageType(MessageType.ORDER)
                        .result(gson.fromJson(message, Order.class)).build();
            case MODIFICATION:
                return DeserializationResult.builder().messageType(MessageType.MODIFICATION)
                        .result(gson.fromJson(message, Modification.class)).build();
            case CANCEL:
                return DeserializationResult.builder().messageType(MessageType.CANCEL)
                        .result(gson.fromJson(message, Cancel.class)).build();
            default:
                return DeserializationResult.builder().messageType(MessageType.SHUTDOWN).build();
        }
    }
}
