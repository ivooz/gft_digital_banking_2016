package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class CancelExecutionTaskProcessorTest {

    private static final int CANCELLED_ORDER_ID = 1;
    private static final String BROKER = "broker";
    private static final String OTHER_BROKER = "broker2";

    private CancelExecutionTaskProcessor sut;

    @Mock
    private ProductExchange productExchange;

    @Mock
    private Cancel cancel;

    @Mock
    private Order orderToCancel;

    @Before
    public void initialize() {
        sut = new CancelExecutionTaskProcessor();
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullCancel_shouldThrowNullPointerException() {
        sut.processTradingMessage(null, productExchange);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullProductExchange_shouldThrowNullPointerException() {
        sut.processTradingMessage(cancel, null);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNulls_shouldThrowNullPointerException() {
        sut.processTradingMessage(null, null);
    }

//    Optional<Order> orderToCancel = productExchange.getById(cancel.getCancelledOrderId());
//    if (!orderToCancel.isPresent()) {
//        //The Order has already been cancelled or fully processed
//        return;
//    }
//    Order order = orderToCancel.get();
//    if (!cancel.getBroker().equals(order.getBroker())) {
//        return;
//    }
//    productExchange.remove(order);

    @Test
    public void processTradingMessage_whenPassedCancelAndOrderIsQueuedAndBrokersMatch_itShouldCancelTheOrder() {
        when(cancel.getCancelledOrderId()).thenReturn(CANCELLED_ORDER_ID);
        when(productExchange.getById(CANCELLED_ORDER_ID)).thenReturn(Optional.of(orderToCancel));
        when(orderToCancel.getBroker()).thenReturn(BROKER);
        when(cancel.getBroker()).thenReturn(BROKER);
        sut.processTradingMessage(cancel, productExchange);
        Mockito.verify(productExchange, times(1)).remove(orderToCancel);
    }

    @Test
    public void processTradingMessage_whenPassedCancelAndOrderIsQueuedAndBrokersDontMatch_itShouldNotCancelTheOrder() {
        when(cancel.getCancelledOrderId()).thenReturn(CANCELLED_ORDER_ID);
        when(productExchange.getById(CANCELLED_ORDER_ID)).thenReturn(Optional.of(orderToCancel));
        when(orderToCancel.getBroker()).thenReturn(OTHER_BROKER);
        when(cancel.getBroker()).thenReturn(BROKER);
        sut.processTradingMessage(cancel, productExchange);
        Mockito.verify(productExchange, never()).remove(orderToCancel);
    }

    @Test
    public void processTradingMessage_whenPassedCancelAndOrderIsNotQueued_itShouldNotCancelTheOrder() {
        when(cancel.getCancelledOrderId()).thenReturn(CANCELLED_ORDER_ID);
        when(productExchange.getById(CANCELLED_ORDER_ID)).thenReturn(Optional.empty());
        sut.processTradingMessage(cancel, productExchange);
        Mockito.verify(productExchange, never()).remove(orderToCancel);
    }
}