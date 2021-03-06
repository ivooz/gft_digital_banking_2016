package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.google.inject.Singleton;
import lombok.NonNull;
import org.apache.camel.Handler;

/**
 * Responsible for execution of SchedulingTasks.
 * The execution is carried out in the same thread it is called so that the Camel route can reissue the request
 * on failed delivery.
 *
 * @see OrderNotFoundException
 * Created by Ivo Zieliński on 2016-07-15.
 */
@Singleton
public class SchedulingTaskExecutor {

    /**
     * @param schedulingTask to be executed
     * @throws OrderNotFoundException when the SchedulingTask could not be executed properly
     */
    @Handler
    public void executeSchedulingTask(@NonNull SchedulingTask schedulingTask) throws OrderNotFoundException {
        schedulingTask.execute();
    }
}
