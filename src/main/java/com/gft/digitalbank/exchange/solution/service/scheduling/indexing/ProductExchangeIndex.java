package com.gft.digitalbank.exchange.solution.service.scheduling.indexing;

import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Indexes ProductExchanges on their product name.
 * <p>
 * Created by Ivo Zieliński on 2016-06-28.
 */
@Singleton
public class ProductExchangeIndex {

    private final ProductExchangeFactory productExchangeFactory;
    private final Map<String, ProductExchange> productExchangeMap;

    @Inject
    public ProductExchangeIndex(ProductExchangeFactory productExchangeFactory) {
        this.productExchangeFactory = productExchangeFactory;
        this.productExchangeMap = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the ProductExchange with the given product name
     *
     * @param productName of the ProductExchange to retrieve
     * @return ProductExchange with the given name
     */
    public ProductExchange getProductExchange(@NonNull String productName) {
        ProductExchange productExchange = productExchangeMap.get(productName);
        if (productExchange == null) {
            productExchangeMap.putIfAbsent(productName, productExchangeFactory.createProductExchange(productName));
            return productExchangeMap.get(productName);
        }
        return productExchange;
    }

    public Collection<ProductExchange> getAllExchanges() {
        return productExchangeMap.values();
    }
}
