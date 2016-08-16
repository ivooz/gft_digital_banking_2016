package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Details;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.utils.PojoFactory;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ModificationExecutionTaskProcessorTest {

    private static final int MODIFIED_ORDER_ID = 1;
    private static final long TIMESTAMP = 1;
    private static final String BROKER = "broker";
    private static final String OTHER_BROKER = "broker2";

    private ModificationExecutionTaskProcessor sut;
    private PojoFactory pojoFactory;
    private Order orderToModify;

    @Mock
    private OrderExecutionTaskProcessor orderExecutionTaskProcessor;

    @Mock
    private ProductExchange productExchange;

    @Mock
    private Modification modification;


    @Before
    public void initialize() {
        sut = new ModificationExecutionTaskProcessor(orderExecutionTaskProcessor);
        pojoFactory = new PojoFactory();
        orderToModify = pojoFactory.createNextOrder();
        orderToModify.setTimestamp(TIMESTAMP);
        orderToModify.setBroker(BROKER);
        orderToModify.setId(MODIFIED_ORDER_ID);
        when(modification.getModifiedOrderId()).thenReturn(MODIFIED_ORDER_ID);
        when(modification.getTimestamp()).thenReturn(TIMESTAMP);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullCancel_shouldThrowNullPointerException()
            throws OrderProcessingException {
        sut.processTradingMessage(null, productExchange);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullProductExchange_shouldThrowNullPointerException()
            throws OrderProcessingException {
        sut.processTradingMessage(modification, null);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNulls_shouldThrowNullPointerException()
            throws OrderProcessingException {
        sut.processTradingMessage(null, null);
    }

    @Test
    public void processTradingMessage_whenPassedModificationAndOrderIsQueuedAndBrokersMatch_itShouldResubmitAModifiedOrderCopy() {
        when(productExchange.getById(MODIFIED_ORDER_ID)).thenReturn(Optional.of(orderToModify));
        when(modification.getBroker()).thenReturn(BROKER);
        try {
            sut.processTradingMessage(modification,productExchange);
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (OrderProcessingException e) {
            e.printStackTrace();
        }
        try {
            Mockito.verify(orderExecutionTaskProcessor,times(1))
                    .processTradingMessage(Mockito.any(Order.class),eq(productExchange));
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void processTradingMessage_whenPassedModificationAndOrderIsQueuedAndBrokersDontMatch_itShouldNotResubmitTheOrder() {
        when(productExchange.getById(MODIFIED_ORDER_ID)).thenReturn(Optional.of(orderToModify));
        when(modification.getBroker()).thenReturn(OTHER_BROKER);
        try {
            sut.processTradingMessage(modification,productExchange);
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
        try {
            Mockito.verify(orderExecutionTaskProcessor,never())
                    .processTradingMessage(Mockito.any(Order.class),eq(productExchange));
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void processTradingMessage_whenPassedModificationAndOrderIsNotQueued_itShouldNotResubmitTheOrder() {
        when(productExchange.getById(MODIFIED_ORDER_ID)).thenReturn(Optional.empty());
        when(modification.getBroker()).thenReturn(BROKER);
        try {
            sut.processTradingMessage(modification,productExchange);
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
        try {
            Mockito.verify(orderExecutionTaskProcessor,never())
                    .processTradingMessage(Mockito.any(Order.class),eq(productExchange));
        } catch (OrderProcessingException e) {
            fail(e.getMessage());
        }
    }
}