package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;

/**
 * Created by iozi on 2016-07-01.
 */
public interface SchedulingTask extends Comparable {

    void execute() throws OrderNotFoundException;

    TradingMessage getTradingMessage();

    @Override
    default int compareTo(Object o) {
        TradingMessage otherMessage = ((SchedulingTask) o).getTradingMessage();
        return (int) (this.getTradingMessage().getTimestamp() - otherMessage.getTimestamp());
    }
}
