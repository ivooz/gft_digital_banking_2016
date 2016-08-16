package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ShutdownNotificationListenerTest {

    private static final int BROKER_COUNT = 5;
    private ShutdownNotificationListener sut;

    @Mock
    private ResultsGatherer resultsGatherer;

    @Mock
    private ProcessingFinisher processingFinisher;

    @Mock
    private ProcessingListener processingListener;

    @Before
    public void initialize() {
        sut = new ShutdownNotificationListener(resultsGatherer, processingFinisher);
    }

    @Test
    public void handleShutdownNotification_whenProcessingFinisherThrowsException_processingListenerShouldBeCalledAnyway()
            throws ProcessingShutdownException, ExecutionException, InterruptedException {
        doThrow(ProcessingShutdownException.class).when(processingFinisher).finishProcessing();
        sut.setBrokerCount(1);
        sut.setProcessingListener(processingListener);

        CompletableFuture<Void> completableFuture = sut.handleShutdownNotification();
        completableFuture.get();

        Mockito.verify(processingListener, times(1)).processingDone(anyObject());
    }

    @Test(expected = NullPointerException.class)
    public void setProcessingListener_whenNullPassed_thenNullPointerExceptionShouldBeThrown() {
        sut.setProcessingListener(null);
    }

    @Test
    public void setBrokerCount_whenBrokerCountSetToX_thenProcessingTaskFinisherAndResultsGathererShouldBeCalledAfterXNotifications() {
        try {
            sut.setProcessingListener(processingListener);
            sut.setBrokerCount(BROKER_COUNT);
            List<CompletableFuture> completableFutureList = new ArrayList<>();
            IntStream.range(0, BROKER_COUNT)
                    .forEach(index -> completableFutureList.add(sut.handleShutdownNotification()));
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).join();
            try {
                Mockito.verify(processingFinisher, times(1)).finishProcessing();
                Mockito.verify(resultsGatherer, times(1)).gatherResults();
                Mockito.verify(processingListener, times(1)).processingDone(anyObject());
            } catch (ProcessingShutdownException e) {
                fail(e.getMessage());
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void setBrokerCount_whenBrokerCountSetToX_thenProcessingTaskFinisherAndResultsGathererShouldNotBeCalledAfterLessThanXNotifications() throws InterruptedException {
        sut.setBrokerCount(BROKER_COUNT);
        sut.setProcessingListener(processingListener);
        List<CompletableFuture> completableFutureList = new ArrayList<>();
        IntStream.range(0, BROKER_COUNT - 1)
                .forEach(index -> completableFutureList.add(sut.handleShutdownNotification()));
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).join();
        try {
            Mockito.verify(processingFinisher, never()).finishProcessing();
            Mockito.verify(resultsGatherer, never()).gatherResults();
            Mockito.verify(processingListener, never()).processingDone(anyObject());
        } catch (ProcessingShutdownException e) {
            fail(e.getMessage());
        }
    }
}