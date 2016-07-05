package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.service.dispatching.TradingMessageDispatcher;
import com.gft.digitalbank.exchange.solution.service.events.ExchangeEventBus;
import com.gft.digitalbank.exchange.solution.service.events.TradingFinishedEvent;
import com.gft.digitalbank.exchange.solution.service.tasks.scheduling.TasksExecutionFinisher;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by iozi on 2016-06-29.
 */
@Singleton
public class ProcessingMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingMonitor.class);

    private final TradingMessageDispatcher tradingMessageDispatcher;
    private final ResultsGatherer resultGatherer;
    private final ExchangeEventBus exchangeEventBus;
    private final TasksExecutionFinisher tasksExecutionFinisher;

    private AtomicInteger brokerCount;
    private ProcessingListener processingListener;


    @Inject
    public ProcessingMonitor(TradingMessageDispatcher tradingMessageDispatcher,
                             ResultsGatherer resultGatherer,
                             ExchangeEventBus exchangeEventBus,
                             TasksExecutionFinisher tasksExecutionFinisher) {
        this.tradingMessageDispatcher = tradingMessageDispatcher;
        this.resultGatherer = resultGatherer;
        this.exchangeEventBus = exchangeEventBus;
        this.tasksExecutionFinisher = tasksExecutionFinisher;
    }

    public void decreaseBrokerCounter() {
        int currentCount = brokerCount.decrementAndGet();
        if (currentCount == 0) {
            exchangeEventBus.postEvent(new TradingFinishedEvent());
            try {
                tradingMessageDispatcher.shutdownAndAwaitTermination();
                tasksExecutionFinisher.finishAllTasks();
                processingListener.processingDone(resultGatherer.gatherResults());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setProcessingListener(ProcessingListener processingListener) {
        this.processingListener = processingListener;
    }

    public void setBrokerCount(int brokerCount) {
        this.brokerCount = new AtomicInteger(brokerCount);
    }
}
