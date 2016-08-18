package com.gft.digitalbank.exchange.solution.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Represents the Order message.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Order extends TradingMessage implements Comparable<Order> {

    @NonNull
    private String client;

    @NonNull
    private Side side;

    @NonNull
    private String product;

    private Details details;

    @NonNull
    private boolean scheduledForDeletion;

    @Builder
    private Order(int id, long timestamp, @NonNull String broker, @NonNull String client, @NonNull Side side, @NonNull String product,
                  @NonNull Details details, boolean scheduledForDeletion) {
        super(id, timestamp, broker);
        this.client = client;
        this.side = side;
        this.product = product;
        this.details = details;
        this.scheduledForDeletion = scheduledForDeletion;
    }

    /**
     * Copying constructor.
     *
     * @param order to be copied
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
     * @return if amount equals 0
     */
    public boolean isFullyTraded() {
        return getAmount() == 0;
    }

    /**
     * A buy order with the higher prices has precedence.
     * A sell order with lower price has precedence.
     * If prices are equal, the one with smaller timestamp has precedence.
     *
     * @param otherOrder to be compared
     * @return the ordering
     */
    @Override
    public int compareTo(@NonNull Order otherOrder) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == otherOrder) {
            return EQUAL;
        }

        if (this.getSide() == otherOrder.getSide()) {
            int comparison = getPrice() - otherOrder.getPrice();
            if (comparison > 0) {
                return this.side == Side.BUY ? BEFORE : AFTER;
            } else if (comparison < 0) {
                return this.side == Side.BUY ? AFTER : BEFORE;
            }
        }
        return (int) (this.getTimestamp() - otherOrder.getTimestamp());
    }
}
