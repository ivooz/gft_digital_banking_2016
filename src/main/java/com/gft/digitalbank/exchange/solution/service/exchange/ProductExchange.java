package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.processing.OrderProcessingException;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Holds the entire state associated with trading a single product.
 * Encapsulates business logic associated with the Order processing order and synchronization of ProcessingTasks
 * execution.
 *
 * Created by iozi on 2016-07-06.
 */
public class ProductExchange {

    private final String productName;
    private final ProductTransactionLedger productTransactionLedger;
    private final OrderQueue orderQueue;
    private final ProcessingTaskExecutor taskExecutor;
    private final ProcessingTaskQueue processingTaskQueue;
    private final OrderCache orderCache;

    public ProductExchange(@NonNull String productName, int processingTaskBufferSize) {
        this.productName = productName;
        this.processingTaskQueue = new ProcessingTaskQueue(processingTaskBufferSize);
        this.productTransactionLedger = new ProductTransactionLedger();
        this.orderCache = new OrderCache();
        this.taskExecutor = new ProcessingTaskExecutor(this);
        this.orderQueue = new OrderQueue();
    }

    /**
     * Adds the Order to the cache for further retrieval as well as queues it for later matching.
     * This method is invoked to add Orders that have not been fully 'traded' or/and no match for them has been found.
     * @param order to enqueue
     */
    public void enqueue(@NonNull Order order) {
        orderCache.add(order);
        orderQueue.pushOrder(order);
    }

    /**
     * Must be executed when an Order has been 'fully traded' by matching it with and Order of the opposite Side or Cancelled.
     * The Order will also be removed from the cache as there is no point of modifying or cancelling a 'fully traded'
     * Order.
     * @param order to remove
     */
    public void remove(@NonNull Order order) {
        order.setScheduledForDeletion(true);
        orderCache.remove(order);
    }

    public Optional<Order> getById(int id) {
        return orderCache.getById(id);
    }

    public void executeRemainingTasksAndShutDown() throws ExchangeShutdownException, OrderProcessingException {
        taskExecutor.shutdownAndAwaitTermination();
        executeTasksWhile(ProcessingTaskQueue::isNotEmpty);
    }

    public void addTask(@NonNull ProcessingTask processingTask) {
        processingTaskQueue.enqueueTask(processingTask);
        processingTaskQueue.executeIfFull(() ->
                taskExecutor.enqueueProcessingTask(processingTaskQueue.getNextTaskToExecute().get()));
    }

    public Optional<Order> peekNextOrder(@NonNull Side side) {
        return orderQueue.peekNextOrder(side);
    }

    public Optional<Order> getNextOrder(@NonNull Side side) {
        return orderQueue.getNextOrder(side);
    }

    public void executeTransaction(@NonNull Order processedOrder, @NonNull Order passiveOrder) {
        productTransactionLedger.executeTransaction(processedOrder, passiveOrder);
        if (passiveOrder.getDetails().getAmount() == 0) {
            remove(passiveOrder);
        }
    }

    public String getProductName() {
        return productName;
    }

    public List<Transaction> getTransactions() {
        return productTransactionLedger.getTransactions();
    }

    private void executeTasksWhile(Predicate<ProcessingTaskQueue> taskQueuePredicate) throws OrderProcessingException {
        while (taskQueuePredicate.test(processingTaskQueue)) {
            Optional<ProcessingTask> nextTaskToExecute = processingTaskQueue.getNextTaskToExecute();
            if (!nextTaskToExecute.isPresent()) {
                return;
            }
            ProcessingTask processingTask = nextTaskToExecute.get();
            processingTask.setProductExchange(this);
            processingTask.run();
        }
    }
}
