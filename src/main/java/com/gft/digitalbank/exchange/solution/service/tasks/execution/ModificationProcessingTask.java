package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.ModificationExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;

/**
 * Created by iozi on 2016-06-28.
 */
public class ModificationProcessingTask implements ProcessingTask {

    private final ModificationExecutionTaskProcessor modificationTaskProcessor;
    private final Modification modification;
    private ProductExchange productExchange;

    public ModificationProcessingTask(ModificationExecutionTaskProcessor modificationTaskProcessor, Modification modification) {
        this.modificationTaskProcessor = modificationTaskProcessor;
        this.modification = modification;
    }

    @Override
    public void run() {
        modificationTaskProcessor.processModification(modification, productExchange);
    }

    @Override
    public void setProductExchange(ProductExchange productExchange) {
        this.productExchange = productExchange;
    }

    @Override
    public TradingMessage getTradingMessage() {
        return modification;
    }

    public Modification getModification() {
        return modification;
    }
}
