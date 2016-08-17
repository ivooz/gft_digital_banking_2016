package com.gft.digitalbank.exchange.solution.utils;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Details;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.rits.cloning.Cloner;
import javafx.util.Pair;

/**
 * Created by Ivo on 11/08/16.
 */
public class PojoFactory {

    public static final int MODIFIED_ORDER_ID = 5;
    private int counter;

    private Order prototype = Order.builder().broker("broker")
            .client("client")
            .details(createDetailsWithAmountAndPrice(1, 1))
            .id(0)
            .side(Side.BUY)
            .timestamp(0)
            .product("product")
            .scheduledForDeletion(false).build();

    private Cloner cloner = new Cloner();

    public Order createNextOrder() {
        Order order = createOrder();
        counter++;
        order.setId(counter);
        order.setTimestamp(counter);
        return order;
    }

    public Details createDetails() {
        return cloner.deepClone(prototype.getDetails());
    }

    public Order createNextOrderWithAmountAndSide(int amount, Side side) {
        Order order = createNextOrderWithSide(side);
        order.getDetails().setAmount(amount);
        return order;
    }

    public Order createNextOrderWithSide(Side side) {
        Order order = createNextOrder();
        order.setSide(side);
        return order;
    }

    public Order createOrder() {
        return cloner.deepClone(prototype);
    }

    public Order createOrderScheduledForDeletion(Side side) {
        Order order = createOrder();
        order.setSide(side);
        order.setScheduledForDeletion(true);
        return order;
    }

    public Order buildOrderWithPriceAndSide(Side side, int price) {
        Order order = createOrder();
        order.setSide(side);
        order.getDetails().setPrice(price);
        return order;
    }

    public Order buildOrderWithTimestampPriceAndSide(Side side, int timestamp, int price) {
        Order order = createOrder();
        order.setSide(side);
        order.setTimestamp(timestamp);
        order.getDetails().setPrice(price);
        return order;
    }

    public Modification createModification(int modifiedOrderId, int amount, int price) {
        return new Modification(modifiedOrderId, createDetailsWithAmountAndPrice(amount, price));
    }

    public Details createDetailsWithAmountAndPrice(int amount, int price) {
        return Details.builder().amount(amount).price(price).build();
    }

    public Transaction createNextTransaction() {
        return Transaction.builder().amount(1).brokerBuy("broker").brokerSell("brokerSell").id(++counter).clientBuy("clientBuy")
                .clientSell("clientSell").price(1).product("product").build();
    }

    public Pair<Order, Order> createIdenticalBuyAndSellOrders() {
        return new Pair<>(createNextOrderWithSide(Side.BUY), createNextOrderWithSide(Side.SELL));
    }
}
