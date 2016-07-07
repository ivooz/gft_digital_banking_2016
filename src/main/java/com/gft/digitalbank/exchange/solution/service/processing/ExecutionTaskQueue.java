package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.service.tasks.execution.ExecutionTask;

import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Created by iozi on 2016-07-06.
 */
public class ExecutionTaskQueue {

    private static final int MAX_QUEUE_SIZE = 5;

    private final PriorityQueue<ExecutionTask> tasksToExecute = new PriorityQueue<>();

    public void addTask(ExecutionTask executionTask) {
        tasksToExecute.add(executionTask);
    }

    public Optional<ExecutionTask> getNextTaskToExecute() {
        return Optional.ofNullable(tasksToExecute.poll());
    }

    public Optional<ExecutionTask> peekTaskToExecute() {
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
