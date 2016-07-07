package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;

/**
 * Created by iozi on 2016-06-27.
 */
@Data
public class Modification extends TradingMessage {

    private int modifiedOrderId;
    private Details details;
}
