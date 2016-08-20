package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.test.utils.OrderPojoFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    private OrderPojoFactory orderPojoFactory;

    @Mock
    private TradingMessageProcessor<Order> tradingMessageProcessor;

    @Mock
    private Order order;

    @Mock
    private ProductExchange productExchange;

    @Before
    public void initialize() {
        sut = new ProcessingTask<>(tradingMessageProcessor, order);
        orderPojoFactory = new OrderPojoFactory();
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
        ProcessingTask<Order> lowerPriorityTask = new ProcessingTask<>(tradingMessageProcessor, lowerPriorityOrder);
        int comparison = sut.compareTo(lowerPriorityTask);
        assertThat(comparison, is(lessThan(0)));
    }

    @Test
    public void equals_whenPassedItsOwnReference_shouldReturnTrue() {
        assertTrue(sut.equals(sut));
    }

    @Test
    public void equals_whenPassedProcessingTaskWithTheSameOrder_shouldReturnTrue() {
        Order order = orderPojoFactory.createDefault();
        ProcessingTask processingTask = new ProcessingTask(tradingMessageProcessor, order);
        ProcessingTask processingTaskWithSameDataOrder = new ProcessingTask(tradingMessageProcessor, order);
        assertTrue(processingTask.equals(processingTaskWithSameDataOrder));
        assertTrue(processingTaskWithSameDataOrder.equals(processingTask));
    }

    @Test
    public void equals_whenPassedProcessingTaskWithDifferentOrder_shouldReturnFalse() {
        Order order = orderPojoFactory.createDefault();
        Order differentOrder = orderPojoFactory.createOrderWithTimestamp(1000);
        ProcessingTask processingTask = new ProcessingTask(tradingMessageProcessor, order);
        ProcessingTask processingTaskWithDifferentOrder = new ProcessingTask(tradingMessageProcessor, differentOrder);
        assertFalse(processingTask.equals(processingTaskWithDifferentOrder));
        assertFalse(processingTaskWithDifferentOrder.equals(processingTask));
    }

    @Test
    public void equals_whenPassedNull_shouldReturnFalse() {
        assertFalse(sut.equals(null));
    }

    @Test
    public void hashCode_whenTwoAreEqual_theyShouldReturnTheSameHashCode() {
        Order order = orderPojoFactory.createDefault();
        ProcessingTask processingTask = new ProcessingTask(tradingMessageProcessor, order);
        ProcessingTask processingTaskWithSameDataOrder = new ProcessingTask(tradingMessageProcessor, order);
        assertTrue(processingTask.equals(processingTaskWithSameDataOrder));
        assertTrue(processingTaskWithSameDataOrder.equals(processingTask));
        assertEquals(processingTask.hashCode(), processingTaskWithSameDataOrder.hashCode());
    }
}