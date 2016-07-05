package com.gft.digitalbank.exchange.solution.service.processing;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
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

    public String get(int id) {
        return idProductMap.get(id);
    }
}
