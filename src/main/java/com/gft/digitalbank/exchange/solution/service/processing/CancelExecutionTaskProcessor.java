package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class CancelExecutionTaskProcessor {

    public void processCancel(Cancel cancel, ProductExchange productExchange) {
        int cancelledOrderId = cancel.getCancelledOrderId();
        Order orderToCancel = productExchange.getById(cancelledOrderId)
                //TODO replace NPE with something else
                .orElseThrow(() -> new NullPointerException("Unable to find order to cancel!"));
        productExchange.remove(orderToCancel);
    }
}
