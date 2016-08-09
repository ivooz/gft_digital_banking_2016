package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.model.Order;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Acts as an index of Orders by their id.
 * Used by Modification and Cancel ProcessingTasks.
 * <p>
 * Created by iozi on 2016-07-06.
 */
public class OrderCache {

    private final Map<Integer, Order> orderCache;

    public OrderCache() {
        this.orderCache = new ConcurrentHashMap<>();
    }

    /**
     * Adds the order to the cache. It can be later retrieved by its id.
     *
     * @param order
     */
    public void add(@NonNull Order order) {
        orderCache.put(order.getId(), order);
    }

    /**
     * Removes the order from the cache.
     * This removal is initiated in a separate thread as it is a low priority task.
     *
     * @param order to remove from cache
     */
    public void remove(@NonNull Order order) {
            CompletableFuture.runAsync(() -> orderCache.remove(order.getId()));
    }

    /**
     * Retrieves the Order from the cache by its id.
     *
     * @param id
     * @return cached Order
     */
    public Optional<Order> getById(int id) {
        return Optional.ofNullable(orderCache.get(id));
    }
}