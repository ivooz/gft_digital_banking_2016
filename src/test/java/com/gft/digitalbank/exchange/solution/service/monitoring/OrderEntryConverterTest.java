package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import com.gft.digitalbank.exchange.verification.FunctionalTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by iozi on 2016-08-10.
 */
@Category({UnitTest.class})
public class OrderEntryConverterTest {

    private OrderEntryConverter sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        sut = new OrderEntryConverter();
        pojoFactory = new PojoFactory();
    }

    @Test(expected = NullPointerException.class)
    public void convertToOrderEntry_whenPassedNull_shouldThrowNullPointerException() {
        sut.convertToOrderEntry(null);
    }

    @Test
    public void convertToOrderEntry_whenPassedOrder_shouldReturnOrderEntryWithTheSameData() {
        Order order = pojoFactory.createNextOrder();
        OrderEntry orderEntry = sut.convertToOrderEntry(order);
        assertThat(orderEntry.getAmount(),is(equalTo(order.getAmount())));
        assertThat(orderEntry.getPrice(),is(equalTo(order.getPrice())));
        assertThat(orderEntry.getBroker(),is(equalTo(order.getBroker())));
        assertThat(orderEntry.getClient(),is(equalTo(order.getClient())));
        assertThat(orderEntry.getId(),is(equalTo(order.getId())));
    }
}
