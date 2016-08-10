package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Singleton;
import lombok.NonNull;

/**
 * Responsible for converting Orders into OrderEntry objects that are expected by the ProcessingListener.
 *
 * Created by Ivo Zieliński on 2016-06-30.
 */
@Singleton
public class OrderEntryConverter {

    /**
     * Maps Order to OrderEntry
     * @param order to map
     * @return mapped OrderEntry
     */
    public OrderEntry convertToOrderEntry(@NonNull Order order) {
        return OrderEntry.builder().amount(order.getDetails().getAmount())
                .broker(order.getBroker())
                .client(order.getClient())
                .id(order.getId())
                .price(order.getPrice()).build();
    }
}
