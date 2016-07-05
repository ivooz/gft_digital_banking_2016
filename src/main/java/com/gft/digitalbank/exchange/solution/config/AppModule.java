package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.service.deserialization.GsonMessageDeserializer;
import com.gft.digitalbank.exchange.solution.service.deserialization.MessageDeserializer;
import com.gft.digitalbank.exchange.solution.service.deserialization.MessageTypeCheckerStrategy;
import com.gft.digitalbank.exchange.solution.service.deserialization.StringContainsMessageTypeCheckerStrategy;
import com.gft.digitalbank.exchange.solution.service.dispatching.ThreadPoolTradingMessageDispatcher;
import com.gft.digitalbank.exchange.solution.service.dispatching.TradingMessageDispatcher;
import com.google.inject.AbstractModule;

/**
 * Created by iozi on 2016-06-28.
 */
//TODO change name
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        //Serialization
        bind(MessageTypeCheckerStrategy.class).to(StringContainsMessageTypeCheckerStrategy.class);
        bind(MessageDeserializer.class).to(GsonMessageDeserializer.class);
        //Queues
        bind(TradingMessageDispatcher.class).to(ThreadPoolTradingMessageDispatcher.class);

    }
}
