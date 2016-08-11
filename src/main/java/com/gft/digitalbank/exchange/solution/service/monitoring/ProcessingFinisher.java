package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.camel.CamelContext;

/**
 * Responsible for finishing processing of TradingMessages. Shuts down all the Camel routes and Threads.
 * <p>
 * Created by iozi on 2016-08-10.
 */
@Singleton
public class ProcessingFinisher {

    private final TasksExecutionFinisher tasksExecutionFinisher;
    private final CamelContext camelContext;

    @Inject
    public ProcessingFinisher(TasksExecutionFinisher tasksExecutionFinisher, CamelContext camelContext) {
        this.tasksExecutionFinisher = tasksExecutionFinisher;
        this.camelContext = camelContext;
    }

    /**
     * Closes all the resources opened and initializes the procedure of finishing all the ProcessingTasks remaining in
     * the buffers.
     *
     * @throws ProcessingShutdownException
     */
    public void finishProcessing() throws ProcessingShutdownException {
        try {
            camelContext.stop();
        } catch (Exception ex) {
            throw new ProcessingShutdownException("Encountered a problem while stopping Camel.", ex);
        } finally {
            tasksExecutionFinisher.finishAllTasks();
        }
    }
}
