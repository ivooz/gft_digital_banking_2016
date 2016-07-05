package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class ModificationExecutionTaskProcessor {

    @Inject
    TransactionExecutor transactionExecutor;

    public void processModification(Modification modification, ProductLedger productLedger) {
        Order orderToModify = productLedger.getById(modification.getModifiedOrderId())
                //TODO
                .orElseThrow(() -> new NullPointerException("Unable to find order to modify!"));
        Order copy = new Order(orderToModify);
        copy.setDetails(modification.getDetails());
        productLedger.remove(orderToModify);
        copy.setTimestamp(modification.getTimestamp());
        transactionExecutor.matchAndClearOrder(copy,productLedger);
    }
}
