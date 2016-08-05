package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.execution.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.processing.OrderProcessingException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by iozi on 2016-07-06.
 */
public class ProductExchange {

    private final String productName;
    private final ProductTransactionLedger productTransactionLedger = new ProductTransactionLedger();
    private final TradingMessageQueue tradingMessageQueue = new TradingMessageQueue();
    private final ProcessingTaskExecutor taskExecutor = new ProcessingTaskExecutor();
    private final ExecutionTaskQueue executionTaskQueue = new ExecutionTaskQueue();
    private final OrderCache orderCache = new OrderCache();

    public ProductExchange(String productName) {
        this.productName = productName;
    }

    public void queueOrder(Order order) {
        orderCache.add(order);
        tradingMessageQueue.pushOrder(order);
    }

    public void markOrderAsComplete(Order order) {
        tradingMessageQueue.removeTopOrder(order.getSide());
        orderCache.remove(order);
    }

    public void remove(Order order) {
        order.setScheduledForDeletion(true);
        orderCache.remove(order);
    }

    public Optional<Order> getById(int id) {
        return orderCache.get(id);
    }

    public void executeRemainingTasksAndShutDown() throws ExchangeShutdownException, OrderProcessingException {
        taskExecutor.shutdown();
        executeTasksWhile(ExecutionTaskQueue::isNotEmpty);
    }

    public void addTask(ProcessingTask processingTask) {
        executionTaskQueue.addTask(processingTask);
        executionTaskQueue.executeIfFull(() ->
                taskExecutor.execute(executionTaskQueue.getNextTaskToExecute().get(),this));
    }

    private void executeTasksWhile(Predicate<ExecutionTaskQueue> taskQueuePredicate) throws OrderProcessingException {
        while (taskQueuePredicate.test(executionTaskQueue)) {
            Optional<ProcessingTask> nextTaskToExecute = executionTaskQueue.getNextTaskToExecute();
            if (!nextTaskToExecute.isPresent()) {
                return;
            }
            ProcessingTask processingTask = nextTaskToExecute.get();
            processingTask.setProductExchange(this);
            processingTask.run();
        }
    }

    public Optional<Order> peekNextOrder(Side side) {
        return tradingMessageQueue.peekNextOrder(side);
    }

    public Optional<Order> getNextOrder(Side side) {
        return tradingMessageQueue.getNextOrder(side);
    }

    public void executeTransaction(Order processedOrder, Order passiveOrder) {
        productTransactionLedger.executeTransaction(processedOrder, passiveOrder);
        if (passiveOrder.getDetails().getAmount() == 0) {
            markOrderAsComplete(passiveOrder);
        }
    }

    public String getProductName() {
        return productName;
    }

    public List<Transaction> getTransactions() {
        return productTransactionLedger.getTransactions();
    }
}
