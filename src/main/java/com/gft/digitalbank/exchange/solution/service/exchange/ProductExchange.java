package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Holds the entire state associated with trading a single product.
 * Encapsulates business logic associated with the Order processing order and synchronization of ProcessingTasks
 * execution.
 * <p>
 * Created by Ivo Zieliński on 2016-07-06.
 */
public class ProductExchange {

    private static final String ENQUEUED_ORDER_SCHEDULED_FOR_DELETION_EXCEPTION_MESSAGE = "Enqueued Order must not be marked as scheduled for deletion.";
    private static final String SAME_SIDE_ORDERS_EXCEPTION_MESSAGE = "Attempting to match orders of the same type!";

    private final String productName;
    private final ProductTransactionLedger productTransactionLedger;
    private final OrderQueue orderQueue;
    private final ProcessingTaskExecutorService taskExecutor;
    private final ProcessingTaskQueue processingTaskQueue;
    private final OrderCache orderCache;

    public ProductExchange(@NonNull String productName, int processingTaskBufferSize) {
        this.productName = productName;
        this.processingTaskQueue = new ProcessingTaskQueue(processingTaskBufferSize);
        this.productTransactionLedger = new ProductTransactionLedger();
        this.orderCache = new OrderCache();
        this.taskExecutor = new ProcessingTaskExecutorService(this);
        this.orderQueue = new OrderQueue();
    }

    /**
     * Adds the Order to the cache for further retrieval as well as queues it for later matching.
     * This method is invoked to add Orders that have not been fully 'traded' or/and no match for them has been found.
     *
     * @param order to enqueueOrder, it cannot be marked as Scheduled for deletion.
     */
    public void enqueueOrder(@NonNull Order order) {
        Preconditions.checkArgument(!order.isScheduledForDeletion(), ENQUEUED_ORDER_SCHEDULED_FOR_DELETION_EXCEPTION_MESSAGE);
        orderCache.add(order);
        orderQueue.pushOrder(order);
    }

    /**
     * Must be executed when an Order has been 'fully traded' by matching it with and Order of the opposite Side or Cancelled.
     * Also invoked during Modification message handling, the old Order is removed while the copy with modified state
     * is treated like a new Order.
     * The passed  Order will be removed from the cache as there is no point of modifying or cancelling a 'fully traded'
     * Order.
     * The Order will be lazily removed from the OrderQueue.
     *
     * @param order to remove
     */
    public void remove(@NonNull Order order) {
        order.setScheduledForDeletion(true);
        orderCache.remove(order);
    }

    /**
     * Retrieves and Order from the cache by its id.
     * Returns and empty Optional if Order has already been 'fully traded' or cancelled, and consequently removed from cache.
     *
     * @param id of the retrieved Order
     * @return Order optional
     */
    public Optional<Order> getById(int id) {
        return orderCache.getById(id);
    }

    /**
     * Shuts down the single-threaded ExecutorService responsible for executing ProcessingTasks and executes all
     * currently enqueued ProcessingTasks from the ProcessingTasks queue.
     *
     * @throws ExchangeShutdownException if there are problems with shutting down ExecutorService
     */
    public void executeRemainingTasksAndShutDown() throws ExchangeShutdownException {
        taskExecutor.shutdownAndAwaitTermination();
        executeTasksWhile(ProcessingTaskQueue::isNotEmpty);
    }

    /**
     * Adds a ProcessingTasks queue. The tasks are executed sequentially by a single-threaded ExecutorService in an order
     * dictated by the timestamp of their encapsulated TradingMessage. If the ProcessingTaskQueue is full the top is full
     * the ProcessingTask with the lowest timestamp is passed to the ExecutorService.
     *
     * @param processingTask to enqueueOrder
     */
    public void enqueueTask(@NonNull ProcessingTask processingTask) {
        processingTaskQueue.enqueueTask(processingTask);
        if (processingTaskQueue.isFull()) {
            synchronized (processingTaskQueue) {
                if (processingTaskQueue.isFull()) {
                    taskExecutor.enqueueProcessingTask(processingTaskQueue.getNextTaskToExecute().get());
                }
            }
        }
    }

    /**
     * Peeks the top Order from the OrderQueue. The Order is not removed from the queue.
     * For buy Orders the Order with the highest price is returned.
     * For sell Orders the Order with the lowest price is returned.
     *
     * @param side of the retrieved Order
     * @return top Order from the queue
     */
    public Optional<Order> peekNextOrder(@NonNull Side side) {
        return orderQueue.peekNextOrder(side);
    }

    /**
     * Removes the top Order from the OrderQueue. The Order is removed from the queue.
     * For buy Orders the Order with the highest price is returned.
     * For sell Orders the Order with the lowest price is returned.
     *
     * @param side of the retrieved Order
     * @return removed top Order from the queue
     */
    public Optional<Order> pollNextOrder(@NonNull Side side) {
        return orderQueue.pollNextOrder(side);
    }

    /**
     * Passed the Orders to the ProductTransactionLedger so that it is saved.
     * Subtracts the traded amount from both Orders.
     * If the processed Order is 'fully traded' it will be removed from the OrderCache so that it cannot be cancelled
     * or modified any more.
     *
     * @param processedOrder that is currently handled
     * @param passiveOrder   retrieved from the queue
     */
    public void executeTransaction(@NonNull Order processedOrder, @NonNull Order passiveOrder) {
        Preconditions.checkArgument(processedOrder.getSide() != passiveOrder.getSide(),
                SAME_SIDE_ORDERS_EXCEPTION_MESSAGE);
        productTransactionLedger.executeTransaction(processedOrder, passiveOrder);
        if (passiveOrder.isFullyTraded()) {
            remove(passiveOrder);
        }
    }

    /**
     * Retrieves the name of the product that the ProductExchange is associated.
     *
     * @return the underlying product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Retrieves the current history of Transactions concerning the associated product.
     *
     * @return the history
     */
    public List<Transaction> getTransactions() {
        return productTransactionLedger.getTransactions();
    }

    private void executeTasksWhile(Predicate<ProcessingTaskQueue> taskQueuePredicate) {
        while (taskQueuePredicate.test(processingTaskQueue)) {
            Optional<ProcessingTask> nextTaskToExecute = processingTaskQueue.getNextTaskToExecute();
            ProcessingTask processingTask = nextTaskToExecute.get();
            processingTask.setProductExchange(this);
            processingTask.run();
        }
    }
}
