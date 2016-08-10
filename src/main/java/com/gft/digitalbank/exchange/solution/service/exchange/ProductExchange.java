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
 * <p>
 * Created by Ivo Zieliński on 2016-07-06.
 */
public class ProductExchange {

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
     * @param order to enqueue
     */
    public void enqueue(@NonNull Order order) {
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
     * @param id
     * @return Order optional
     */
    public Optional<Order> getById(int id) {
        return orderCache.getById(id);
    }

    /**
     * Shutes down the single-threaded ExecutorService responsible for executing ProcessingTasks and executes all
     * currently enqueued ProcessingTasks from the ProcessingTasks queue.
     *
     * @throws ExchangeShutdownException if there are problems with shutting down ExecutorService
     * @throws OrderProcessingException  if there are problems executing the remaining ProcessingTasks
     */
    public void executeRemainingTasksAndShutDown() throws ExchangeShutdownException, OrderProcessingException {
        taskExecutor.shutdownAndAwaitTermination();
        executeTasksWhile(ProcessingTaskQueue::isNotEmpty);
    }

    /**
     * Adds a ProcessingTasks queue. The tasks are executed sequentially by a single-threaded ExecutorService in an order
     * dictated by the timestamp of their encapsulated TradingMessage. If the ProcessingTaskQueue is full the top is full
     * the ProcessingTask with the lowest timestamp is passed to the ExecutorService.
     *
     * @param processingTask to enqueue
     */
    public void enqueueTask(@NonNull ProcessingTask processingTask) {
        processingTaskQueue.enqueueTask(processingTask);
        processingTaskQueue.executeIfFull(() ->
                taskExecutor.enqueueProcessingTask(processingTaskQueue.getNextTaskToExecute().get()));
    }

    /**
     * Peeks the top Order from the OrderQueue. The Order is not removed from the queue.
     * For buy Orders the Order with the highest price is returned.
     * For sell Orders the Order with the lowest price is returned.
     *
     * @param side of the retrieved Order
     * @return
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
     * @return
     */
    public Optional<Order> getNextOrder(@NonNull Side side) {
        return orderQueue.getNextOrder(side);
    }

    /**
     * Creates a transaction from the Orders passed.
     * Subtracts the traded amount from both Orders.
     * If the processed Order is 'fully traded' it will be removed from the OrderCache so that it cannot be cancelled
     * or modified any more.
     *
     * @param processedOrder that is currently handled
     * @param passiveOrder   retrieved from the queue
     * @throws OrderProcessingException
     */
    public void executeTransaction(@NonNull Order processedOrder, @NonNull Order passiveOrder) throws OrderProcessingException {
        if (processedOrder.getSide() == passiveOrder.getSide()) {
            throw new OrderProcessingException("Attempting to match orders of the same type!");
        }
        productTransactionLedger.executeTransaction(processedOrder, passiveOrder);
        if (passiveOrder.isFullyProcessed()) {
            remove(passiveOrder);
        }
    }

    /**
     * Retrieves the name of the product that the ProductExchange is associated.
     * @return the underlying product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Retieves the current history of Transactions concerning the associated product.
     * @return the history
     */
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
