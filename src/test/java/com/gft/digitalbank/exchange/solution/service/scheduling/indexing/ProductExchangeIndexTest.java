package com.gft.digitalbank.exchange.solution.service.scheduling.indexing;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchange;
import com.gft.digitalbank.exchange.solution.service.exchange.ProductExchangeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Ivo on 14/08/16.
 */
@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductExchangeIndexTest {

    private final static String PRODUCT_NAME = "product";

    private ProductExchangeIndex sut;

    @Mock
    private ProductExchangeFactory productExchangeFactory;

    @Mock
    private ProductExchange productExchange;

    @Before
    public void initialize() {
        sut = new ProductExchangeIndex(productExchangeFactory);
        when(productExchangeFactory.createProductExchange(PRODUCT_NAME)).thenReturn(productExchange);
    }

    @Test(expected = NullPointerException.class)
    public void getProductExchange_whenPassedNull_shouldThrowNullPointerException() {
        sut.getProductExchange(null);
    }

    @Test
    public void getProductExchange_whenPassedProductNameOfNonExistingExchange_thenNewExchangeShallBeCreatedAndReturned() {
        ProductExchange productExchangeFromIndex = sut.getProductExchange(PRODUCT_NAME);
        Mockito.verify(productExchangeFactory,times(1)).createProductExchange(PRODUCT_NAME);
        assertThat(productExchangeFromIndex,is(sameInstance(productExchange)));
    }

    @Test
    public void getProductExchange_whenPassedProductNameOfExistingExchange_thenNoNewExchangeShallBeCreatedAndExistingExchangeReturned() {
        sut.getProductExchange(PRODUCT_NAME);
        ProductExchange productExchangeFromIndex = sut.getProductExchange(PRODUCT_NAME);
        Mockito.verify(productExchangeFactory,times(1)).createProductExchange(PRODUCT_NAME);
        assertThat(productExchangeFromIndex,is(sameInstance(productExchange)));
    }

    @Test
    public void getAllExchanges_whenGetNeverCalled_shouldReturnEmptyList() {
        Collection<ProductExchange> allExchanges = sut.getAllExchanges();
        assertThat(allExchanges,is(empty()));
    }

    @Test
    public void getAllExchanges_whenGetCalled_thenTheCreatedProductExchangeShouldBeInTheReturnedList() {
        sut.getProductExchange(PRODUCT_NAME);
        Collection<ProductExchange> allExchanges = sut.getAllExchanges();
        assertThat(allExchanges,hasItem(productExchange));
    }
}