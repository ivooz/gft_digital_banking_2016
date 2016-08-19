package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;

/**
 * Created by Ivo Zieliński on 2016-06-27.
 */
@Data
public class TradingMessage {

    private final int id;
    private final long timestamp;
    private final String broker;
}
