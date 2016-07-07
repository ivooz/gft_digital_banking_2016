package com.gft.digitalbank.exchange.solution.service.processing;

import com.google.inject.Singleton;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
@Data
public class ProductExchangeIndex {

    private final Map<String, ProductExchange> productExchangeMap = new HashMap<>();

    public synchronized ProductExchange getLedger(String product) {
        ProductExchange productExchange = productExchangeMap.get(product);
        if (productExchange == null) {
            productExchange = new ProductExchange(product);
            productExchangeMap.put(product, productExchange);
        }
        return productExchange;
    }

    public Collection<ProductExchange> getAllExchanges() {
        return productExchangeMap.values();
    }
}
