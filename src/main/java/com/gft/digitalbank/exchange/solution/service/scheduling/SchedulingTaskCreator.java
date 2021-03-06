package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.apache.camel.Handler;

/**
 * Responsible for creation of SchedulingTasks.
 * As SchedulingTaskFactory depends on ProcessingTaskFactory it encapsulates both of them.
 * <p>
 * Created by Ivo Zieliński on 2016-06-28.
 */
@Singleton
public class SchedulingTaskCreator<E extends TradingMessage> {

    private final ProcessingTaskFactory<E> processingTaskFactory;
    private final SchedulingTaskFactory<E> schedulingTaskFactory;

    @Inject
    public SchedulingTaskCreator(ProcessingTaskFactory<E> processingTaskFactory,
                                 SchedulingTaskFactory<E> schedulingTaskFactory) {
        this.processingTaskFactory = processingTaskFactory;
        this.schedulingTaskFactory = schedulingTaskFactory;
    }

    /**
     * Creates a SchedulingTask  for a TradingMessage's ProcessingTask.
     *
     * @param message whose processing is to be scheduled by the created task
     * @return SchedulingTask
     */
    @Handler
    public SchedulingTask<E> createSchedulingTask(@NonNull E message) {
        ProcessingTask<E> processingTask = processingTaskFactory.createProcessingTask(message);
        return schedulingTaskFactory.create(processingTask);
    }
}
