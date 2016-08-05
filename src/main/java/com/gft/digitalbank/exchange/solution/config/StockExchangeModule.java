package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.CancelExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.processing.ModificationExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.processing.OrderExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.processing.TradingMessageProcessor;
import com.google.inject.TypeLiteral;
import org.apache.camel.guice.CamelModuleWithMatchingRoutes;

/**
 * Google Guice utility component maping injection point interfaces to implementations.
 * <p>
 * Created by iozi on 2016-06-28.
 */
public class StockExchangeModule extends CamelModuleWithMatchingRoutes {

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<TradingMessageProcessor<Order>>(){}).to(OrderExecutionTaskProcessor.class);
        bind(new TypeLiteral<TradingMessageProcessor<Cancel>>(){}).to(CancelExecutionTaskProcessor.class);
        bind(new TypeLiteral<TradingMessageProcessor<Modification>>(){}).to(ModificationExecutionTaskProcessor.class);
    }
}
