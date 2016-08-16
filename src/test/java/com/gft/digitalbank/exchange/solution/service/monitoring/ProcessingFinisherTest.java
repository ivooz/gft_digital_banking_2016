package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

/**
 * Created by iozi on 2016-08-16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ProcessingFinisherTest {

    private ProcessingFinisher sut;

    @Mock
    private CamelContext camelContext;

    @Mock
    private TasksExecutionFinisher tasksExecutionFinisher;

    @Before
    public void initialize() {
        sut = new ProcessingFinisher(tasksExecutionFinisher, camelContext);
    }

    @Test
    public void finishProcessing_whenCalled_shouldStopCamelContextAndInitializeTaskFinishing() {
        try {
            sut.finishProcessing();
            Mockito.verify(camelContext, times(1)).stop();
            Mockito.verify(tasksExecutionFinisher, times(1)).finishAllTasks();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = ProcessingShutdownException.class)
    public void finishProcessing_whenCalledAndExceptionThrownFromCamelContext_shouldInitializeTaskFinishingAndRethrowWrappedException()
            throws Exception {
        doThrow(Exception.class).when(camelContext).stop();
        try {
            sut.finishProcessing();
            Mockito.verify(camelContext, times(1)).stop();
        } catch (ProcessingShutdownException ex) {
            throw ex;
        } finally {
            Mockito.verify(tasksExecutionFinisher, times(1)).finishAllTasks();
        }
    }
}