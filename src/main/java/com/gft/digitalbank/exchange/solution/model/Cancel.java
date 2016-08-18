package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents the Cancel message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Cancel extends TradingMessage {

    private int cancelledOrderId;
}
