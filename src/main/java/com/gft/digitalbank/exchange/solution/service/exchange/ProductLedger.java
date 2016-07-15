package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.TransactionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iozi on 2016-06-28.
 */
public class ProductLedger {

    private final TransactionFactory transactionFactory = new TransactionFactory();
    private final List<Transaction> transactions = new ArrayList<>();

    public int getTransactionCount() {
        return transactions.size();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Order processedOrder, Order passiveOrder, int amountTraded) {
        transactions.add(transactionFactory.createTransaction(processedOrder, passiveOrder, amountTraded,
                transactions.size() + 1));
    }
}
