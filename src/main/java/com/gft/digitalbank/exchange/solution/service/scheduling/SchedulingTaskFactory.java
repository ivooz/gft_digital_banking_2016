package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import lombok.NonNull;

/**
 * Responsible for creation of SchedulingTasks.
 *
 * Created by Ivo Zieliński on 2016-06-28.
 */
public interface SchedulingTaskFactory<M extends TradingMessage> {

    /**
     * Creates the SchedulingTask for a given ProcessingTask
     * @param processingTask to be sheduled
     * @return the created SchedulingTask
     */
    SchedulingTask<M> create(@NonNull ProcessingTask<M> processingTask);
}
