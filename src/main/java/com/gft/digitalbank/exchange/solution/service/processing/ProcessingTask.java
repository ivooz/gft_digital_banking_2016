package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents the unit of work associated with applying the TradingMessage to ProductExchange.
 * It is meant to be used in PriorityQueues so that tasks can be properly ordered.
 *
 * Created by Ivo Zieliński on 2016-07-01.
 */
@Slf4j
public class ProcessingTask<E extends TradingMessage> implements Comparable<ProcessingTask>, Runnable {

    private static final String PRODUCT_EXCHANGE_NOT_SET_EXCEPTION_MESSAGE =
            "Cannot process TradingMessage without the ProductExchange!";

    private final TradingMessageProcessor<E> tradingMessageProcessor;
    private final E tradingMessage;

    private ProductExchange productExchange;

    public ProcessingTask(TradingMessageProcessor<E> tradingMessageProcessor,
                          E tradingMessage) {
        this.tradingMessageProcessor = tradingMessageProcessor;
        this.tradingMessage = tradingMessage;
    }

    /**
     * Initializes the process of applying the TradingMessage against the ProductExchange.
     */
    public void run() {
        Preconditions.checkNotNull(productExchange, PRODUCT_EXCHANGE_NOT_SET_EXCEPTION_MESSAGE);
        try {
            tradingMessageProcessor.processTradingMessage(tradingMessage, productExchange);
        } catch (OrderProcessingException ex) {
            log.error("Cannot properly conclude the processing task.",ex);
        }
    }

    /**
     *
     * @return the TradingMessage that the ProcessingTask is to apply
     */
    public E getTradingMessage() {
        return tradingMessage;
    }

    /**
     * The tasks are ordered according to the timestamp of their underlying TradingMessage. Messages with lower
     * timestamp (earlier) have priority.
     * @param processingTask to compare against
     * @return
     */
    @Override
    public int compareTo(@NonNull ProcessingTask processingTask) {
        return (int) (this.getTradingMessage().getTimestamp() - processingTask.getTradingMessage().getTimestamp());
    }

    /**
     * Sets the ProductExchange to apply the TradingMessage against. This cannot be done during the ProcessingTask
     * creation as it is not always immediately known to which ProductExchange the ProcessingTask pertains.
     * @param productExchange
     */
    public void setProductExchange(@NonNull ProductExchange productExchange) {
        this.productExchange = productExchange;
    }
}
