package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 12/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ProcessingTaskQueueTest {

    private final static int BUFFER_SIZE = 5;
    private ProcessingTaskQueue sut;

    @Mock
    private ProcessingTask<Order> orderProcessingTask;

    @Before
    public void initialize() {
        this.sut = new ProcessingTaskQueue(BUFFER_SIZE);
    }

    @Test
    public void isNotEmpty_whenJustCreated_shouldReturnFalse() {
        assertThat(sut.isNotEmpty(), is(equalTo(false)));
    }

    @Test
    public void isNotEmpty_afterAddingProcessingTask_shouldReturnTrue() {
        sut.enqueueTask(orderProcessingTask);
        assertThat(sut.isNotEmpty(), is(equalTo(true)));
    }

    @Test
    public void isEmpty_whenJustCreated_shouldReturnTrue() {
        assertThat(sut.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void isEmpty_afterAddingProcessingTask_shouldReturnFalse() {
        sut.enqueueTask(orderProcessingTask);
        assertThat(sut.isEmpty(), is(equalTo(false)));
    }

    @Test
    public void isFull_ifBufferFull_shouldReturnTrue() {
        fillTheQueue();
        assertThat(sut.isFull(), is(equalTo(true)));
    }

    @Test
    public void isFull_whenEmpty_shouldReturnFalse() {
        assertThat(sut.isFull(), is(equalTo(false)));
    }

    @Test
    public void isFull_ifFullAndThenAnProcessingTaskIsRemove_shouldReturnFalse() {
        fillTheQueue();
        sut.getNextTaskToExecute();
        assertThat(sut.isFull(), is(equalTo(false)));
    }

    @Test
    public void getNextTaskToExecute_whenEmpty_shouldReturnEmptyOptional() {
        Optional<ProcessingTask> nextTaskToExecute = sut.getNextTaskToExecute();
        assertThat(nextTaskToExecute, is(equalTo(Optional.empty())));
    }

    @Test
    public void getNextTaskToExecute_whenMultipleProcessingTasksAdded_shouldReturnAProcessingTaskWithHighestPriority() {
        fillTheQueue();
        ProcessingTask processingTaskWithHighestPriority = Mockito.mock(ProcessingTask.class);
        when(processingTaskWithHighestPriority.compareTo(Matchers.anyObject())).thenReturn(-1);
        sut.enqueueTask(processingTaskWithHighestPriority);
        ProcessingTask nextTaskToExecute = sut.getNextTaskToExecute().get();
        assertThat(nextTaskToExecute, is(sameInstance(processingTaskWithHighestPriority)));
    }

    @Test(expected = NullPointerException.class)
    public void enqueueTask_whenPassedNull_shouldThrowNullPointerException() {
        sut.enqueueTask(null);
    }

    private void fillTheQueue() {
        IntStream.range(0, BUFFER_SIZE).parallel()
                .forEach(value -> sut.enqueueTask(Mockito.mock(ProcessingTask.class)));
    }
}