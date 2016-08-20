package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import static org.junit.Assert.fail;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Ivo on 13/08/16.
 */
@Category(UnitTest.class)
public class SchedulingTaskExecutorTest {

    private SchedulingTaskExecutor sut;

    @Before
    public void initialize() {
        sut = new SchedulingTaskExecutor();
    }

    @Test(expected = NullPointerException.class)
    public void executeSchedulingTask_whenPassedNull_shouldThrowNullPointerException() throws OrderNotFoundException {
        sut.executeSchedulingTask(null);
    }

    @Test
    public void executeSchedulingTask_whenPassedSchedulingTask_theTaskShouldGetExecuted() {
        SchedulingTask schedulingTask = Mockito.mock(SchedulingTask.class);
        try {
            sut.executeSchedulingTask(schedulingTask);
        } catch (OrderNotFoundException e) {
            fail(e.getMessage());
        }
        try {
            Mockito.verify(schedulingTask, times(1)).execute();
        } catch (OrderNotFoundException e) {
            fail(e.getMessage());
        }
    }
}