package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.processing.ProcessingTask;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.IdProductIndex;
import com.gft.digitalbank.exchange.solution.service.scheduling.indexing.ProductExchangeIndex;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Ivo on 13/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ModifySchedulingTaskTest {

    public static final String PRODUCT_NAME = "product";
    public static final int ORDER_ID = 5;

    private ModificationSchedulingTask sut;

    @Mock
    ProductExchangeIndex productExchangeIndex;

    @Mock
    IdProductIndex idProductIndex;

    @Mock
    ProcessingTask<Modification> processingTask;

    @Mock
    ProductExchange productExchange;

    @Mock
    Modification modification;

    @Before
    public void initialize() {
        sut = new ModificationSchedulingTask(productExchangeIndex,idProductIndex,processingTask);
        when(processingTask.getTradingMessage()).thenReturn(modification);
        when(modification.getModifiedOrderId()).thenReturn(ORDER_ID);
    }

    @Test(expected = OrderNotFoundException.class)
    public void execute_whenOrderNotInIndex_shouldThrowOrderNotFoundException() throws OrderNotFoundException {
        when(idProductIndex.get(Matchers.anyInt())).thenReturn(Optional.empty());
        sut.execute();
    }

    @Test
    public void execute_whenOrderCanBeFound_shouldEnqueueTask() {
        when(idProductIndex.get(ORDER_ID)).thenReturn(Optional.of(PRODUCT_NAME));
        when(productExchangeIndex.getProductExchange(PRODUCT_NAME)).thenReturn(productExchange);
        try {
            sut.execute();
        } catch (OrderNotFoundException e) {
            fail(e.getMessage());
        }
        Mockito.verify(productExchange,times(1)).enqueueTask(processingTask);
    }



}