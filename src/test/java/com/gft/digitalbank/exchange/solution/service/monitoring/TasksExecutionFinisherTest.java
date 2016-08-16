package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.exchange.ExchangeShutdownException;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.OrderProcessingException;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class TasksExecutionFinisherTest {

    private TasksExecutionFinisher sut;

    @Mock
    private ProductExchangeIndex productExchangeIndex;

    @Mock
    private ProductExchange productExchange;

    @Before
    public void initialize() {
        sut = new TasksExecutionFinisher(productExchangeIndex);
        when(productExchangeIndex.getAllExchanges()).thenReturn(Arrays.asList(productExchange));
    }

    @Test
    public void finishAllTasks_whenCalled_shouldRetrieveAllProductExchangesAndExecuteProcessFinishingProcedure() {
        sut.finishAllTasks();
        try {
            Mockito.verify(productExchange,times(1)).executeRemainingTasksAndShutDown();
        } catch (ExchangeShutdownException | OrderProcessingException e) {
            fail(e.getMessage());
        }
    }

}