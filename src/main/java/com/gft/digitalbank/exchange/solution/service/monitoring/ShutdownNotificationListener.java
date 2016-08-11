package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Responsible for monitoring whether the trading session is finished, initializing the shutdown procedures and relaying
 * the gathered results to the ProcessingListener.
 *
 * Created by Ivo Zieliński on 2016-06-29.
 */
@Slf4j
@Singleton
public class ShutdownNotificationListener {

    public static final String SHUTDOWN_EXCEPTION_MESSAGE = "Encountered problems when shutting down processing session.";
    private final ResultsGatherer resultGatherer;
    private final ProcessingFinisher processingFinisher;

    private AtomicInteger brokerCount;
    private ProcessingListener processingListener;

    @Inject
    public ShutdownNotificationListener(ResultsGatherer resultGatherer,
                                        ProcessingFinisher processingFinisher) {
        this.resultGatherer = resultGatherer;
        this.processingFinisher = processingFinisher;
    }

    /**
     * Counts the ShutdownNotification messages. When the counter reaches the Broker count, shutdown procedures are initiated
     * and the processing results are passed to the ProcessingListener.
     */
    public void handleShutdownNotification() {
        CompletableFuture.runAsync(() -> {
            int currentCount = brokerCount.decrementAndGet();
            if (currentCount == 0) {
                try {
                    processingFinisher.finishProcessing();
                } catch (ProcessingShutdownException e) {
                    log.error(SHUTDOWN_EXCEPTION_MESSAGE,e);
                }
                processingListener.processingDone(resultGatherer.gatherResults());
            }
        });
    }

    /**
     * Sets the ProcessingListener to which the processing results are passed after the shutdown procedure.
     * @param processingListener
     */
    public void setProcessingListener(@NonNull ProcessingListener processingListener) {
        this.processingListener = processingListener;
    }

    /**
     * Lets the ProcessingMonitor know how many brokers there are, so it can start the shutdown procedure after a specific
     * amount of ShutdownNotifications has been received.
     *
     * @param brokerCount
     */
    public void setBrokerCount(int brokerCount) {
        this.brokerCount = new AtomicInteger(brokerCount);
    }
}
