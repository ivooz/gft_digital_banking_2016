package com.gft.digitalbank.exchange.solution.utils;

import com.gft.digitalbank.exchange.solution.model.*;
import javafx.util.Pair;

/**
 * Created by Ivo on 11/08/16.
 */
public class OrderPojoFactory extends PojoFactory {

    private DetailsPojoFactory detailsPojoFactory = new DetailsPojoFactory();

    public Order createDefaultOrder() {
        return getOrderBuilderWithDefaultValues().build();
    }

    public Order createNextOrder() {
        return createNextOrderBuilder().build();
    }

    public Order.OrderBuilder createNextOrderBuilder() {
        counter++;
        return getOrderBuilderWithDefaultValues()
                .id(counter)
                .timestamp(counter);
    }

    public Order createNextOrderWithAmountAndSide(int amount, Side side) {
        Order order = createNextOrderWithSide(side);
        order.getDetails().setAmount(amount);
        return order;
    }

    public Order createNextOrderWithSide(Side side) {
        return createNextOrderBuilder().side(side).build();
    }


    public Order createOrderWithTimestamp(long timestamp) {
        return getOrderBuilderWithDefaultValues().timestamp(timestamp).build();
    }

    public Order createOrderScheduledForDeletionWithSide(Side side) {
        return getOrderBuilderWithDefaultValues().side(side).scheduledForDeletion(true).build();
    }

    public Order createOrderWithPriceAndSide(Side side, int price) {
        return getOrderBuilderWithDefaultValues()
                .side(side)
                .details(detailsPojoFactory.createDetailsWithPrice(price))
                .build();
    }

    public Order createOrderWithTimestampPriceAndSide(Side side, int timestamp, int price) {
        return getOrderBuilderWithDefaultValues()
                .side(side)
                .timestamp(timestamp)
                .details(detailsPojoFactory.createDetailsWithPrice(price))
                .build();
    }

    public Pair<Order, Order> createIdenticalBuyAndSellOrders() {
        return new Pair<>(createNextOrderWithSide(Side.BUY), createNextOrderWithSide(Side.SELL));
    }

    private Order.OrderBuilder getOrderBuilderWithDefaultValues() {
        return Order.builder()
                .broker(DEFAULT_BROKER)
                .client(DEFAULT_CLIENT)
                .details(detailsPojoFactory.createDefaultDetails())
                .id(DEFAULT_ID)
                .side(DEFAULT_SIDE)
                .timestamp(DEFAULT_TIMESTAMP)
                .product(DEFAULT_PRODUCT)
                .scheduledForDeletion(DEFAULT_SCHEDULED_FOR_DELETION);
    }
}
