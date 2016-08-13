package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.service.processing.OrderProcessingException;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import javafx.util.Pair;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Ivo on 12/08/16.
 */
@RunWith(JUnitParamsRunner.class)
public class ProductTransactionLedgerTest {

    private ProductTransactionLedger sut;
    private PojoFactory pojoFactory;

    @Before
    public void initialize() {
        sut = new ProductTransactionLedger();
        pojoFactory = new PojoFactory();
    }

    @Test
    public void getTransactions_whenJustCreated_shouldReturnEmptyList() {
        List<Transaction> transactions = sut.getTransactions();
        assertThat(transactions,is(empty()));
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenPassedNullOrderFromQueue_shouldThrowNullPointerException() {
        try {
            sut.executeTransaction(Mockito.mock(Order.class),null);
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenPassedNullActiveOrder_shouldThrowNullPointerException() {
        try {
            sut.executeTransaction(null,Mockito.mock(Order.class));
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenPassedNullOrders_shouldThrowNullPointerException() {
        try {
            sut.executeTransaction(null,null);
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void executeTransaction_whenPassedTwoIdenticalOrdersWithOppositeSide_shouldResultInOneValidTransactionAndFullyTradedOrders() {
        Pair<Order,Order> orders = pojoFactory.createIdenticalBuyAndSellOrders();
        Order buyOrder = orders.getKey();
        Order sellOrder = orders.getValue();
        try {
            sut.executeTransaction(buyOrder,sellOrder);
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
        int transactionCount = sut.getTransactions().size();
        assertThat(transactionCount,is(equalTo(1)));
        assertThat(buyOrder.isFullyTraded(),is(equalTo(true)));
        assertThat(sellOrder.isFullyTraded(),is(equalTo(true)));
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "buyAndSellSides")
    public void executeTransaction_whenPassedOrdersOfTheSameSide_shouldThrowIllegalArgumentException(Side side) {
        try {
            Order firstOrder = pojoFactory.createNextOrderWithSide(side);
            Order secondOrder = pojoFactory.createNextOrderWithSide(side);
            sut.executeTransaction(firstOrder, secondOrder);
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
    }

    private Object buyAndSellSides() {
        return new Object[] {
                Side.BUY,
                Side.SELL
        };
    }





}