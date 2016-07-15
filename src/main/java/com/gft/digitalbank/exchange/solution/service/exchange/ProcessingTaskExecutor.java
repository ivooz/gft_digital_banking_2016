package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.service.tasks.execution.ProcessingTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by iozi on 2016-07-15.
 */
public class ProcessingTaskExecutor {

    private final ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());

    public void shutdown() {
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(1000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //TODO
            e.printStackTrace();
        }
    }

    public void execute(ProcessingTask processingTask) {
        taskExecutor.execute(processingTask);
    }

    interface RunnableComparable extends Runnable,Comparable {}
}
