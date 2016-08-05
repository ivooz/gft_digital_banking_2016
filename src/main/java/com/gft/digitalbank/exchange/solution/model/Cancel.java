package com.gft.digitalbank.exchange.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by iozi on 2016-06-27.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Cancel extends TradingMessage {

    private int cancelledOrderId;
}
