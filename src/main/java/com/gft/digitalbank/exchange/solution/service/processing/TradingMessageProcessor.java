package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;

/**
 * Created by iozi on 2016-07-15.
 */
public interface TradingMessageProcessor<E extends TradingMessage> {

    public void processTradingMessage(E message, ProductExchange productExchange) throws OrderProcessingException;
}
