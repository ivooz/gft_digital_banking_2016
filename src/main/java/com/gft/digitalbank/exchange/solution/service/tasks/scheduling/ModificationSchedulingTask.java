package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.events.OrderNotFoundMessageBroker;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ModificationExecutionTask;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-28.
 */
public class ModificationSchedulingTask implements SchedulingTask, Runnable {

    private final ProductExchangeIndex productExchangeIndex;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final OrderNotFoundMessageBroker orderNotFoundMessageBroker;
    private final ModificationExecutionTask modificationExecutionTask;


    public ModificationSchedulingTask(ProductExchangeIndex productExchangeIndex,
                                      IdProductIndex idProductIndex,
                                      ExecutionTaskScheduler executionTaskScheduler,
                                      OrderNotFoundMessageBroker orderNotFoundMessageBroker,
                                      ModificationExecutionTask modificationExecutionTask) {
        this.productExchangeIndex = productExchangeIndex;
        this.idProductIndex = idProductIndex;
        this.executionTaskScheduler = executionTaskScheduler;
        this.orderNotFoundMessageBroker = orderNotFoundMessageBroker;
        this.modificationExecutionTask = modificationExecutionTask;
    }

    @Override
    public void run() {
        Modification modification = modificationExecutionTask.getModification();
        Optional<String> product = idProductIndex.get(modification.getModifiedOrderId());
        if (!product.isPresent()) {
            orderNotFoundMessageBroker.broadCastOrderNotFoundMessage(this);
            return;
        }
        ProductExchange productExchange = productExchangeIndex.getLedger(product.get());
        executionTaskScheduler.scheduleExecutionTask(modificationExecutionTask, productExchange);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return modificationExecutionTask.getTradingMessage();
    }
}
