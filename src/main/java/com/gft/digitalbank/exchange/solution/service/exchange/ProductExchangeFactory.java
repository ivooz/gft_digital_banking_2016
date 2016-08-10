package com.gft.digitalbank.exchange.solution.service.exchange;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Responsible for creating ProductExchange objects.
 * <p>
 * Created by Ivo Zieliński on 2016-08-09.
 */
@Singleton
public class ProductExchangeFactory {

    private final int processingTaskBufferSize;

    @Inject
    public ProductExchangeFactory(@Named("processing.task.buffer.size") int processingTaskBufferSize) {
        this.processingTaskBufferSize = processingTaskBufferSize;
    }

    /**
     * Creates a new ProductExchange object
     *
     * @param productName for the ProductExchange
     * @return
     */
    public ProductExchange createProductExchange(String productName) {
        return new ProductExchange(productName, processingTaskBufferSize);
    }
}
