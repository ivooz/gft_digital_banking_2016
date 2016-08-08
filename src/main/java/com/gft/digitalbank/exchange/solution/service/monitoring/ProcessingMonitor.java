package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by iozi on 2016-06-29.
 */
@Singleton
public class ProcessingMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingMonitor.class);

    private final ResultsGatherer resultGatherer;
    private final TasksExecutionFinisher tasksExecutionFinisher;
    private final CamelContext camelContext;

    private AtomicInteger brokerCount;
    private ProcessingListener processingListener;


    @Inject
    public ProcessingMonitor(ResultsGatherer resultGatherer,
                             TasksExecutionFinisher tasksExecutionFinisher,
                             CamelContext camelContext) {
        this.resultGatherer = resultGatherer;
        this.tasksExecutionFinisher = tasksExecutionFinisher;
        this.camelContext = camelContext;
    }

    public void decreaseBrokerCounter() {
        CompletableFuture.runAsync(() -> {
            int currentCount = brokerCount.decrementAndGet();
            if (currentCount == 0) {
                try {
                    Thread.sleep(50);
                    camelContext.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tasksExecutionFinisher.finishAllTasks();
                processingListener.processingDone(resultGatherer.gatherResults());
            }
        });
    }

    public void setProcessingListener(ProcessingListener processingListener) {
        this.processingListener = processingListener;
    }

    public void setBrokerCount(int brokerCount) {
        this.brokerCount = new AtomicInteger(brokerCount);
    }
}
