package com.gft.digitalbank.exchange.solution.utils;

import com.gft.digitalbank.exchange.solution.model.Cancel;

/**
 * Created by iozi on 2016-08-19.
 */
public class CancelPojoFactory extends PojoFactory {

    public static final int DEFAULT_CANCELLED_ORDER_ID = 1;

    public Cancel createDefaultCancel() {
        return Cancel.builder().timestamp(DEFAULT_TIMESTAMP)
                .broker(DEFAULT_BROKER)
                .id(DEFAULT_ID)
                .cancelledOrderId(DEFAULT_CANCELLED_ORDER_ID).build();
    }

    public Cancel createCancelWithCancelledOrderId(int cancelledOrderId) {
        return getCancelBuilderWithDefaultValues()
                .cancelledOrderId(cancelledOrderId).build();
    }

    private Cancel.CancelBuilder getCancelBuilderWithDefaultValues() {
        return Cancel.builder().timestamp(DEFAULT_TIMESTAMP)
                .broker(DEFAULT_BROKER)
                .id(DEFAULT_ID)
                .cancelledOrderId(DEFAULT_CANCELLED_ORDER_ID);
    }
}
