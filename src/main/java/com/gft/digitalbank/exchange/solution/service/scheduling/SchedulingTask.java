package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.google.inject.assistedinject.Assisted;
import lombok.NonNull;

/**
 * Represents a unit of work associated with adding a ProcessingTask handling a TradingMessage to the queue in
 * ProductExchange corresponding the TradingMessage's product.
 *
 * Created by Ivo Zieliński on 2016-07-01.
 */
public abstract class SchedulingTask<E extends TradingMessage> {

    protected final ProductExchangeIndex productExchangeIndex;
    protected final IdProductIndex idProductIndex;
    protected final ProcessingTask<E> processingTask;

    public SchedulingTask(ProductExchangeIndex productExchangeIndex,
                          IdProductIndex idProductIndex,
                          @Assisted ProcessingTask<E> processingTask) {
        this.productExchangeIndex = productExchangeIndex;
        this.idProductIndex = idProductIndex;
        this.processingTask = processingTask;
    }

    /**
     * Adds the ProcessingTask associated with handling a TradingMessage to the ProcessingTask queue in an appropriate
     * ProductExchange.
     *
     * @throws OrderNotFoundException if the ProductExchange associated with the Order to alter could not be determined.
     * In parallel execution of SchedulingTasks sometimes the Cancel/Modification TradingMessage processing is scheduled
     * before the associated Order TradingMessage, so that its product and consequently ProductExchange is unknown.
     */
    abstract void execute() throws OrderNotFoundException;

    /**
     * Retrieves the TradingMessage to be handled by the scheduled ProcessingTask.
     * @return
     */
    public E getTradingMessage() {
        return processingTask.getTradingMessage();
    }

//    /**
//     * Tasks are ordered according to the timestamp of the ProcessingTask's TradingMessage. Lower timestamps have priority.
//     * @param schedulingTask
//     * @return
//     */
//    @Override
//    public int compareTo(@NonNull SchedulingTask schedulingTask) {
//        return (int) (this.getTradingMessage().getTimestamp() - schedulingTask.getTradingMessage().getTimestamp());
//    }
}
