package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.service.processing.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;

/**
 * Created by iozi on 2016-07-01.
 */
public interface ExecutionTask extends Comparable {

    void execute(ProductExchange productExchange);

    TradingMessage getTradingMessage();

    @Override
    default int compareTo(Object o) {
        ExecutionTask executionTask = (ExecutionTask) o;
        return (int) (this.getTradingMessage().getTimestamp() - executionTask.getTradingMessage().getTimestamp());
    }
}
