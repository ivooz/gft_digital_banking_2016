package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ModificationProcessingTask;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-28.
 */
public class ModificationSchedulingTask implements SchedulingTask {

    private final ProductExchangeIndex productExchangeIndex;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final ModificationProcessingTask modificationProcessingTask;


    public ModificationSchedulingTask(ProductExchangeIndex productExchangeIndex,
                                      IdProductIndex idProductIndex,
                                      ExecutionTaskScheduler executionTaskScheduler,
                                      ModificationProcessingTask modificationProcessingTask) {
        this.productExchangeIndex = productExchangeIndex;
        this.idProductIndex = idProductIndex;
        this.executionTaskScheduler = executionTaskScheduler;
        this.modificationProcessingTask = modificationProcessingTask;
    }

    @Override
    public void execute() throws OrderNotFoundException {
        Modification modification = modificationProcessingTask.getModification();
        Optional<String> product = idProductIndex.get(modification.getModifiedOrderId());
        if (!product.isPresent()) {
            throw new OrderNotFoundException();
        }
        ProductExchange productExchange = productExchangeIndex.getLedger(product.get());
        executionTaskScheduler.scheduleExecutionTask(modificationProcessingTask, productExchange);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return modificationProcessingTask.getTradingMessage();
    }
}
