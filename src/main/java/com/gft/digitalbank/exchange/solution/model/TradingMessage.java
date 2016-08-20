package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;

/**
 * Base class for Cancel, Order and Modification classes containing all the common fields.
 *
 * Created by Ivo Zieliński on 2016-06-27.
 */
@Data
public class TradingMessage {

    private final int id;
    private final long timestamp;
    private final String broker;
}
