package com.gft.digitalbank.exchange.solution.model;

import lombok.Data;
import lombok.NonNull;

/**
 * Represents the Modification message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@Data
public class Modification extends TradingMessage {

    @NonNull
    private int modifiedOrderId;

    @NonNull
    private Details details;
}
