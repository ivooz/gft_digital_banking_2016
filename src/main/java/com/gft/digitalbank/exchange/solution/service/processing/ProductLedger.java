package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ExecutionTask;
import com.google.common.base.Preconditions;

import java.util.*;

/**
 * Created by iozi on 2016-06-28.
 */
public class ProductLedger {

    private final String product;

    private final static int MAX_CACHE_SIZE = 500;

    private List<Transaction> transactions = new ArrayList<>();
    private Map<Integer, Order> orderCache = new HashMap<>();
    private PriorityQueue<Order> buyOrders = new PriorityQueue<>();
    private PriorityQueue<Order> sellOrders = new PriorityQueue<>();
    private PriorityQueue<ExecutionTask> tasksToExecute = new PriorityQueue<>();

    public ProductLedger(String product) {
        this.product = product;
    }

    public void addOrder(Order order) {
        orderCache.put(order.getId(),order);
        pushOrder(order);
    }

    public Optional<Order> getNextOrder(Side side) {
        Order order = null;
        while (true){
            order = pollOrder(side);
            if (order == null) {
                return Optional.empty();
            }
            if(!order.isScheduledForDeletion()) {
                return Optional.of(order);
            }
        }
    }

    public Optional<Order> peekNextOrder(Side side) {
        Order order = null;
        while (true) {
            order = peekOrder(side);
            if (order == null) {
                return Optional.empty();
            }
            if(order.isScheduledForDeletion()) {
                discardTopOrder(side);
            } else {
                return Optional.of(order);
            }
        }
    }

    public void markOrderAsComplete(Order order) {
        pollOrder(order.getSide());
        removeFromCache(order);
    }

    public void remove(Order order) {
        order.setScheduledForDeletion(true);
        removeFromCache(order);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public void addTask(ExecutionTask executionTask) {
        tasksToExecute.add(executionTask);
    }

    public int getTaskToExecuteCount() {
        return tasksToExecute.size();
    }

    public Optional<ExecutionTask> getNextTaskToExecute() {
        ExecutionTask task = tasksToExecute.poll();
        if(task == null) {
            return Optional.empty();
        }
        return Optional.of(task);
    }

    public Optional<ExecutionTask> peekTaskToExecute() {
        ExecutionTask task = tasksToExecute.peek();
        if(task== null) {
            return Optional.empty();
        }
        return Optional.of(task);
    }

    public Optional<Order> getById(int id) {
        return Optional.of(orderCache.get(id));
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getProduct() {
        return product;
    }

    public PriorityQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    public PriorityQueue<Order> getSellOrders() {
        return sellOrders;
    }

    private void removeFromCache(Order order) {
        if(orderCache.size() >= MAX_CACHE_SIZE) {
            orderCache.remove(order.getId());
        }
    }

    private Order peekOrder(Side side) {
        switch (side) {
            case BUY:
                return buyOrders.peek();
            case SELL:
                return sellOrders.peek();
        }
        //TODO throw exception
        return null;
    }

    private Order pollOrder(Side side) {
        switch (side) {
            case BUY:
                return buyOrders.poll();
            case SELL:
                return sellOrders.poll();
        }
        //TODO throw exception
        return null;
    }


    private void pushOrder(Order order) {
        switch (order.getSide()) {
            case BUY:
                buyOrders.add(order);
                break;
            case SELL:
                sellOrders.add(order);
                break;
        }
    }

    private void discardTopOrder(Side side) {
        switch (side) {
            case BUY:
                buyOrders.poll();
                break;
            case SELL:
                sellOrders.poll();
                break;
        }
    }
}
