package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 13/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderSchedulingTaskTest {

    public static final String PRODUCT_NAME = "product";
    public static final int ORDER_ID = 5;

    private OrderSchedulingTask sut;

    @Mock
    ProductExchangeIndex productExchangeIndex;

    @Mock
    IdProductIndex idProductIndex;

    @Mock
    ProcessingTask<Order> processingTask;

    @Mock
    ProductExchange productExchange;

    @Mock
    Order order;

    @Before
    public void initialize() {
        sut = new OrderSchedulingTask(productExchangeIndex,idProductIndex,processingTask);
        when(processingTask.getTradingMessage()).thenReturn(order);
        when(order.getId()).thenReturn(ORDER_ID);
        when(order.getProduct()).thenReturn(PRODUCT_NAME);
        when(productExchangeIndex.getProductExchange(PRODUCT_NAME)).thenReturn(productExchange);
    }

    @Test
    public void execute_whenOrderCanBeFound_shouldEnqueueTask() {
        sut.execute();
        Mockito.verify(idProductIndex,times(1)).put(ORDER_ID,PRODUCT_NAME);
        Mockito.verify(productExchange,times(1)).enqueueTask(processingTask);
    }



}