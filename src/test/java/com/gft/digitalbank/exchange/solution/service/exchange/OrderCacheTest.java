package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.utils.OrderPojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Ivo on 11/08/16.
 */
@Category({UnitTest.class})
public class OrderCacheTest {

    private OrderCache sut;
    private OrderPojoFactory orderPojoFactory;

    @Before
    public void initialize() {
        this.sut = new OrderCache();
        this.orderPojoFactory = new OrderPojoFactory();
    }

    @Test
    public void add_whenOrderAdded_itShouldBeRetrievableByItsId() {
        Order order = orderPojoFactory.createNextOrder();
        int orderId = order.getId();
        sut.add(order);
        Order orderFromCache = sut.getById(orderId).get();
        assertThat(orderFromCache, is(sameInstance(order)));
    }

    @Test(expected = NullPointerException.class)
    public void add_whenNullAdded_shouldThrowNullPointerException() {
        sut.add(null);
    }

    @Test
    public void remove_whenOrderRemoved_itShouldNoLongerBeRetrievable() {
        Order order = orderPojoFactory.createNextOrder();
        int orderId = order.getId();
        sut.add(order);
        sut.remove(order);
        Optional<Order> optionalOrder = sut.getById(orderId);
        assertThat(optionalOrder, is(equalTo(Optional.empty())));
    }

    @Test
    public void getById_whenNoOrderWithAGivenIdWasAdded_itShouldReturnEmptyOptional() {
        int givenId = 1;
        sut.getById(givenId);
        Optional<Order> optionalOrder = sut.getById(givenId);
        assertThat(optionalOrder, is(equalTo(Optional.empty())));
    }
}