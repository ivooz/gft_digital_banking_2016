package com.gft.digitalbank.exchange.solution.service.events;

import lombok.Data;

/**
 * Created by iozi on 2016-07-01.
 */
@Data
public class OrderNotFoundEvent {

    private final Runnable runnable;
}
