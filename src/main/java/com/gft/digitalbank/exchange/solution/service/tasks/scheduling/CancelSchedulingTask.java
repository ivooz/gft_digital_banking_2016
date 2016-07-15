package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.CancelProcessingTask;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-28.
 */
public class CancelSchedulingTask implements SchedulingTask {

    private final ProductExchangeIndex productMessageQueuesHolder;
    private final IdProductIndex idProductIndex;
    private final ExecutionTaskScheduler executionTaskScheduler;
    private final CancelProcessingTask cancelProcessingTask;

    public CancelSchedulingTask(ProductExchangeIndex productMessageQueuesHolder,
                                ExecutionTaskScheduler executionTaskScheduler,
                                IdProductIndex idProductIndex,
                                CancelProcessingTask cancelProcessingTask) {
        this.productMessageQueuesHolder = productMessageQueuesHolder;
        this.executionTaskScheduler = executionTaskScheduler;
        this.idProductIndex = idProductIndex;
        this.cancelProcessingTask = cancelProcessingTask;
    }

    @Override
    public void execute() throws OrderNotFoundException {
        Optional<String> productOptional = idProductIndex.get(cancelProcessingTask.getCancel().getCancelledOrderId());
        if (!productOptional.isPresent()) {
            throw new OrderNotFoundException();
        }
        ProductExchange productExchange = productMessageQueuesHolder.getLedger(productOptional.get());
        executionTaskScheduler.scheduleExecutionTask(cancelProcessingTask, productExchange);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return cancelProcessingTask.getTradingMessage();
    }
}
