package com.gft.digitalbank.exchange.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the Modification message.
 *
 * Created by iozi on 2016-06-27.
 */
@Data
@NoArgsConstructor
public class Modification extends TradingMessage {

    private int modifiedOrderId;
    private Details details;
}
