package com.gft.digitalbank.exchange.solution.service.dispatching;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;

/**
 * Created by iozi on 2016-06-28.
 */
public interface TradingMessageDispatcher {

    void dispatchOrder(Order order);

    void dispatchModification(Modification modification);

    void dispatchCancel(Cancel cancel);

    void shutdownAndAwaitTermination() throws InterruptedException;

    void onOrderNotFound(Runnable runnable);
}
