package com.gft.digitalbank.exchange.solution.model;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the Order message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends TradingMessage implements Comparable<Order> {

    private String client;
    private Side side;
    private String product;
    private Details details;
    private boolean scheduledForDeletion;

    /**
     * Copying constructor.
     *
     * @param order
     */
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

    /**
     * The amount to buy/sell has been fulfilled.
     *
     * @return
     */
    public boolean isFullyProcessed() {
        return getAmount() == 0;
    }

    /**
     * A buy order with the higher prices has precedence.
     * A sell order with lower price has precedence.
     * If prices are equal, the one with smaller timestamp has precedence.
     *
     * @param otherOrder
     * @return
     */
    @Override
    public int compareTo(Order otherOrder) {
        Preconditions.checkNotNull(otherOrder, "Cannot compare with null.");
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == otherOrder) {
            return EQUAL;
        }

        int comparison = getPrice() - otherOrder.getPrice();
        if (comparison > 0) {
            return this.side == Side.BUY ? BEFORE : AFTER;
        } else if (comparison < 0) {
            return this.side == Side.BUY ? AFTER : BEFORE;
        }
        return (int) (this.getTimestamp() - otherOrder.getTimestamp());
    }
}
