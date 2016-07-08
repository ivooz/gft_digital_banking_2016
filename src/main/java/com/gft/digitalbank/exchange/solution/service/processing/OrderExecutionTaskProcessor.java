package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class OrderExecutionTaskProcessor {

    @Inject
    TransactionExecutor transactionExecutor;

    public void processOrder(Order processedOrder, ProductExchange productExchange) {
            transactionExecutor.matchAndClearOrder(processedOrder, productExchange);
    }
}
