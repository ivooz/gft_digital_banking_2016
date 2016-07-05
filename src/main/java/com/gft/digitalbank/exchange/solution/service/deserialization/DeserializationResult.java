package com.gft.digitalbank.exchange.solution.service.deserialization;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import lombok.Builder;
import lombok.Data;

/**
 * Created by iozi on 2016-06-27.
 */
@Data
@Builder
public class DeserializationResult {

    private final TradingMessage result;
    private final MessageType messageType;

    public DeserializationResult(TradingMessage result, MessageType messageType) {
        this.result = result;
        this.messageType = messageType;
    }
}
