package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;

/**
 * Created by Ivo on 13/08/16.
 */
@RunWith(JUnitParamsRunner.class)
@Category(UnitTest.class)
public class ProductExchangeTest {

    public static final int PROCESSING_TASK_BUFFER_SIZE = 10;
    private ProductExchange sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        sut = new ProductExchange("product", PROCESSING_TASK_BUFFER_SIZE);
        pojoFactory = new PojoFactory();
    }

    @Test(expected = NullPointerException.class)
    public void enqueue_whenPassedNull_shouldThrowNullPointerException() {
        sut.enqueueTask(null);
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void enqueue_whenPassedOrder_itShouldBeRetrievableFromQueueAndCache(Side side) {
        Order enqueuedOrder = pojoFactory.createNextOrderWithSide(side);
        sut.enqueue(enqueuedOrder);
        Order orderFromCache = sut.getById(enqueuedOrder.getId()).get();
        assertThat(orderFromCache,is(sameInstance(enqueuedOrder)));
        Order nextOrderFromQueue = sut.getNextOrder(side).get();
        assertThat(nextOrderFromQueue,is(sameInstance(enqueuedOrder)));
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "buyAndSellSides")
    public void enqueue_whenPassedOrderScheduledForDeletion_IllegalArgumentExceptionShouldBeThrown(Side side) {
        Order enqueuedOrder = pojoFactory.createOrderScheduledForDeletion(side);
        sut.enqueue(enqueuedOrder);
    }



    private Object buyAndSellSides() {
        return new Object[] {
                Side.BUY,
                Side.SELL
        };
    }
}