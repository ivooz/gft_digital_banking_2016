package com.gft.digitalbank.exchange.solution.service.scheduling.indexing;

import com.google.inject.Singleton;

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
     * Lets the index know where the Order with the given id resides.
     * @param id
     * @param product
     */
    public void put(int id, String product) {
        idProductMap.put(id, product);
    }

    public Optional<String> get(int id) {
        return Optional.ofNullable(idProductMap.get(id));
    }
}
