package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Ivo on 12/08/16.
 */
@Category(UnitTest.class)
@RunWith(JUnitParamsRunner.class)
public class ProcessingTaskExecutorServiceTest {

    private ProcessingTaskExecutorService sut;

    @Before
    public void initialize() {
        ProductExchange productExchange = Mockito.mock(ProductExchange.class);
        this.sut = new ProcessingTaskExecutorService(productExchange);
    }

    @Test(expected = NullPointerException.class)
    public void noArgConstructor_whenPassedNull_shouldThrowNullPointerException() {
        new ProcessingTaskExecutorService(null);
    }

    @Test(expected = NullPointerException.class)
    public void enqueueProcessingTask_whenPassedNull_shouldThrowNullPointerException() {
        sut.enqueueProcessingTask(null);
    }

    @Test
    @Parameters(method = "taskCounts")
    public void shutdownAndAwaitTermination_whenCalled_shouldFinishAllTasksBeforeReturning(int taskCount) {
        List<ProcessingTask> processingTasks = new CopyOnWriteArrayList<>();
        IntStream.range(0, taskCount).parallel()
                .forEach(value -> {
                    ProcessingTask processingTask = Mockito.mock(ProcessingTask.class);
                    processingTasks.add(processingTask);
                    sut.enqueueProcessingTask(processingTask);
                });
        try {
            sut.shutdownAndAwaitTermination();
        } catch (ExchangeShutdownException e) {
            fail(e.getMessage());
        }
        processingTasks.forEach(processingTask -> Mockito.verify(processingTask, times(1)).run());
    }

    @Test(expected = ExchangeShutdownException.class)
    public void shutdownAndAwaitTermination_whenExecutorThrowsException_itShouldBeWrappedAndRethrown()
            throws InterruptedException, ExchangeShutdownException {
        ThreadPoolExecutor threadPoolExecutor = Mockito.mock(ThreadPoolExecutor.class);
        doThrow(InterruptedException.class).when(threadPoolExecutor).awaitTermination(anyLong(),anyObject());
        Whitebox.setInternalState(sut,"taskExecutor",threadPoolExecutor);
        sut.shutdownAndAwaitTermination();
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
        Mockito.verify(processingTask, times(1)).run();
    }

    @Test
    public void enqueueProcessingTask_whenPassedAProcessingTasksAndThenShutdown_theTaskWithHigherPriorityShouldBeExecutedFirst() {
        ProcessingTask processingTaskWithHighPriority = Mockito.mock(ProcessingTask.class);
        ProcessingTask processingTaskWithLowPriority = Mockito.mock(ProcessingTask.class);
        when(processingTaskWithHighPriority.compareTo(Matchers.anyObject())).thenReturn(-1);
        when(processingTaskWithLowPriority.compareTo(Matchers.anyObject())).thenReturn(1);
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

    private Object[] taskCounts() {
        return new Object[]{
                1, 2, 4, 8, 16, 32, 64, 128, 256
        };
    }
}