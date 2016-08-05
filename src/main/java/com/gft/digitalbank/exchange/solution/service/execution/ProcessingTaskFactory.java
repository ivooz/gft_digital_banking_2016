package com.gft.digitalbank.exchange.solution.service.execution;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.TradingMessageProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class ProcessingTaskFactory<E extends TradingMessage> {

    @Inject
    private TradingMessageProcessor<E> tradingMessageProcessor;

    public ProcessingTask<E> createProcessingTask(E message) {
        return new ProcessingTask<E>(tradingMessageProcessor, message);
    }
}
