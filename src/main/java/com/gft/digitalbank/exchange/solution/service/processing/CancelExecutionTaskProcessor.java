package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class CancelExecutionTaskProcessor {

    public void processCancel(Cancel cancel, ProductLedger productLedger) {
        int cancelledOrderId = cancel.getCancelledOrderId();
        Order orderToCancel = productLedger.getById(cancelledOrderId)
                //TODO replace NPE with something else
                .orElseThrow(() -> new NullPointerException("Unable to find order to cancel!"));
        productLedger.remove(orderToCancel);
    }
}
