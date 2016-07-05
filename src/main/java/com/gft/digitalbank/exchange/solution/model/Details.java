package com.gft.digitalbank.exchange.solution.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by iozi on 2016-06-27.
 */
@Data
@Builder
@AllArgsConstructor
public class Details {

    private int amount;
    private int price;

    public Details(Details details) {
        this.amount = details.getAmount();
        this.price = details.getPrice();
    }
}
