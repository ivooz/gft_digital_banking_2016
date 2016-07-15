package com.gft.digitalbank.exchange.solution.service.exchange;

import com.google.inject.Singleton;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
@Data
public class ProductExchangeIndex {

    private final Map<String, ProductExchange> productExchangeMap = new ConcurrentHashMap<>();

    public ProductExchange getLedger(String product) {
        ProductExchange productExchange = productExchangeMap.get(product);
        if (productExchange == null) {
            productExchangeMap.putIfAbsent(product,new ProductExchange(product));
            return productExchangeMap.get(product);
        }
        return productExchange;
    }

    public Collection<ProductExchange> getAllExchanges() {
        return productExchangeMap.values();
    }
}
