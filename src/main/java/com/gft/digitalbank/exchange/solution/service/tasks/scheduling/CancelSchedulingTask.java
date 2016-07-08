package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.events.OrderNotFoundMessageBroker;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.CancelExecutionTask;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-28.
 */
public class CancelSchedulingTask implements SchedulingTask, Runnable {

    private final ProductExchangeIndex productMessageQueuesHolder;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final OrderNotFoundMessageBroker orderNotFoundMessageBroker;
    private final CancelExecutionTask cancelExecutionTask;

    public CancelSchedulingTask(ProductExchangeIndex productMessageQueuesHolder,
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
        Optional<String> productOptional = idProductIndex.get(cancelExecutionTask.getCancel().getCancelledOrderId());
        if (!productOptional.isPresent()) {
            orderNotFoundMessageBroker.broadCastOrderNotFoundMessage(this);
            return;
        }
        ProductExchange productExchange = productMessageQueuesHolder.getLedger(productOptional.get());
        executionTaskScheduler.scheduleExecutionTask(cancelExecutionTask, productExchange);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return cancelExecutionTask.getTradingMessage();
    }
}
