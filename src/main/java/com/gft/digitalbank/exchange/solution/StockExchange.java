package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.Exchange;
import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.config.CamelConfigurer;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.config.StockExchangeStartupException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Your solution must implement the {@link Exchange} interface.
 */
@Slf4j
public class StockExchange implements Exchange {

    private static final String BROKER_URL = "vm://localhost";
    private static final String STARTUP_EXCEPTION_MESSAGE = "Could not start StockExchange.";
    private static final String UNABLE_TO_CONFIGURE_APACHE_CAMEL_MESSAGE = "Unable to configure Apache Camel";

    private final CamelConfigurer camelConfigurer;

    public StockExchange() {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        this.camelConfigurer = injector.getInstance(CamelConfigurer.class);
    }

    @Override
    public void register(@NonNull ProcessingListener processingListener) {
        camelConfigurer.registerProcessingListener(processingListener);
    }

    @Override
    public void setDestinations(@NonNull List<String> destinations) {
        try {
            camelConfigurer.configure(destinations, BROKER_URL);
        } catch (StockExchangeStartupException ex) {
            log.error(UNABLE_TO_CONFIGURE_APACHE_CAMEL_MESSAGE, ex);
        }
    }

    @Override
    public void start() {
        try {
            camelConfigurer.start();
        } catch (StockExchangeStartupException e) {
            log.error(STARTUP_EXCEPTION_MESSAGE, e);
        }
    }
}
