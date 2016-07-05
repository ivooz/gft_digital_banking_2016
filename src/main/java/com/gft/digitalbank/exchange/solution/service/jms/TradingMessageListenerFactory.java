package com.gft.digitalbank.exchange.solution.service.jms;

import com.gft.digitalbank.exchange.solution.service.deserialization.MessageDeserializer;
import com.gft.digitalbank.exchange.solution.service.dispatching.TradingMessageDispatcher;
import com.gft.digitalbank.exchange.solution.service.events.ExchangeEventBus;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class TradingMessageListenerFactory {

    @Inject
    private TradingMessageDispatcher tradingMessageDispatcher;

    @Inject
    private MessageDeserializer messageDeserializer;

    @Inject
    private ExchangeEventBus exchangeEventBus;

    @Inject
    private ProcessingMonitor processingMonitor;

    public TradingMessageListener createTradingMessageListener() {
        return new TradingMessageListener(tradingMessageDispatcher, messageDeserializer, exchangeEventBus, processingMonitor);
    }
}
