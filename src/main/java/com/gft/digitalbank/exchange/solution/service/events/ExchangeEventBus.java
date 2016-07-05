package com.gft.digitalbank.exchange.solution.service.events;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

import java.util.concurrent.CompletableFuture;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class ExchangeEventBus {

    private final EventBus eventBus = new EventBus();

    public void register(Object listener) {
        eventBus.register(listener);
    }

    public void postEvent(Object event) {
        CompletableFuture.runAsync(() -> eventBus.post(event));
    }
}
