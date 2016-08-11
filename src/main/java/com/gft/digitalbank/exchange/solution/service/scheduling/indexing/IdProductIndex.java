package com.gft.digitalbank.exchange.solution.service.scheduling.indexing;

import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for holding information about the location of Orders across ProductExchanges Order queues.
 * Used when handling Cancel and Modification TradingMessages that don't know which product the Order they seek to modify
 * is assigned.
 *
 * Created by Ivo Zieliński on 2016-06-28.
 */
@Singleton
public class IdProductIndex {

    private final Map<Integer, String> idProductMap;

    public IdProductIndex() {
        this.idProductMap = new ConcurrentHashMap<>();
    }

    /**
     * Saves the productName name associated with the Order with the given id
     * @param id of the Order
     * @param productName name
     */
    public void put(int id, @NonNull String productName) {
        idProductMap.put(id, productName);
    }

    /**
     * Retrieves the product name of an Order with a given id.
     * @param id of the Order
     * @return product name
     */
    public Optional<String> get(int id) {
        return Optional.ofNullable(idProductMap.get(id));
    }
}
