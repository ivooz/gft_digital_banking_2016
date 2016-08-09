package com.gft.digitalbank.exchange.solution.service.exchange;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Created by iozi on 2016-08-09.
 */
@Singleton
public class ProductExchangeFactory {

    private final int processingTaskBufferSize;

    @Inject
    public ProductExchangeFactory(@Named("processing.task.buffer.size") int processingTaskBufferSize) {
        this.processingTaskBufferSize = processingTaskBufferSize;
    }

    public ProductExchange createProducteExchange(String productName) {
        return new ProductExchange(productName,processingTaskBufferSize);
    }
}
