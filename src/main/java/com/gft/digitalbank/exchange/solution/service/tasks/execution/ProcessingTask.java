package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;

/**
 * Created by iozi on 2016-07-01.
 */
public interface ProcessingTask extends Comparable, Runnable {

    void setProductExchange(ProductExchange productExchange);
    TradingMessage getTradingMessage();

    @Override
    default int compareTo(Object o) {
        ProcessingTask processingTask = (ProcessingTask) o;
        return (int) (this.getTradingMessage().getTimestamp() - processingTask.getTradingMessage().getTimestamp());
    }
}
