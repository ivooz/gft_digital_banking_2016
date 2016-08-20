package com.gft.digitalbank.exchange.solution.test.utils;

import com.gft.digitalbank.exchange.solution.model.Modification;

/**
 * Created by Ivo Zieliński on 2016-08-19.
 */
public class ModificationPojoFactory extends PojoFactory<Modification> {

    private DetailsPojoFactory detailsPojoFactory = new DetailsPojoFactory();

    /**
     * {@inheritDoc}
     */
    public Modification createDefault() {
        return getModificationBuilderWithDefaultValues().build();
    }

    public Modification createModificationWithBroker(String broker) {
        return getModificationBuilderWithDefaultValues().broker(broker).build();
    }

    public Modification createModificationWithModifiedOrderIdAmountAndPrice(int modifiedOrderId, int amount, int price) {
        return getModificationBuilderWithDefaultValues()
                .modifiedOrderId(modifiedOrderId)
                .details(detailsPojoFactory.createDetailsWithAmountAndPrice(amount,price))
                .build();
    }

    private Modification.ModificationBuilder getModificationBuilderWithDefaultValues() {
        return Modification.builder()
                .broker(DEFAULT_BROKER)
                .details(detailsPojoFactory.createDefault())
                .id(DEFAULT_ID)
                .modifiedOrderId(DEFAULT_MODIFIED_ORDER_ID)
                .timestamp(DEFAULT_TIMESTAMP);
    }
}
