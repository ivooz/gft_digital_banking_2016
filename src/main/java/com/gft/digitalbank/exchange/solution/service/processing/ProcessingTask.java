package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;

/**
 * Created by iozi on 2016-07-01.
 */
public class ProcessingTask<E extends TradingMessage> implements Comparable, Runnable {

    private final TradingMessageProcessor<E> tradingMessageProcessor;
    private final E tradingMessage;

    private ProductExchange productExchange;

    public ProcessingTask(TradingMessageProcessor<E> tradingMessageProcessor, E tradingMessage) {
        this.tradingMessageProcessor = tradingMessageProcessor;
        this.tradingMessage = tradingMessage;
    }

    public E getTradingMessage() {
        return tradingMessage;
    }

    public void run() {
        try {
            tradingMessageProcessor.processTradingMessage(tradingMessage,productExchange);
        } catch (OrderProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Object o) {
        ProcessingTask processingTask = (ProcessingTask) o;
        return (int) (this.getTradingMessage().getTimestamp() - processingTask.getTradingMessage().getTimestamp());
    }

    public void setProductExchange(ProductExchange productExchange) {
        this.productExchange = productExchange;
    }
}
