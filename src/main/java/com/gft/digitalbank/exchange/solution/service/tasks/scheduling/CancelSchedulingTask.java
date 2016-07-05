package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.events.OrderNotFoundMessageBroker;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedgerIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.CancelExecutionTask;

/**
 * Created by iozi on 2016-06-28.
 */
public class CancelSchedulingTask implements SchedulingTask,Runnable {

    private final ProductLedgerIndex productMessageQueuesHolder;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final OrderNotFoundMessageBroker orderNotFoundMessageBroker;
    private final CancelExecutionTask cancelExecutionTask;

    public CancelSchedulingTask(ProductLedgerIndex productMessageQueuesHolder,
                                ExecutionTaskScheduler executionTaskScheduler,
                                IdProductIndex idProductIndex,
                                OrderNotFoundMessageBroker orderNotFoundMessageBroker,
                                CancelExecutionTask cancelExecutionTask) {
        this.productMessageQueuesHolder = productMessageQueuesHolder;
        this.executionTaskScheduler = executionTaskScheduler;
        this.idProductIndex = idProductIndex;
        this.orderNotFoundMessageBroker = orderNotFoundMessageBroker;
        this.cancelExecutionTask = cancelExecutionTask;
    }

    @Override
    public void run() {
        String product = idProductIndex.get(cancelExecutionTask.getCancel().getCancelledOrderId());
        if(product == null) {
            orderNotFoundMessageBroker.broadCastOrderNotFoundMessage(this);
            return;
        }
        ProductLedger productLedger = productMessageQueuesHolder.getLedger(product);
        synchronized (productLedger) {
            executionTaskScheduler.scheduleExecutionTask(cancelExecutionTask, productLedger);
        }
    }

    @Override
    public TradingMessage getTradingMessage() {
        return cancelExecutionTask.getTradingMessage();
    }




}
