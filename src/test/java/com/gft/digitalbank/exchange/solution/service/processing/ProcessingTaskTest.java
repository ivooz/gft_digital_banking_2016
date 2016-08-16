package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ProcessingTaskTest {

    private ProcessingTask sut;

    @Mock
    private TradingMessageProcessor<Order> tradingMessageProcessor;

    @Mock
    private Order order;

    @Mock
    private ProductExchange productExchange;

    @Before
    public void initialize() {
        sut = new ProcessingTask<>(tradingMessageProcessor, order);
    }

    @Test(expected = IllegalStateException.class)
    public void run_whenProductExchangeNotSet_shouldThrowIllegalStateException() {
        sut.run();
    }

    @Test
    public void run_whenProductExchangeIsSet_shouldProcessTradingMessage() {
        sut.setProductExchange(productExchange);
        sut.run();
        Mockito.verify(tradingMessageProcessor, times(1)).processTradingMessage(eq(order), eq(productExchange));
    }

    @Test(expected = NullPointerException.class)
    public void setProductExchange_whenPassedNull_shouldThrowNullPointerException() {
        sut.setProductExchange(null);
    }

    @Test(expected = NullPointerException.class)
    public void compareTo_whenComparedToNull_shouldThrowNullPointerException() {
        sut.compareTo(null);
    }

    @Test
    public void compareTo_whenComparedToItself_shouldReturnZero() {
        int comparison = sut.compareTo(sut);
        assertThat(comparison, is(equalTo(0)));
    }

    @Test
    public void compareTo_whenComparedToProcessingTaskWithHigherTimestampMessage_shouldReturnZero() {
        Order samePriorityOrder = Mockito.mock(Order.class);
        when(samePriorityOrder.getTimestamp()).thenReturn(1L);
        when(order.getTimestamp()).thenReturn(1L);
        ProcessingTask<Order> samePriorityTask = new ProcessingTask<>(tradingMessageProcessor, samePriorityOrder);
        int comparison = sut.compareTo(samePriorityTask);
        assertThat(comparison, is(equalTo(0)));
    }

    @Test
    public void compareTo_whenComparedToProcessingTaskWithLowerTimestampMessage_shouldReturnPositiveNumber() {
        Order higherPriorityOrder = Mockito.mock(Order.class);
        when(higherPriorityOrder.getTimestamp()).thenReturn(1L);
        when(order.getTimestamp()).thenReturn(2L);
        ProcessingTask<Order> higherPriorityTask = new ProcessingTask<>(tradingMessageProcessor, higherPriorityOrder);
        int comparison = sut.compareTo(higherPriorityTask);
        assertThat(comparison, is(greaterThan(0)));
    }

    @Test
    public void compareTo_whenComparedToProcessingTaskWithHigherTimestampMessage_shouldReturnNegativeNumber() {
        Order lowerPriorityOrder = Mockito.mock(Order.class);
        when(lowerPriorityOrder.getTimestamp()).thenReturn(2L);
        when(order.getTimestamp()).thenReturn(1L);
        ProcessingTask<Order> lowerPriortyTask = new ProcessingTask<>(tradingMessageProcessor, lowerPriorityOrder);
        int comparison = sut.compareTo(lowerPriortyTask);
        assertThat(comparison, is(lessThan(0)));
    }


}