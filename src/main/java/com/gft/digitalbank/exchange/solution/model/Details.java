package com.gft.digitalbank.exchange.solution.model;

import lombok.*;

/**
 * Represents the additional details associated with new Cancel, Order and Modification messages.
 * <p>
 * Created by Ivo Zieliński on 2016-06-27.
 */
@Data
@Builder
@AllArgsConstructor
public class Details {

    private int amount;
    private final int price;

    /**
     * Copying constructor
     *
     * @param details to copy
     */
    public Details(@NonNull Details details) {
        this.amount = details.getAmount();
        this.price = details.getPrice();
    }
}
