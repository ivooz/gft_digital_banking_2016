package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.utils.OrderPojoFactory;
import javafx.util.Pair;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 13/08/16.
 */
@RunWith(JUnitParamsRunner.class)
@Category(UnitTest.class)
public class ProductExchangeTest {

    private static final int PROCESSING_TASK_BUFFER_SIZE = 5;
    private static final String PRODUCT_NAME = "product";

    private ProductExchange sut;
    private OrderPojoFactory orderPojoFactory;

    @Before
    public void initialize() {
        sut = new ProductExchange(PRODUCT_NAME, PROCESSING_TASK_BUFFER_SIZE);
        orderPojoFactory = new OrderPojoFactory();
    }

    @Test(expected = NullPointerException.class)
    public void enqueueOrder_whenPassedNull_shouldThrowNullPointerException() {
        sut.enqueueOrder(null);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "buyAndSellSides")
    public void enqueueOrder_whenPassedOrderScheduledFormDeletion_shouldThrowIllegalArgumentException(Side side) {
        Order order = orderPojoFactory.createNextOrderWithSide(side);
        order.setScheduledForDeletion(true);
        sut.enqueueOrder(order);
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void enqueueOrder_whenPassedOrder_itShouldBeRetrievableFromQueueAndCache(Side side) {
        Order enqueuedOrder = orderPojoFactory.createNextOrderWithSide(side);
        sut.enqueueOrder(enqueuedOrder);
        Order orderFromCache = sut.getById(enqueuedOrder.getId()).get();
        assertThat(orderFromCache, is(sameInstance(enqueuedOrder)));
        Order nextOrderFromQueue = sut.pollNextOrder(side).get();
        assertThat(nextOrderFromQueue, is(sameInstance(enqueuedOrder)));
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void remove_whenPassedOrder_itShouldBeNoLongerRetrievableFromCacheAndQueue(Side side) {
        Order order = orderPojoFactory.createNextOrderWithSide(side);
        sut.enqueueOrder(order);
        sut.remove(order);
        Optional<Order> orderOptional = sut.getById(order.getId());
        assertThat(orderOptional, is(equalTo(Optional.empty())));
        orderOptional = sut.pollNextOrder(side);
        assertThat(orderOptional, is(equalTo(Optional.empty())));
    }

    @Test(expected = NullPointerException.class)
    @Parameters(method = "buyAndSellSides")
    public void remove_whenPassedNull_NullPointerExceptionShouldBeThrown(Side side) {
        sut.remove(null);
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void getById_whenOrderWasEnqueue_itShouldBeRetrievableByItsId(Side side) {
        Order order = orderPojoFactory.createNextOrderWithSide(side);
        sut.enqueueOrder(order);
        Order orderOptional = sut.getById(order.getId()).get();
        assertThat(orderOptional, is(sameInstance(order)));
    }

    @Test
    @Parameters(method = "buyAndSellSides")
    public void executeRemainingTasksAndShutDown_whenMultipleTasksWereEnqueuedConcurrently_theyShouldAllBeExecutedBeforeShuttingDown(Side side) {
        int processingTaskToQueue = 100;
        List<ProcessingTask> processingTasksToEnqueue = new ArrayList<>();
        IntStream.range(0, processingTaskToQueue)
                .forEach(value -> processingTasksToEnqueue.add(Mockito.mock(ProcessingTask.class)));
        processingTasksToEnqueue.parallelStream()
                .forEach(task -> sut.enqueueTask(task));
        try {
            sut.executeRemainingTasksAndShutDown();
        } catch (ExchangeShutdownException e) {
            fail(e.getMessage());
        }
        processingTasksToEnqueue.forEach(processingTask -> Mockito.verify(processingTask).run());
    }

    @Test
    @Parameters(method = "taskCounts")
    public void enqueueTask_whenMultipleTasksAddedConcurrently_onlyHighPriorityTasksShouldBeExecutedWhenBufferOverflows
            (int processingTaskCount) throws InterruptedException {
        int tasksThatShouldBeExecuted = Math.max(0, processingTaskCount - (PROCESSING_TASK_BUFFER_SIZE - 1));
        List<ProcessingTask> lowPriorityTasks = new ArrayList<>();
        IntStream.range(0, processingTaskCount - tasksThatShouldBeExecuted)
                .forEach(value -> {
                    ProcessingTask mock = Mockito.mock(ProcessingTask.class);
                    when(mock.compareTo(Matchers.anyObject())).thenReturn(1);
                    lowPriorityTasks.add(mock);
                });
        List<ProcessingTask> highPriorityTasks = new ArrayList<>();
        IntStream.range(0, tasksThatShouldBeExecuted)
                .forEach(value -> {
                    ProcessingTask mock = Mockito.mock(ProcessingTask.class);
                    when(mock.compareTo(Matchers.anyObject())).thenReturn(-1);
                    highPriorityTasks.add(mock);
                });
        lowPriorityTasks.parallelStream().forEach(task ->
                sut.enqueueTask(task)
        );
        highPriorityTasks.parallelStream().forEach(task ->
                sut.enqueueTask(task)
        );
        //Make sure the task executor finishes all outstanding tasks
        ProcessingTaskExecutorService processingTaskExecutorService = (ProcessingTaskExecutorService) Whitebox.getInternalState(sut, "taskExecutor");
        ThreadPoolExecutor taskExecutor = (ThreadPoolExecutor) Whitebox.getInternalState(processingTaskExecutorService, "taskExecutor");
        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        lowPriorityTasks.forEach(processingTask -> Mockito.verify(processingTask, never()).run());
        highPriorityTasks.forEach(processingTask -> Mockito.verify(processingTask).run());
    }

    @Test(expected = NullPointerException.class)
    public void enqueueTask_whenNullIsPassed_NullPointerExceptionShouldBeThrown() {
        sut.enqueueTask(null);
    }

    @Test
    @Parameters(method = "sidesAndCounts")
    public void peekNextOrder_whenMultipleOrdersEnqueued_thOneWithHighestPriorityShouldBeReturned(Side side, int orderCount) {
        Order highPriorityOrder = Mockito.mock(Order.class);
        when(highPriorityOrder.compareTo(Matchers.anyObject())).thenReturn(-1);
        when(highPriorityOrder.getSide()).thenReturn(side);
        sut.enqueueOrder(highPriorityOrder);
        IntStream.range(0, orderCount)
                .forEach(value -> {
                    Order lowPriorityOrder = Mockito.mock(Order.class);
                    when(lowPriorityOrder.compareTo(Matchers.anyObject())).thenReturn(1);
                    when(lowPriorityOrder.isScheduledForDeletion()).thenReturn(false);
                    if (sut == null) {

                        sut.enqueueOrder(lowPriorityOrder);
                    }
                    when(lowPriorityOrder.getSide()).thenReturn(side);
                });
        Order order = sut.peekNextOrder(side).get();
        assertThat(order, is(sameInstance(highPriorityOrder)));
    }

    @Test(expected = NullPointerException.class)
    public void peekNextOrder_whenPassedNull_NullPointerExceptionShouldBeThrown() {
        sut.peekNextOrder(null);
    }

    @Test
    @Parameters(method = "sidesAndCounts")
    public void pollNextOrder_whenMultipleOrdersEnqueued_thOneWithHighestPriorityShouldBeReturnedOnlyOnce(Side side, int orderCount) {
        Order highPriorityOrder = Mockito.mock(Order.class);
        when(highPriorityOrder.compareTo(Matchers.anyObject())).thenReturn(-1);
        when(highPriorityOrder.getSide()).thenReturn(side);
        sut.enqueueOrder(highPriorityOrder);
        IntStream.range(0, orderCount)
                .forEach(value -> {
                    Order lowPriorityOrder = Mockito.mock(Order.class);
                    when(lowPriorityOrder.compareTo(Matchers.anyObject())).thenReturn(1);
                    when(lowPriorityOrder.getSide()).thenReturn(side);
                    sut.enqueueOrder(lowPriorityOrder);
                });
        Order orderFromQueue = sut.pollNextOrder(side).get();
        assertThat(orderFromQueue, is(sameInstance(highPriorityOrder)));
        orderFromQueue = sut.pollNextOrder(side).get();
        assertThat(orderFromQueue, is(not(sameInstance(highPriorityOrder))));
    }

    public void executeTransaction_whenMatchingOrdersPassed_shouldCreateATransactionAndScheduleOrderFromQueueForDeletion(Pair<Order, Order> orderPairs) {
        Pair<Order, Order> matchingOrders = orderPojoFactory.createIdenticalBuyAndSellOrders();
        Order processedOrder = matchingOrders.getKey();
        Order orderFromQueue = matchingOrders.getValue();
        sut.executeTransaction(processedOrder, orderFromQueue);
        assertThat(sut.getTransactions().size(), is(equalTo(5)));
        assertThat(orderFromQueue.isScheduledForDeletion(), is(equalTo(true)));
        assertThat(orderFromQueue.isFullyTraded(), is(equalTo(true)));
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "buyAndSellSides")
    public void executeTransaction_whenOrdersOfTheSameSidePassed_shouldThrowIllegalArgumentException(Side side) {
        Order firstOrder = orderPojoFactory.createNextOrderWithSide(side);
        Order secondOrder = orderPojoFactory.createNextOrderWithSide(side);
        sut.executeTransaction(firstOrder, secondOrder);
    }

    @Test
    public void executeTransaction_whenMatchingOrdersPassedWithOrderFromQueueWithBiggerAmount_shouldNotMarkOrderFromQueueAsScheduledForDeletion() {
        Pair<Order, Order> matchingOrders = orderPojoFactory.createIdenticalBuyAndSellOrders();
        Order processedOrder = matchingOrders.getKey();
        Order orderFromQueue = matchingOrders.getValue();
        orderFromQueue.getDetails().setAmount(orderFromQueue.getAmount() + 1);
        sut.executeTransaction(processedOrder, orderFromQueue);
        assertThat(orderFromQueue.isScheduledForDeletion(), is(equalTo(false)));
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenFirstNullOrderPassed_shouldThrowNullPointerException() {
        sut.executeTransaction(null, Mockito.mock(Order.class));
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenSecondNullOrderPassed_shouldThrowNullPointerException() {
        sut.executeTransaction(Mockito.mock(Order.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenNullOrdersPassed_shouldThrowNullPointerException() {
        sut.executeTransaction(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void pollNextOrder_whenPassedNull_NullPointerExceptionShouldBeThrown() {
        sut.pollNextOrder(null);
    }

    @Test
    public void getProductName_whenCalled_shouldReturnProductName() {
        String productName = sut.getProductName();
        assertThat(productName, is(equalTo(PRODUCT_NAME)));
    }

    @Test
    public void getTransactions_whenCalled_shouldReturnAnEmptyList() {
        List<Transaction> transactions = sut.getTransactions();
        assertThat(transactions, is(empty()));
    }

    @Test
    @Parameters(method = "taskCounts")
    public void getTransactions_afterManyTransactionsWereExecuted_shouldReturnAListOfProperSize(int transactionCount) {
        IntStream.range(0, transactionCount)
                .forEach(index -> {
                    Pair<Order, Order> matchingOrders = orderPojoFactory.createIdenticalBuyAndSellOrders();
                    sut.executeTransaction(matchingOrders.getKey(), matchingOrders.getValue());
                });
        List<Transaction> transactions = sut.getTransactions();
        assertThat(transactions.size(), is(CoreMatchers.equalTo(transactionCount)));
    }

    private Object sidesAndCounts() {
        return new Object[]{
                new Object[]{Side.BUY, 1},
                new Object[]{Side.SELL, 1},
                new Object[]{Side.BUY, 10},
                new Object[]{Side.SELL, 10},
                new Object[]{Side.BUY, 100},
                new Object[]{Side.SELL, 100},
                new Object[]{Side.BUY, 1000},
                new Object[]{Side.SELL, 1000}
        };
    }


    private Object taskCounts() {
        return new Object[]{
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 50, 100, 200, 1000
        };
    }


    private Object buyAndSellSides() {
        return new Object[]{
                Side.BUY,
                Side.SELL
        };
    }
}