package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTaskFactory;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by Ivo Zieliński on 2016-06-28.
 */
@Singleton
public class CancelSchedulingTaskFactory {

    private final ProductExchangeIndex productMessageQueuesHolder;
    private final IdProductIndex idProductIndex;
    private final ProcessingTaskFactory<Cancel> cancelSchedulingTaskFactory;

    @Inject
    public CancelSchedulingTaskFactory(ProductExchangeIndex productMessageQueuesHolder,
                                       IdProductIndex idProductIndex,
                                       ProcessingTaskFactory<Cancel> cancelSchedulingTaskFactory) {
        this.productMessageQueuesHolder = productMessageQueuesHolder;
        this.idProductIndex = idProductIndex;
        this.cancelSchedulingTaskFactory = cancelSchedulingTaskFactory;
    }

    public CancelSchedulingTask createCancelTask(Cancel cancel) {
        return new CancelSchedulingTask(productMessageQueuesHolder, idProductIndex,
                cancelSchedulingTaskFactory.createProcessingTask(cancel));
    }
}
