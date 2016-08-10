package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the Modification message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@Data
@NoArgsConstructor
public class Modification extends TradingMessage {

    private int modifiedOrderId;
    private Details details;
}
