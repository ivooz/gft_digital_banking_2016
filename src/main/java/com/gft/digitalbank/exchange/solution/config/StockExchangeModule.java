package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.processing.CancelExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.processing.ModificationExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.processing.OrderExecutionTaskProcessor;
import com.gft.digitalbank.exchange.solution.service.processing.TradingMessageProcessor;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.apache.camel.guice.CamelModuleWithMatchingRoutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Google Guice utility component mapping injection point interfaces to implementations.
 * <p>
 * Created by iozi on 2016-06-28.
 */
public class StockExchangeModule extends CamelModuleWithMatchingRoutes {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockExchangeModule.class);
    private static final String PROPERTIES_FILE_NAME = "application.properties";
    private static final String UNABLE_TO_LOAD_PROPERTIES_FILE = "Unable to load properties file";

    @Override
    protected void configure() {
        super.configure();
        try {
            Names.bindProperties(binder(), getProperties());
            bind(new TypeLiteral<TradingMessageProcessor<Order>>() {
            }).to(OrderExecutionTaskProcessor.class);
            bind(new TypeLiteral<TradingMessageProcessor<Cancel>>() {
            }).to(CancelExecutionTaskProcessor.class);
            bind(new TypeLiteral<TradingMessageProcessor<Modification>>() {
            }).to(ModificationExecutionTaskProcessor.class);
        } catch (IOException ex) {
            LOGGER.error(UNABLE_TO_LOAD_PROPERTIES_FILE,ex);
            throw new IllegalStateException(UNABLE_TO_LOAD_PROPERTIES_FILE,ex);
        }

    }

    private Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));
        return properties;
    }
}
