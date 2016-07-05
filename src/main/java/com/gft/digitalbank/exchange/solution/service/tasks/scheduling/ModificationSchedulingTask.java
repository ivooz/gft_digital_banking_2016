package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.events.OrderNotFoundMessageBroker;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedgerIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ModificationExecutionTask;

/**
 * Created by iozi on 2016-06-28.
 */
public class ModificationSchedulingTask implements SchedulingTask,Runnable {

    private final ProductLedgerIndex productLedgerIndex;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final OrderNotFoundMessageBroker orderNotFoundMessageBroker;
    private final ModificationExecutionTask modificationExecutionTask;


    public ModificationSchedulingTask(ProductLedgerIndex productLedgerIndex,
                                      IdProductIndex idProductIndex,
                                      ExecutionTaskScheduler executionTaskScheduler,
                                      OrderNotFoundMessageBroker orderNotFoundMessageBroker,
                                      ModificationExecutionTask modificationExecutionTask) {
        this.productLedgerIndex = productLedgerIndex;
        this.idProductIndex = idProductIndex;
        this.executionTaskScheduler = executionTaskScheduler;
        this.orderNotFoundMessageBroker = orderNotFoundMessageBroker;
        this.modificationExecutionTask = modificationExecutionTask;
    }

    @Override
    public void run() {
        Modification modification = modificationExecutionTask.getModification();
        String product = idProductIndex.get(modification.getModifiedOrderId());
        if(product == null) {
            orderNotFoundMessageBroker.broadCastOrderNotFoundMessage(this);
            return;
        }
        ProductLedger productLedger = productLedgerIndex.getLedger(product);
        synchronized (productLedger) {
            executionTaskScheduler.scheduleExecutionTask(modificationExecutionTask, productLedger);
        }
    }

    @Override
    public TradingMessage getTradingMessage() {
        return modificationExecutionTask.getTradingMessage();
    }
}
