package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.model.OrderBook;
import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.model.SolutionResult;
import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedgerIndex;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by iozi on 2016-06-29.
 */
@Singleton
public class ResultsGatherer {

    @Inject
    ProductLedgerIndex productLedgerIndex;

    @Inject
    OrderEntryConverter orderEntryConverter;

    public SolutionResult gatherResults() {
        return SolutionResult.builder().orderBooks(getOrdersBook())
                .transactions(getTransactions())
                .build();
    }


    public List<OrderBook> getOrdersBook() {
        return productLedgerIndex.getProductLedgerMap().values().parallelStream()
                .map(this::getOrderBook)
                .filter(orderBook -> !orderBook.getBuyEntries().isEmpty() || !orderBook.getSellEntries().isEmpty())
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactions() {
        return productLedgerIndex.getProductLedgerMap().values().parallelStream()
                .map(ProductLedger::getTransactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private OrderBook getOrderBook(ProductLedger ledger) {

        PriorityQueue<Order> buyOrders = ledger.getBuyOrders();
        List<OrderEntry> buyEntries = new ArrayList<>(buyOrders.size());

        int index = 1;
        Order order = buyOrders.poll();
        while (order != null) {
            while (order.isScheduledForDeletion()) {
                order = buyOrders.poll();
                if (order == null) {
                    break;
                }
            }
            if (order == null) {
                break;
            }
            order.setId(index++);
            buyEntries.add(orderEntryConverter.convertToOrderEntry(order));
            order = buyOrders.poll();
        }

        PriorityQueue<Order> sellOrders = ledger.getSellOrders();
        List<OrderEntry> sellEntries = new ArrayList<>(sellOrders.size());

        index = 1;
        order = sellOrders.poll();
        while (order != null) {
            while (order.isScheduledForDeletion()) {
                order = sellOrders.poll();
                if (order == null) {
                    break;
                }
            }
            if (order == null) {
                break;
            }
            order.setId(index++);
            sellEntries.add(orderEntryConverter.convertToOrderEntry(order));
            order = sellOrders.poll();
        }

        return OrderBook.builder().product(ledger.getProduct()).buyEntries(buyEntries).sellEntries(sellEntries).build();
    }
}
