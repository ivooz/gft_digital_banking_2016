package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.processing.CancelExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;

/**
 * Created by iozi on 2016-06-28.
 */
public class CancelProcessingTask implements ProcessingTask {

    private final CancelExecutionTaskProcessor cancelTaskProcessor;
    private final Cancel cancel;
    private ProductExchange productExchange;

    public CancelProcessingTask(CancelExecutionTaskProcessor cancelTaskProcessor, Cancel cancel) {
        this.cancelTaskProcessor = cancelTaskProcessor;
        this.cancel = cancel;
    }

    @Override
    public void run() {
        cancelTaskProcessor.processCancel(cancel, productExchange);
    }

    @Override
    public void setProductExchange(ProductExchange productExchange) {
        this.productExchange = productExchange;
    }

    @Override
    public TradingMessage getTradingMessage() {
        return cancel;
    }

    public Cancel getCancel() {
        return cancel;
    }
}
