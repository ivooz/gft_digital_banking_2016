package com.gft.digitalbank.exchange.solution.service.exchange;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Ivo on 13/08/16.
 */
@Category(UnitTest.class)
public class ProductExchangeFactoryTest {

    private final static int BUFFER_SIZE = 5;
    private final String PRODUCT_NAME = "product";
    private ProductExchangeFactory sut;

    @Before
    public void initialize() {
        sut = new ProductExchangeFactory(BUFFER_SIZE);
    }

    @Test
    public void createProductExchange_whenCalled_shouldCreateProductExchangeWithProperProductName() {
        ProductExchange productExchange = sut.createProductExchange(PRODUCT_NAME);
        assertThat(productExchange, is(not(nullValue())));
        assertThat(productExchange.getProductName(), is(equalTo(PRODUCT_NAME)));
    }
}