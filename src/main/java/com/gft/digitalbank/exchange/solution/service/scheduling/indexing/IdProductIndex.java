package com.gft.digitalbank.exchange.solution.service.scheduling.indexing;

import com.google.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class IdProductIndex {

    private final Map<Integer, String> idProductMap = new ConcurrentHashMap<>();

    public void put(int id, String product) {
        idProductMap.put(id, product);
    }

    public Optional<String> get(int id) {
        return Optional.ofNullable(idProductMap.get(id));
    }
}