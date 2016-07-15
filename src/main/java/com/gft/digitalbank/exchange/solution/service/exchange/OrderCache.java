package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.model.Order;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iozi on 2016-07-06.
 */
public class OrderCache {

    private final static int MAX_CACHE_SIZE = 500;

    private final Map<Integer, Order> orderCache = new ConcurrentHashMap<>();

    public void add(Order order) {
        orderCache.put(order.getId(), order);
    }

    public void remove(Order order) {
        if (orderCache.size() >= MAX_CACHE_SIZE) {
            CompletableFuture.runAsync(() -> orderCache.remove(order.getId()));
        }
    }

    public Optional<Order> get(int id) {
        return Optional.ofNullable(orderCache.get(id));
    }
}
