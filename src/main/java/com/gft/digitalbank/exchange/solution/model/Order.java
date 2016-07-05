package com.gft.digitalbank.exchange.solution.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by
 * iozi on 2016-06-27.
 */
@Data
@Builder
@AllArgsConstructor
public class Order extends TradingMessage implements Comparable {

    private String client;
    private Side side;
    private String product;
    private Details details;
    private boolean scheduledForDeletion;

    public Order(Order order) {
        super(order.getId(), order.getTimestamp(), order.getBroker());
        this.client = order.client;
        this.side = order.side;
        this.product = order.product;
        this.scheduledForDeletion = order.scheduledForDeletion;
        this.details = new Details(order.details);
    }

    public int getPrice() {
        return details.getPrice();
    }

    public int getAmount() {
        return details.getAmount();
    }

    @Override
    public int compareTo(Object o) {
        Order otherOrder = (Order) o;
        int comparison = getPrice() - otherOrder.getPrice();
        switch (this.side) {
            case BUY:
                if (comparison > 0) {
                    return -1;
                } else if (comparison < 0) {
                    return 1;
                }
            case SELL:
                if (comparison > 0) {
                    return 1;
                } else if (comparison < 0) {
                    return -1;
                }
        }
        return (int) (this.getTimestamp() - otherOrder.getTimestamp());
    }

    @Override
    public String toString() {
        return "Order{" +
                ", client='" + client + '\'' +
                ", id='" + getId() + '\'' +
                ", broker='" + getBroker() + '\'' +
                ", timestamp='" + getTimestamp() + '\'' +
                ", side=" + side +
                ", product='" + product + '\'' +
                ", details=" + details +
                ", scheduledForDeletion=" + scheduledForDeletion +
                '}';
    }
}
