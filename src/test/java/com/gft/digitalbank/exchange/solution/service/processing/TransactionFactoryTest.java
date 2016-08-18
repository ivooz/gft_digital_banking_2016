package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by Ivo Zieliński on 2016-08-17.
 */
@Category(UnitTest.class)
public class TransactionFactoryTest {

    private static final int AMOUNT_TRADED = 1;
    private static final int ID = 1;

    private TransactionFactory sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        pojoFactory = new PojoFactory();
        sut = new TransactionFactory();
    }

    @Test(expected = NullPointerException.class)
    public void createTransaction_whenPassedNullProcessedOrder_shouldThrowNullPointerException() throws Exception {
        sut.createTransaction(null, Mockito.mock(Order.class),AMOUNT_TRADED,ID);
    }

    @Test(expected = NullPointerException.class)
    public void createTransaction_whenPassedNullOrderFromQueue_shouldThrowNullPointerException() throws Exception {
        sut.createTransaction(Mockito.mock(Order.class),null,AMOUNT_TRADED,ID);
    }

    @Test(expected = NullPointerException.class)
    public void createTransaction_whenPassedNulls_shouldThrowNullPointerException() throws Exception {
        sut.createTransaction(null, null,AMOUNT_TRADED,ID);
    }

    @Test
    public void createTransaction_whenPassedTwoOrders_itShouldProduceValidTransaction() throws Exception {
        Pair<Order, Order> identicalBuyAndSellOrders = pojoFactory.createIdenticalBuyAndSellOrders();
        Order processedOrder = identicalBuyAndSellOrders.getKey();
        Order orderFromQueue = identicalBuyAndSellOrders.getValue();
        Transaction transaction = sut.createTransaction(processedOrder, orderFromQueue, AMOUNT_TRADED, ID);
        assertEquals(transaction.getPrice(),orderFromQueue.getPrice());
        assertEquals(transaction.getAmount(),AMOUNT_TRADED);
        assertEquals(transaction.getId(),ID);
        assertEquals(transaction.getBrokerBuy(),processedOrder.getBroker());
        assertEquals(transaction.getBrokerSell(),orderFromQueue.getBroker());
        assertEquals(transaction.getClientBuy(),processedOrder.getClient());
        assertEquals(transaction.getClientSell(),orderFromQueue.getClient());
        assertEquals(transaction.getProduct(),processedOrder.getProduct());
    }
}