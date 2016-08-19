package com.gft.digitalbank.exchange.solution.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Represents the Modification message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Modification extends TradingMessage {

    private final int modifiedOrderId;
    private final Details details;

    @Builder
    public Modification(int id, long timestamp, @NonNull String broker, int modifiedOrderId,@NonNull Details details) {
        super(id, timestamp, broker);
        this.modifiedOrderId = modifiedOrderId;
        this.details = details;
    }
}
