package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import lombok.NonNull;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Holds the state of buy and sell Order queues.
 * Uses priority queues to enforce the correct processing Order.
 * Responsible from lazy removal of Orders from queues.
 * <p>
 * Created by Ivo Zieliński on 2016-07-06.
 */
public class OrderQueue {

    private final PriorityQueue<Order> buyOrders = new PriorityQueue<>();
    private final PriorityQueue<Order> sellOrders = new PriorityQueue<>();

    /**
     * Retrieves the next Order to process from the Queue. The Order will be removed from the Queue.
     * If the retrieved order has been marked for deletion by a Cancel or has been 'fully traded', it will be ignored
     * and the next Order will be retrieved.
     *
     * @param side
     * @return the top Order
     */
    public Optional<Order> getNextOrder(@NonNull Side side) {
        Optional<Order> orderOptional;
        while (true) {
            orderOptional = pollOrder(side);
            if (!orderOptional.isPresent()) {
                return orderOptional;
            }
            if (!orderOptional.get().isScheduledForDeletion()) {
                return orderOptional;
            }
        }
    }

    /**
     * Peeks the next Order to process from the Queue. The Order will not be removed from the Queue.
     * If the retrieved order has been marked for deletion by a Cancel or has been 'fully traded', it will be ignored
     * and the next Order will be peeked.
     *
     * @param side
     * @return the top Order
     */
    public Optional<Order> peekNextOrder(@NonNull Side side) {
        Optional<Order> orderOptional;
        while (true) {
            orderOptional = peekOrder(side);
            if (!orderOptional.isPresent()) {
                return orderOptional;
            }
            if (orderOptional.get().isScheduledForDeletion()) {
                discardTopOrder(side);
            } else {
                return orderOptional;
            }
        }
    }

    /**
     * Pushes Order onto the proper queue.
     *
     * @param order to add
     */
    public void pushOrder(@NonNull Order order) {
        consumeQueue(order.getSide(), queue -> queue.add(order));
    }

    private Optional<Order> peekOrder(Side side) {
        return applyToQueue(side, queue -> queue.peek());
    }

    private Optional<Order> pollOrder(Side side) {
        return applyToQueue(side, queue -> queue.poll());
    }

    private void discardTopOrder(Side side) {
        consumeQueue(side, queue -> queue.poll());
    }

    private Optional<Order> applyToQueue(Side side, Function<PriorityQueue<Order>, Order> function) {
        switch (side) {
            case BUY:
                return Optional.ofNullable(function.apply(buyOrders));
            case SELL:
                return Optional.ofNullable(function.apply(sellOrders));
        }
        return Optional.empty();
    }

    private void consumeQueue(Side side, Consumer<PriorityQueue<Order>> consumer) {
        switch (side) {
            case BUY:
                consumer.accept(buyOrders);
                break;
            case SELL:
                consumer.accept(sellOrders);
                break;
        }
    }
}
