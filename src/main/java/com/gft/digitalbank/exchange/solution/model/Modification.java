package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Represents the Modification message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Modification extends TradingMessage {

    @NonNull
    private final int modifiedOrderId;

    @NonNull
    private final Details details;
}
