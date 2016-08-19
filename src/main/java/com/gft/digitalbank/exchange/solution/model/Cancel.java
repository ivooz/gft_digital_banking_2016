package com.gft.digitalbank.exchange.solution.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Represents the Cancel message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Cancel extends TradingMessage {

    private final int cancelledOrderId;

    @Builder
    public Cancel(int id, long timestamp, @NonNull String broker, int cancelledOrderId) {
        super(id, timestamp, broker);
        this.cancelledOrderId = cancelledOrderId;
    }
}
