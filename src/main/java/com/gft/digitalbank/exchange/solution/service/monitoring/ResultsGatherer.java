package com.gft.digitalbank.exchange.solution.service.monitoring;

import com.gft.digitalbank.exchange.model.OrderBook;
import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.model.SolutionResult;
import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Responsible for extracting the results of TradingMessage procsssing from ProductExchanges and converting them
 * to format expected by the ProcessingListener.
 * <p>
 * Created by Ivo Zieliński on 2016-06-29.
 */
@Singleton
public class ResultsGatherer {

    private static final int ORDER_ENTRIES_START_INDEX = 1;

    private final ProductExchangeIndex productExchangeIndex;
    private final OrderEntryConverter orderEntryConverter;

    @Inject
    public ResultsGatherer(ProductExchangeIndex productExchangeIndex, OrderEntryConverter orderEntryConverter) {
        this.productExchangeIndex = productExchangeIndex;
        this.orderEntryConverter = orderEntryConverter;
    }

    /**
     * Gathers all the OrderBooks and Transactions from the ProductExchanges applying conversions if necessary.
     * Transactions are ordered chronologically with ids starting from 1 for the earliest Transaction.
     * BookOrders are ordered according to their position in queue with ids starting from 1 for the top Order.
     *
     * @return result of the message processing
     */
    public SolutionResult gatherResults() {
        return SolutionResult.builder().orderBooks(getOrdersBook())
                .transactions(getTransactions())
                .build();
    }

    private List<OrderBook> getOrdersBook() {
        return productExchangeIndex.getAllExchanges().parallelStream()
                .map(this::getOrderBook)
                .filter(orderBook -> !orderBook.getBuyEntries().isEmpty() || !orderBook.getSellEntries().isEmpty())
                .collect(Collectors.toList());
    }

    private List<Transaction> getTransactions() {
        return productExchangeIndex.getAllExchanges().parallelStream()
                .map(ProductExchange::getTransactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private OrderBook getOrderBook(ProductExchange productExchange) {
        return OrderBook.builder().product(productExchange.getProductName())
                .buyEntries(extractOrderEntries(Side.BUY, productExchange))
                .sellEntries(extractOrderEntries(Side.SELL, productExchange))
                .build();
    }

    private List<OrderEntry> extractOrderEntries(Side side, ProductExchange productExchange) {
        List<OrderEntry> orderEntries = new ArrayList<>();
        int index = ORDER_ENTRIES_START_INDEX;
        Optional<Order> orderOptional = productExchange.pollNextOrder(side);
        while (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            orderEntries.add(orderEntryConverter.convertToOrderEntry(order,index++));
            orderOptional = productExchange.pollNextOrder(side);
        }
        return orderEntries;
    }
}
