package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.TransactionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iozi on 2016-06-28.
 */
public class ProductTransactionLedger {

    private final TransactionFactory transactionFactory = new TransactionFactory();
    private final List<Transaction> transactions = new ArrayList<>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void executeTransaction(Order processedOrder, Order passiveOrder) {
        int processedOrderAmount = processedOrder.getAmount();
        int passiveOrderAmount = passiveOrder.getAmount();
        int amountTraded = processedOrderAmount > passiveOrderAmount ? passiveOrderAmount : processedOrderAmount;
        transactions.add(transactionFactory.createTransaction(processedOrder, passiveOrder, amountTraded,
                transactions.size() + 1));
        processedOrder.getDetails().setAmount(processedOrderAmount - amountTraded);
        passiveOrder.getDetails().setAmount(passiveOrderAmount - amountTraded);
    }
}
