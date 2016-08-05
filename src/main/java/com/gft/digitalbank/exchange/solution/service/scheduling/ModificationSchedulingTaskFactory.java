package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.execution.ProcessingTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class ModificationSchedulingTaskFactory {

    private final ProductExchangeIndex productExchangeIndex;
    private final IdProductIndex idProductIndex;
    private final ProcessingTaskFactory<Modification> modificationProcessingTaskFactory;

    @Inject
    public ModificationSchedulingTaskFactory(ProductExchangeIndex productExchangeIndex,
                                             IdProductIndex idProductIndex,
                                             ProcessingTaskFactory<Modification> modificationProcessingTaskFactory) {
        this.productExchangeIndex = productExchangeIndex;
        this.idProductIndex = idProductIndex;
        this.modificationProcessingTaskFactory = modificationProcessingTaskFactory;
    }

    public ModificationSchedulingTask createModificationTask(Modification modification) {
        return new ModificationSchedulingTask(productExchangeIndex, idProductIndex,
                modificationProcessingTaskFactory.createProcessingTask(modification));
    }
}
