package com.gft.digitalbank.exchange.solution.model;

import com.gft.digitalbank.exchange.solution.categories.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.gft.digitalbank.exchange.solution.model.Side.BUY;
import static com.gft.digitalbank.exchange.solution.model.Side.SELL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Created by Ivo Zieliński on 2016-08-11.
 */
@Category(UnitTest.class)
public class SideTest {

    @Test
    public void opposite_whenCalledForBuy_shouldReturnSell() {
        assertThat(BUY.opposite(), is(sameInstance(Side.SELL)));
    }

    @Test
    public void opposite_whenCalledForSell_shouldReturnBuy() {
        assertThat(SELL.opposite(), is(sameInstance(Side.BUY)));
    }

}