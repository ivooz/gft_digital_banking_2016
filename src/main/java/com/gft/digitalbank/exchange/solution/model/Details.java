package com.gft.digitalbank.exchange.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by iozi on 2016-06-27.
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Details {

    private int amount;
    private int price;

    public Details(Details details) {
        this.amount = details.getAmount();
        this.price = details.getPrice();
    }
}
