package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.execution.ProcessingTask;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-28.
 */
public class ModificationSchedulingTask implements SchedulingTask {

    private final ProductExchangeIndex productExchangeIndex;
    private final IdProductIndex idProductIndex;
    private final ProcessingTask<Modification> modificationProcessingTask;


    public ModificationSchedulingTask(ProductExchangeIndex productExchangeIndex,
                                      IdProductIndex idProductIndex,
                                      ProcessingTask<Modification> modificationProcessingTask) {
        this.productExchangeIndex = productExchangeIndex;
        this.idProductIndex = idProductIndex;
        this.modificationProcessingTask = modificationProcessingTask;
    }

    @Override
    public void execute() throws OrderNotFoundException {
        Modification modification = modificationProcessingTask.getTradingMessage();
        Optional<String> product = idProductIndex.get(modification.getModifiedOrderId());
        if (!product.isPresent()) {
            throw new OrderNotFoundException();
        }
        productExchangeIndex.getLedger(product.get()).addTask(modificationProcessingTask);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return modificationProcessingTask.getTradingMessage();
    }
}
