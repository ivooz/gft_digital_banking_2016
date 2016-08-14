package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.utils.AsyncTaskRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

/**
 * Created by Ivo on 12/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ProcessingTaskExecutorServiceTest {

    private ProcessingTaskExecutorService sut;
    private AsyncTaskRunner asyncTaskRunner;

    @Mock
    ProductExchange productExchange;

    @Before
    public void initialize() {
        this.sut = new ProcessingTaskExecutorService(productExchange);
        this.asyncTaskRunner = new AsyncTaskRunner();
    }

    @Test(expected = NullPointerException.class)
    public void enqueueProcessingTask_whenPassedNull_shouldThrowNullPointerException() {
        sut.enqueueProcessingTask(null);
    }

    @Test(expected = NullPointerException.class)
    public void noArgConstructor_whenPassedNull_shouldThrowNullPointerException() {
        new ProcessingTaskExecutorService(null);
    }

    @Test
    public void enqueueProcessingTask_whenPassedAProcessingTaskAndThenShutdown_theTaskShouldBeExecuted() {
        ProcessingTask processingTask = Mockito.mock(ProcessingTask.class);
        sut.enqueueProcessingTask(processingTask);
        try {
            sut.shutdownAndAwaitTermination();
        } catch (ExchangeShutdownException e) {
            fail(e.getMessage());
        }
        Mockito.verify(processingTask,times(1)).run();
    }

    @Test
    public void enqueueProcessingTask_whenPassedAProcessingTasksAndThenShutdown_theTaskWithHigherPriorityShouldBeExecutedFirst() {
        ProcessingTask processingTaskWithHighPriority = Mockito.mock(ProcessingTask.class);
        ProcessingTask processingTaskWithLowPriority = Mockito.mock(ProcessingTask.class);
        Mockito.when(processingTaskWithHighPriority.compareTo(Matchers.anyObject())).thenReturn(-1);
        Mockito.when(processingTaskWithLowPriority.compareTo(Matchers.anyObject())).thenReturn(1);
        InOrder inOrder = inOrder(processingTaskWithHighPriority, processingTaskWithLowPriority);
        sut.enqueueProcessingTask(processingTaskWithHighPriority);
        sut.enqueueProcessingTask(processingTaskWithLowPriority);
        try {
            sut.shutdownAndAwaitTermination();
        } catch (ExchangeShutdownException e) {
            fail(e.getMessage());
        }
        inOrder.verify(processingTaskWithHighPriority).run();
        inOrder.verify(processingTaskWithLowPriority).run();
    }
}