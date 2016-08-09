package com.gft.digitalbank.exchange.solution.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * Created by iozi on 2016-06-27.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingMessage {

    private int id;
    private long timestamp;
    private String broker;

}
