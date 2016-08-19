package com.gft.digitalbank.exchange.solution.utils;

import com.gft.digitalbank.exchange.solution.model.Details;

/**
 * Created by iozi on 2016-08-19.
 */
public class DetailsPojoFactory extends PojoFactory {


    public Details createDefaultDetails() {
        return getDetailsBuilderWithDefaultValues().build();
    }

    public Details createDetailsWithPrice(int price) {
        return getDetailsBuilderWithDefaultValues().price(price).build();
    }

    public Details createDetailsWithAmountAndPrice(int amount, int price) {
        return getDetailsBuilderWithDefaultValues().amount(amount).price(price).build();
    }

    private Details.DetailsBuilder getDetailsBuilderWithDefaultValues() {
        return Details.builder()
                .amount(DEFAULT_AMOUNT)
                .price(DEFAULT_PRICE);
    }
}
