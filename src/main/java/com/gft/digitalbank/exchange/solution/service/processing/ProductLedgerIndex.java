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
public class ProductLedgerIndex {

    private final Map<String, ProductLedger> productLedgerMap = new HashMap<>();

    public synchronized ProductLedger getLedger(String product) {
        ProductLedger ledger = productLedgerMap.get(product);
        if (ledger == null) {
            ledger = new ProductLedger(product);
            productLedgerMap.put(product, ledger);
        }
        return ledger;
    }

    public Collection<ProductLedger> getAllLedgers() {
        return productLedgerMap.values();
    }
}
