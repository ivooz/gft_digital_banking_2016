package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.Optional;

/**
 * {@inheritDoc}
 *
 * Created by Ivo Zieliński on 2016-06-28.
 */
public class CancelSchedulingTask extends SchedulingTask<Cancel> {

    @Inject
    public CancelSchedulingTask(ProductExchangeIndex productExchangeIndex,
                                IdProductIndex idProductIndex,
                                @Assisted ProcessingTask<Cancel> processingTask) {
        super(productExchangeIndex, idProductIndex, processingTask);
    }

    /**
     * {@inheritDoc}
     * Before the ProcessingTask is scheduled a ProductExchange of the cancelled Order is retrieved.
     */
    @Override
    public void execute() throws OrderNotFoundException {
        int cancelledOrderId = processingTask.getTradingMessage().getCancelledOrderId();
        Optional<String> productOptional = idProductIndex.get(cancelledOrderId);
        if (!productOptional.isPresent()) {
            throw new OrderNotFoundException();
        }
        productExchangeIndex.getProductExchange(productOptional.get()).enqueueTask(processingTask);
    }
}
