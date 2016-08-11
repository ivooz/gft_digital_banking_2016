package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @inheritDoc
 * Created by Ivo Zieliński on 2016-06-28.
 */
public class OrderSchedulingTask extends SchedulingTask<Order> {

    @Inject
    public OrderSchedulingTask(ProductExchangeIndex productExchangeIndex,
                               IdProductIndex idProductIndex,
                               @Assisted ProcessingTask<Order> processingTask) {
        super(productExchangeIndex, idProductIndex, processingTask);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void execute() {
        Order order = processingTask.getTradingMessage();
        idProductIndex.put(order.getId(), order.getProduct());
        productExchangeIndex.getProductExchange(order.getProduct()).enqueueTask(processingTask);
    }
}
