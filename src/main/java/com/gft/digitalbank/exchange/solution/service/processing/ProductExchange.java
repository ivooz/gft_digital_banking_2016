package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ExecutionTask;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;

/**
 * Created by iozi on 2016-07-06.
 */
public class ProductExchange {

    private final String product;
    private final ProductLedger productLedger = new ProductLedger();
    private final TradingMessageQueue tradingMessageQueue = new TradingMessageQueue();
    private final ExecutionTaskQueue executionTaskQueue = new ExecutionTaskQueue();
    private final OrderCache orderCache = new OrderCache();

    public ProductExchange(String product) {
        this.product = product;
    }

    public void addOrder(Order order) {
        orderCache.add(order);
        tradingMessageQueue.pushOrder(order);
    }

    public void markOrderAsComplete(Order order) {
        tradingMessageQueue.getNextOrder(order.getSide());
        orderCache.remove(order);
    }

    public void remove(Order order) {
        order.setScheduledForDeletion(true);
        orderCache.remove(order);
    }

    public Optional<Order> getById(int id) {
        return orderCache.get(id);
    }

    public void executeRemainingTasks() {
        executeTasksWhile(ExecutionTaskQueue::isNotEmpty);
    }

    public void addTask(ExecutionTask executionTask) {
        executionTaskQueue.addTask(executionTask);
        executeTasksWhile(ExecutionTaskQueue::isFull);
    }

    private synchronized void executeTasksWhile(Predicate<ExecutionTaskQueue> taskQueuePredicate) {
        while (taskQueuePredicate.test(executionTaskQueue)) {
            Optional<ExecutionTask> nextTaskToExecute = executionTaskQueue.getNextTaskToExecute();
            if (!nextTaskToExecute.isPresent()) {
                return;
            }
            nextTaskToExecute.get().execute(this);
        }
    }

    public Optional<Order> peekNextOrder(Side side) {
        return tradingMessageQueue.peekNextOrder(side);
    }

    public Optional<Order> getNextOrder(Side side) {
        return tradingMessageQueue.getNextOrder(side);
    }

    public void addTransaction(Order processedOrder, Order passiveOrder, int amountTraded) {
        productLedger.addTransaction(processedOrder, passiveOrder, amountTraded);
    }

    public String getProduct() {
        return product;
    }

    public PriorityQueue<Order> getOrders(Side side) {
        return side == Side.BUY ? tradingMessageQueue.getBuyOrders() : tradingMessageQueue.getSellOrders();
    }

    public List<Transaction> getTransactions() {
        return productLedger.getTransactions();
    }
}
