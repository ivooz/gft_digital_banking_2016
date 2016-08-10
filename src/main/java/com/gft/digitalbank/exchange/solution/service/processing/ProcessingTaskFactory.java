package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;

/**
 * Responsible for creation of ProcessingTask objects.
 *
 * Created by Ivo Zieliński on 2016-06-28.
 */
@Singleton
public class ProcessingTaskFactory<E extends TradingMessage> {

    private final TradingMessageProcessor<E> tradingMessageProcessor;

    @Inject
    public ProcessingTaskFactory(TradingMessageProcessor<E> tradingMessageProcessor) {
        this.tradingMessageProcessor = tradingMessageProcessor;
    }

    /**
     *
     * @param message for the ProcessingTask to execute
     * @return
     */
    public ProcessingTask<E> createProcessingTask(@NonNull E message) {
        return new ProcessingTask<E>(tradingMessageProcessor, message);
    }
}
