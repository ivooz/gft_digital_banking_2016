package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by iozi on 2016-07-15.
 */
public class ProcessingTaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingTaskExecutor.class);

    public static final int CORE_POOL_SIZE = 1;
    public static final int MAXIMUM_POOL_SIZE = 1;
    public static final int KEEP_ALIVE_TIME = 10;
    public static final int SHUTDOWN_TIMEOUT = 1000;

    private final ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());

    public void shutdown() throws ExchangeShutdownException {
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new ExchangeShutdownException("Processing task interrupted!", e);
        }
    }

    public void execute(ProcessingTask processingTask, ProductExchange productExchange) {
        processingTask.setProductExchange(productExchange);
        taskExecutor.execute(processingTask);
    }
}
