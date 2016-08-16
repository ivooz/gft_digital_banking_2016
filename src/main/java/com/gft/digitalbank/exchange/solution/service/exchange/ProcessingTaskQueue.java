package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import lombok.NonNull;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Defines the ProcessingTask queueing logic.
 * Tasks are buffered until the buffer size is exceeded - after that point the queue is considered full.
 * ProcessingTasks are taken from the top of the queue according to the order of they were dispatched in.
 * Increasing the buffer size decreases the probability that tasks will be served out of order.
 * <p>
 * Created by Ivo Zieliński on 2016-07-06.
 */
public class ProcessingTaskQueue {

    private final int processingTaskBufferSize;
    private final BlockingQueue<ProcessingTask> tasksToExecute;

    /**
     * @param processingTaskBufferSize that defines when the queue is considered empty
     */
    public ProcessingTaskQueue(int processingTaskBufferSize) {
        this.processingTaskBufferSize = processingTaskBufferSize;
        this.tasksToExecute = new PriorityBlockingQueue<>();
    }

    /**
     * The ProcessingTask shall be enqueued, if the queue size exceeds the buffer size the execution of the top
     * ProcessingTask should start.
     *
     * @param processingTask to enqueueOrder
     */
    public void enqueueTask(@NonNull ProcessingTask processingTask) {
        tasksToExecute.add(processingTask);
    }

    /**
     * Returns the next Optional ProcessingTask from the queue according to the timestamp of the TradingMessage they wrap.
     * The task is removed from the queue.
     *
     * @return
     */
    public Optional<ProcessingTask> getNextTaskToExecute() {
        return Optional.ofNullable(tasksToExecute.poll());
    }

    /**
     * Checks whether the queue is full, which means that is the buffer size was exceeded and the the ProcessingTask
     * execution should begin.
     *
     * @return boolean
     */
    public boolean isFull() {
        return tasksToExecute.size() >= processingTaskBufferSize;
    }

    /**
     * Checks whether there are any ProcessingTasks queued
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return tasksToExecute.isEmpty();
    }

    /**
     * Checks whether there are no ProcessingTasks queued
     *
     * @return true if not empty
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
