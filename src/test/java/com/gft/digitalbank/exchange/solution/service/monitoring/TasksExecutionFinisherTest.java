package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.exchange.ExchangeShutdownException;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(JUnitParamsRunner.class)
public class TasksExecutionFinisherTest {

    private TasksExecutionFinisher sut;

    private ProductExchangeIndex productExchangeIndex;

    @Before
    public void initialize() {
        productExchangeIndex = Mockito.mock(ProductExchangeIndex.class);
        sut = new TasksExecutionFinisher(productExchangeIndex);
    }

    @Test
    public void finishAllTasks_whenCalled_shouldRetrieveAllProductExchangesAndExecuteProcessFinishingProcedure() {
        ProductExchange productExchange = Mockito.mock(ProductExchange.class);
        when(productExchangeIndex.getAllExchanges()).thenReturn(Arrays.asList(productExchange));
        sut.finishAllTasks();
        try {
            Mockito.verify(productExchange, times(1)).executeRemainingTasksAndShutDown();
        } catch (ExchangeShutdownException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Parameters(method = "exceptionCounts")
    public void finishAllTasks_whenExceptionsThrownDuringTaskFinishing_theyShouldBeContainedInTheReturnedList(int exceptionCount)
            throws ExchangeShutdownException {
        List<ProductExchange> productExchanges = new ArrayList<>();
        for (int i = 0; i < exceptionCount; i++) {
            ProductExchange productExchange = Mockito.mock(ProductExchange.class);
            doThrow(ExchangeShutdownException.class).when(productExchange).executeRemainingTasksAndShutDown();
            productExchanges.add(productExchange);
        }
        when(productExchangeIndex.getAllExchanges()).thenReturn(productExchanges);
        List<Exception> exceptions = sut.finishAllTasks();
        assertThat(exceptions.size(),is(equalTo(exceptionCount)));
    }

    private Object[] exceptionCounts() {
        return new Object[]{
                1, 2, 4, 7, 10, 20, 100, 1000
        };
    }
}