package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import lombok.Data;

import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Created by iozi on 2016-07-06.
 */
@Data
public class TradingMessageQueue {

    private final PriorityQueue<Order> buyOrders = new PriorityQueue<>();
    private final PriorityQueue<Order> sellOrders = new PriorityQueue<>();

    public Optional<Order> getNextOrder(Side side) {
        Order order = null;
        while (true) {
            order = pollOrder(side);
            if (order == null) {
                return Optional.empty();
            }
            if (!order.isScheduledForDeletion()) {
                return Optional.of(order);
            }
        }
    }

    public Optional<Order> peekNextOrder(Side side) {
        Order order = null;
        while (true) {
            order = peekOrder(side);
            if (order == null) {
                return Optional.empty();
            }
            if (order.isScheduledForDeletion()) {
                discardTopOrder(side);
            } else {
                return Optional.of(order);
            }
        }
    }

    public void pushOrder(Order order) {
        switch (order.getSide()) {
            case BUY:
                buyOrders.add(order);
                break;
            case SELL:
                sellOrders.add(order);
                break;
        }
    }

    public Order peekOrder(Side side) {
        switch (side) {
            case BUY:
                return buyOrders.peek();
            case SELL:
                return sellOrders.peek();
        }
        //TODO throw exception?
        return null;
    }

    private Order pollOrder(Side side) {
        switch (side) {
            case BUY:
                return buyOrders.poll();
            case SELL:
                return sellOrders.poll();
        }
        //TODO throw exception?
        return null;
    }

    private void discardTopOrder(Side side) {
        switch (side) {
            case BUY:
                buyOrders.poll();
                break;
            case SELL:
                sellOrders.poll();
                break;
        }
    }
}
