package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.Optional;

/**
 * {@inheritDoc} Created by Ivo Zieliński on 2016-06-28.
 */
public class ModificationSchedulingTask extends SchedulingTask<Modification> {

    @Inject
    public ModificationSchedulingTask(ProductExchangeIndex productExchangeIndex,
                                      IdProductIndex idProductIndex,
                                      @Assisted ProcessingTask<Modification> processingTask) {
        super(productExchangeIndex, idProductIndex, processingTask);
    }

    /**
     * {@inheritDoc}
     * Before the ProcessingTask is Scheduled a ProductExchange of the modified Order is retrieved.
     */
    @Override
    public void execute() throws OrderNotFoundException {
        Modification modification = processingTask.getTradingMessage();
        Optional<String> product = idProductIndex.get(modification.getModifiedOrderId());
        if (!product.isPresent()) {
            throw new OrderNotFoundException();
        }
        productExchangeIndex.getProductExchange(product.get()).enqueueTask(processingTask);
    }
}
