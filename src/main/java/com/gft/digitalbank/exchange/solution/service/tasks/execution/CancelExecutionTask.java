package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.CancelExecutionTaskProcessor;

/**
 * Created by iozi on 2016-06-28.
 */
public class CancelExecutionTask implements ExecutionTask {

    private final CancelExecutionTaskProcessor cancelTaskProcessor;
    private final Cancel cancel;

    public CancelExecutionTask(CancelExecutionTaskProcessor cancelTaskProcessor, Cancel cancel) {
        this.cancelTaskProcessor = cancelTaskProcessor;
        this.cancel = cancel;
    }

    @Override
    public void execute(ProductExchange productExchange) {

        cancelTaskProcessor.processCancel(cancel, productExchange);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return cancel;
    }

    public Cancel getCancel() {
        return cancel;
    }
}
