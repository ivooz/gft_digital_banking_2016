package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-07-15.
 */
@Singleton
public class SchedulingTaskExecutor {

    public void executeSchedulingTask(SchedulingTask schedulingTask) throws OrderNotFoundException {
        schedulingTask.execute();
    }
}
