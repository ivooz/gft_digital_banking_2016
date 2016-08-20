package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;

/**
 * Defines how a TradingMessage is applied to the ProductExchange.
 * <p>
 * Created by Ivo Zieliński on 2016-07-15.
 */
public interface TradingMessageProcessor<E extends TradingMessage> {

    /**
     * Applies the TradingMessage to the ProductExchange.
     *
     * @param message         message
     * @param productExchange to execute the message against
     */
    void processTradingMessage(E message, ProductExchange productExchange);
}
