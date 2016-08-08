package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class OrderSchedulingTaskFactory {

    private final ProductExchangeIndex productExchangeIndex;
    private final IdProductIndex idProductIndex;
    private final ProcessingTaskFactory<Order> orderProcessingTaskFactory;

    @Inject
    public OrderSchedulingTaskFactory(ProductExchangeIndex productExchangeIndex,
                                      IdProductIndex idProductIndex,
                                      ProcessingTaskFactory<Order> orderProcessingTaskFactory) {
        this.productExchangeIndex = productExchangeIndex;
        this.idProductIndex = idProductIndex;
        this.orderProcessingTaskFactory = orderProcessingTaskFactory;
    }

    public OrderSchedulingTask createOrderTask(Order order) {
        return new OrderSchedulingTask(productExchangeIndex, idProductIndex,
                orderProcessingTaskFactory.createProcessingTask(order));
    }
}
