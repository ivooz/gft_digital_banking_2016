package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.ModificationExecutionTaskProcessor;

/**
 * Created by iozi on 2016-06-28.
 */
public class ModificationExecutionTask implements ExecutionTask {

    private final ModificationExecutionTaskProcessor modificationTaskProcessor;
    private final Modification modification;

    public ModificationExecutionTask(ModificationExecutionTaskProcessor modificationTaskProcessor, Modification modification) {
        this.modificationTaskProcessor = modificationTaskProcessor;
        this.modification = modification;
    }

    @Override
    public void execute(ProductLedger productLedger) {
        modificationTaskProcessor.processModification(modification, productLedger);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return modification;
    }

    public Modification getModification() {
        return modification;
    }
}
