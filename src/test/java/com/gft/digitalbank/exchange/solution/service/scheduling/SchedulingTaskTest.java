package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 13/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class SchedulingTaskTest {

    private SchedulingTask firstSchedulingTask;
    private SchedulingTask secondSchedulingTask;
    private ProcessingTask firstProcessingTask;
    private ProcessingTask secondProcessingTask;
    private Order firstOrder;
    private Order secondOrder;

    @Mock
    ProductExchangeIndex productExchangeIndex;

    @Mock
    IdProductIndex idProductIndex;

    @Mock
    ProductExchange productExchange;

    @Before
    public void initialize() {
        firstOrder = Mockito.mock(Order.class);
        firstProcessingTask = Mockito.mock(ProcessingTask.class);
        when(firstProcessingTask.getTradingMessage()).thenReturn(firstOrder);

        secondOrder = Mockito.mock(Order.class);
        secondProcessingTask = Mockito.mock(ProcessingTask.class);
        when(secondProcessingTask.getTradingMessage()).thenReturn(secondOrder);
    }

    @Test(expected = NullPointerException.class)
    public void compareTo_whenPassedNull_shouldThrowNullPointerException() {
        firstSchedulingTask = new OrderSchedulingTask(productExchangeIndex,idProductIndex,firstProcessingTask);
        firstSchedulingTask.compareTo(null);
    }


    @Test
    public void compareTo_whenPassedSchedulingTasksWithDifferentTimestamps_theOneWithLowerTimestampTradingMessageShouldBeFirst() {
        when(firstOrder.getTimestamp()).thenReturn(1L);
        when(secondOrder.getTimestamp()).thenReturn(2L);
        firstSchedulingTask = new OrderSchedulingTask(productExchangeIndex,idProductIndex,firstProcessingTask);
        secondSchedulingTask = new OrderSchedulingTask(productExchangeIndex,idProductIndex,secondProcessingTask);
        assertThat(firstSchedulingTask.compareTo(secondSchedulingTask),is(lessThan(0)));
        assertThat(secondSchedulingTask.compareTo(firstSchedulingTask),is(greaterThan(0)));
    }

    @Test
    public void compareTo_whenPassedSchedulingTasksWithEqualTimestamps_theyShouldBeConsideredOfEqualPriority() {
        when(firstOrder.getTimestamp()).thenReturn(2L);
        when(secondOrder.getTimestamp()).thenReturn(2L);
        firstSchedulingTask = new OrderSchedulingTask(productExchangeIndex,idProductIndex,firstProcessingTask);
        secondSchedulingTask = new OrderSchedulingTask(productExchangeIndex,idProductIndex,secondProcessingTask);
        assertThat(firstSchedulingTask.compareTo(secondSchedulingTask),is(equalTo(0)));
    }

    @Test
    public void compareTo_whenPassedItsOwnReference_theyShouldBeConsideredOfEqualPriority() {
        when(firstOrder.getTimestamp()).thenReturn(2L);
        firstSchedulingTask = new OrderSchedulingTask(productExchangeIndex,idProductIndex,firstProcessingTask);
        assertThat(firstSchedulingTask.compareTo(firstSchedulingTask),is(equalTo(0)));
    }
}