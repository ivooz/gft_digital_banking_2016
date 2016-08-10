package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import lombok.NonNull;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for executing ProcessingTasks for an associated ProductExchange.
 * Encapsulates single-threaded executor service so that no ProductExchanged-scoped synchronization is required for
 * ProcessingTask execution.
 * ProcessingTasks execution order is dictated by their timestamp.
 * <p>
 * Created by Ivo Zieliński on 2016-07-15.
 */
public class ProcessingTaskExecutorService {

    public static final int CORE_POOL_SIZE = 1;
    public static final int MAXIMUM_POOL_SIZE = 1;
    public static final int KEEP_ALIVE_TIME = 1000;
    public static final int SHUTDOWN_TIMEOUT = 1000;

    private final ProductExchange productExchange;
    private final ThreadPoolExecutor taskExecutor;

    /**
     * @param productExchange that the ProcessingTasks shall be executed against.
     */
    public ProcessingTaskExecutorService(@NonNull ProductExchange productExchange) {
        this.productExchange = productExchange;
        this.taskExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());
    }

    /**
     * Shuts down and waits till currently executed task terminates.
     *
     * @throws ExchangeShutdownException
     */
    public void shutdownAndAwaitTermination() throws ExchangeShutdownException {
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new ExchangeShutdownException("Processing task interrupted!", e);
        }
    }

    /**
     * Adds the ProcessingTask to the queue.
     *
     * @param processingTask to queue
     */
    public void enqueueProcessingTask(@NonNull ProcessingTask processingTask) {
        processingTask.setProductExchange(productExchange);
        taskExecutor.execute(processingTask);
    }
}
