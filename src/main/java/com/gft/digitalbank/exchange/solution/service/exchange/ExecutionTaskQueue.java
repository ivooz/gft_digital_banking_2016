package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.service.tasks.execution.ProcessingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created by iozi on 2016-07-06.
 */
public class ExecutionTaskQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTaskQueue.class);

    private static final int MAX_QUEUE_SIZE = 15;

    private final BlockingQueue<ProcessingTask> tasksToExecute = new PriorityBlockingQueue<>(MAX_QUEUE_SIZE);

    public void addTask(ProcessingTask processingTask) {
        tasksToExecute.add(processingTask);
    }

    public Optional<ProcessingTask> getNextTaskToExecute() {
        return Optional.ofNullable(tasksToExecute.poll());
    }

    public Optional<ProcessingTask> peekTaskToExecute() {
        return Optional.ofNullable(tasksToExecute.peek());
    }

    public boolean isFull() {
        return tasksToExecute.size() >= MAX_QUEUE_SIZE;
    }

    public boolean isEmpty() {
        return tasksToExecute.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public int getTaskToExecuteCount() {
        return tasksToExecute.size();
    }
}
