package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.OrderProcessingException;
import com.gft.digitalbank.exchange.solution.service.processing.TransactionFactory;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for transforming buy and sell Orders into transactions and a list of all Transactions associated with
 * a single ProductExchange.
 * <p>
 * Created by Ivo Zieliński on 2016-06-28.
 */
public class ProductTransactionLedger {

    public static final String SAME_ORDER_TYPE_EXCEPTION_MESSAGE = "Cannot transact Orders of the same type!";
    private final TransactionFactory transactionFactory = new TransactionFactory();
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Creates a transaction from the Orders passed.
     * Subtracts the traded amount from both Orders.
     * Saves the created Transaction for later retrieval.
     *
     * @param processedOrder the currently handled order
     * @param orderFromQueue   order retrieved from the queue
     */
    public void executeTransaction(@NonNull Order processedOrder, @NonNull Order orderFromQueue) {
        Preconditions.checkState(processedOrder.getSide() != orderFromQueue.getSide(),
                SAME_ORDER_TYPE_EXCEPTION_MESSAGE);
        int processedOrderAmount = processedOrder.getAmount();
        int passiveOrderAmount = orderFromQueue.getAmount();
        int amountTraded = processedOrderAmount > passiveOrderAmount ? passiveOrderAmount : processedOrderAmount;
        transactions.add(transactionFactory.createTransaction(processedOrder, orderFromQueue, amountTraded,
                transactions.size() + 1));
        processedOrder.getDetails().setAmount(processedOrderAmount - amountTraded);
        orderFromQueue.getDetails().setAmount(passiveOrderAmount - amountTraded);
    }

    /**
     * Retrieves the list of all Transactions executed for an associated ProductExchange.
     *
     * @return history of Transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
