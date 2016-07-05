package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;

/**
 * Created by iozi on 2016-06-27.
 */
@Data
public class Cancel extends TradingMessage {

    private int cancelledOrderId;

    @Override
    public String toString() {
        return "Cancel{" +
                "timestamp=" + getTimestamp() +
                ", cancelledOrderId=" + cancelledOrderId +
                '}';
    }
}
