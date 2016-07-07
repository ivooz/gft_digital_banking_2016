package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class OrderExecutionTaskProcessor {

    @Inject
    TransactionExecutor transactionExecutor;

    public void processOrder(Order processedOrder, ProductExchange productExchange) {
        transactionExecutor.matchAndClearOrder(processedOrder,productExchange);
    }
}
