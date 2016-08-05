package com.gft.digitalbank.exchange.solution.service.scheduling.indexing;

import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class ProductExchangeIndex {

    private final Map<String, ProductExchange> productExchangeMap = new ConcurrentHashMap<>();

    public ProductExchange getLedger(String productName) {
        ProductExchange productExchange = productExchangeMap.get(productName);
        if (productExchange == null) {
            productExchangeMap.putIfAbsent(productName,new ProductExchange(productName));
            return productExchangeMap.get(productName);
        }
        return productExchange;
    }

    public Collection<ProductExchange> getAllExchanges() {
        return productExchangeMap.values();
    }
}
