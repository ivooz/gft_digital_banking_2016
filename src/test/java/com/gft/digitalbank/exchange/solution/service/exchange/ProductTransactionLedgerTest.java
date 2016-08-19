package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.model.Side;
import com.gft.digitalbank.exchange.solution.utils.OrderPojoFactory;
import javafx.util.Pair;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Ivo on 12/08/16.
 */
@Category(UnitTest.class)
@RunWith(JUnitParamsRunner.class)
public class ProductTransactionLedgerTest {

    private ProductTransactionLedger sut;
    private OrderPojoFactory orderPojoFactory;

    @Before
    public void initialize() {
        sut = new ProductTransactionLedger();
        orderPojoFactory = new OrderPojoFactory();
    }

    @Test
    public void getTransactions_whenJustCreated_shouldReturnEmptyList() {
        List<Transaction> transactions = sut.getTransactions();
        assertThat(transactions, is(empty()));
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenPassedNullOrderFromQueue_shouldThrowNullPointerException() {
        sut.executeTransaction(Mockito.mock(Order.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenPassedNullActiveOrder_shouldThrowNullPointerException() {
        sut.executeTransaction(null, Mockito.mock(Order.class));
    }

    @Test(expected = NullPointerException.class)
    public void executeTransaction_whenPassedNullOrders_shouldThrowNullPointerException() {
        sut.executeTransaction(null, null);
    }

    @Test
    public void executeTransaction_whenPassedTwoIdenticalOrdersWithOppositeSide_shouldResultInOneValidTransactionAndFullyTradedOrders() {
        Pair<Order, Order> orders = orderPojoFactory.createIdenticalBuyAndSellOrders();
        Order buyOrder = orders.getKey();
        Order sellOrder = orders.getValue();
        sut.executeTransaction(buyOrder, sellOrder);
        int transactionCount = sut.getTransactions().size();
        assertThat(transactionCount, is(equalTo(1)));
        assertThat(buyOrder.isFullyTraded(), is(equalTo(true)));
        assertThat(sellOrder.isFullyTraded(), is(equalTo(true)));
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "buyAndSellSides")
    public void executeTransaction_whenPassedOrdersOfTheSameSide_shouldThrowIllegalArgumentException(Side side) {
        Order firstOrder = orderPojoFactory.createNextOrderWithSide(side);
        Order secondOrder = orderPojoFactory.createNextOrderWithSide(side);
        sut.executeTransaction(firstOrder, secondOrder);
    }

    private Object buyAndSellSides() {
        return new Object[]{
                Side.BUY,
                Side.SELL
        };
    }


}