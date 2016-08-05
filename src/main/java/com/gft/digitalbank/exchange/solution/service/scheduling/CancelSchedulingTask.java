package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.TradingMessage;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.gft.digitalbank.exchange.solution.service.execution.ProcessingTask;

import java.util.Optional;

/**
 * Created by iozi on 2016-06-28.
 */
public class CancelSchedulingTask implements SchedulingTask {

    private final ProductExchangeIndex productMessageQueuesHolder;
    private final IdProductIndex idProductIndex;
    private final ProcessingTask<Cancel> cancelProcessingTask;

    public CancelSchedulingTask(ProductExchangeIndex productMessageQueuesHolder,
                                IdProductIndex idProductIndex,
                                ProcessingTask<Cancel> cancelProcessingTask) {
        this.productMessageQueuesHolder = productMessageQueuesHolder;
        this.idProductIndex = idProductIndex;
        this.cancelProcessingTask = cancelProcessingTask;
    }

    @Override
    public void execute() throws OrderNotFoundException {
        Optional<String> productOptional = idProductIndex.get(cancelProcessingTask.getTradingMessage().getCancelledOrderId());
        if (!productOptional.isPresent()) {
            throw new OrderNotFoundException();
        }
        productMessageQueuesHolder.getLedger(productOptional.get()).addTask(cancelProcessingTask);
    }

    @Override
    public TradingMessage getTradingMessage() {
        return cancelProcessingTask.getTradingMessage();
    }
}
