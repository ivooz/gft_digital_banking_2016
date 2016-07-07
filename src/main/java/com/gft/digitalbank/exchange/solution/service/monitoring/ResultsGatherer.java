package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.model.OrderBook;
import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.model.SolutionResult;
import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProductLedger;
import com.gft.digitalbank.exchange.solution.service.processing.ProductExchangeIndex;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by iozi on 2016-06-29.
 */
@Singleton
public class ResultsGatherer {

    @Inject
    ProductExchangeIndex productExchangeIndex;

    @Inject
    OrderEntryConverter orderEntryConverter;

    public SolutionResult gatherResults() {
        return SolutionResult.builder().orderBooks(getOrdersBook())
                .transactions(getTransactions())
                .build();
    }

    public List<OrderBook> getOrdersBook() {
        return productExchangeIndex.getProductExchangeMap().values().parallelStream()
                .map(this::getOrderBook)
                .filter(orderBook -> !orderBook.getBuyEntries().isEmpty() || !orderBook.getSellEntries().isEmpty())
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactions() {
        return productExchangeIndex.getProductExchangeMap().values().parallelStream()
                .map(ProductExchange::getTransactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private OrderBook getOrderBook(ProductExchange productExchange) {
        return OrderBook.builder().product(productExchange.getProduct())
                .buyEntries(extractOrderEntries(Side.BUY,productExchange))
                .sellEntries(extractOrderEntries(Side.SELL,productExchange))
                .build();
    }

    private List<OrderEntry> extractOrderEntries(Side side, ProductExchange productExchange) {
        List<OrderEntry> orderEntries = new ArrayList<>();
        int index = 1;
        Optional<Order> orderOptional = productExchange.getNextOrder(side);
        while (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setId(index++);
            orderEntries.add(orderEntryConverter.convertToOrderEntry(order));
            orderOptional = productExchange.getNextOrder(side);
        }
        return orderEntries;
    }
}
