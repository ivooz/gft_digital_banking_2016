package com.gft.digitalbank.exchange.solution.service.processing;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.utils.ModificationPojoFactory;
import com.gft.digitalbank.exchange.solution.utils.OrderPojoFactory;
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
public class ModificationExecutionTaskProcessorTest {

    public static final String OTHER_BROKER = "otherBroker";
    private ModificationProcessor sut;
    private Order orderToModify;

    @Mock
    private OrderProcessor orderProcessor;

    @Mock
    private ProductExchange productExchange;

    private Modification modification;
    private OrderPojoFactory orderPojoFactory;
    private ModificationPojoFactory modificationPojoFactory;

    @Before
    public void initialize() {
        sut = new ModificationProcessor(orderProcessor);
        modificationPojoFactory = new ModificationPojoFactory();
        orderPojoFactory = new OrderPojoFactory();
        orderToModify = orderPojoFactory.createNextOrder();
        modification = modificationPojoFactory.createDefaultModification();
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullCancel_shouldThrowNullPointerException() {
        sut.processTradingMessage(null, productExchange);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNullProductExchange_shouldThrowNullPointerException() {
        sut.processTradingMessage(modification, null);
    }

    @Test(expected = NullPointerException.class)
    public void processTradingMessage_whenPassedNulls_shouldThrowNullPointerException() {
        sut.processTradingMessage(null, null);
    }

    @Test
    public void processTradingMessage_whenPassedModificationAndOrderIsQueuedAndBrokersMatch_itShouldResubmitAModifiedOrderCopy() {
        when(productExchange.getById(orderPojoFactory.DEFAULT_MODIFIED_ORDER_ID)).thenReturn(Optional.of(orderToModify));
        sut.processTradingMessage(modification, productExchange);
        Mockito.verify(orderProcessor, times(1))
                .processTradingMessage(Mockito.any(Order.class), eq(productExchange));
    }

    @Test
    public void processTradingMessage_whenPassedModificationAndOrderIsQueuedAndBrokersDontMatch_itShouldNotResubmitTheOrder() {
        when(productExchange.getById(orderPojoFactory.DEFAULT_MODIFIED_ORDER_ID)).thenReturn(Optional.of(orderToModify));
        modification = modificationPojoFactory.createModificationWithBroker(OTHER_BROKER);
        sut.processTradingMessage(modification, productExchange);
        Mockito.verify(orderProcessor, never())
                .processTradingMessage(Mockito.any(Order.class), eq(productExchange));
    }

    @Test
    public void processTradingMessage_whenPassedModificationAndOrderIsNotQueued_itShouldNotResubmitTheOrder() {
        when(productExchange.getById(orderPojoFactory.DEFAULT_MODIFIED_ORDER_ID)).thenReturn(Optional.empty());
        sut.processTradingMessage(modification, productExchange);
        Mockito.verify(orderProcessor, never())
                .processTradingMessage(Mockito.any(Order.class), eq(productExchange));
    }
}