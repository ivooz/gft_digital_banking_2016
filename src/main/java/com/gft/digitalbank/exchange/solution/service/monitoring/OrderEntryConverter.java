package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-30.
 */
@Singleton
public class OrderEntryConverter {

    public OrderEntry convertToOrderEntry(Order order) {
        return OrderEntry.builder().amount(order.getDetails().getAmount())
                .broker(order.getBroker())
                .client(order.getClient())
                .id(order.getId())
                .price(order.getPrice()).build();
    }
}
