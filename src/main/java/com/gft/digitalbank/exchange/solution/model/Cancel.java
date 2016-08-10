package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the Cancel message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@Data
@NoArgsConstructor
public class Cancel extends TradingMessage {

    private int cancelledOrderId;
}
