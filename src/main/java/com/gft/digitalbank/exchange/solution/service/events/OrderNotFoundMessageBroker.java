package com.gft.digitalbank.exchange.solution.service.events;

import com.gft.digitalbank.exchange.solution.service.dispatching.TradingMessageDispatcher;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by iozi on 2016-07-01.
 */
@Singleton
public class OrderNotFoundMessageBroker {

    List<TradingMessageDispatcher> messageDispatchers = new ArrayList<>();

    public void subscribe(TradingMessageDispatcher listener) {
        messageDispatchers.add(listener);
    }

    //TODO refactor
    public void broadCastOrderNotFoundMessage(Runnable runnable) {
        for(TradingMessageDispatcher tradingMessageDispatcher : messageDispatchers) {
            tradingMessageDispatcher.onOrderNotFound(runnable);
        }
    }
}
