package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.Exchange;
import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.config.ApacheCamelConfigurer;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.config.StockExchangeStartupException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Your solution must implement the {@link Exchange} interface.
 */
@Slf4j
public class StockExchange implements Exchange {

    private static final String STARTUP_EXCEPTION_MESSAGE = "Could not start StockExchange.";

    private final ApacheCamelConfigurer apacheCamelConfigurer;

    public StockExchange() {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        this.apacheCamelConfigurer = injector.getInstance(ApacheCamelConfigurer.class);
    }

    @Override
    public void register(ProcessingListener processingListener) {
        apacheCamelConfigurer.registerProcessingListener(processingListener);
    }

    @Override
    public void setDestinations(List<String> destinations) {
        apacheCamelConfigurer.configure(destinations,"vm://localhost");
    }

    @Override
    public void start() {
        try {
            apacheCamelConfigurer.start();
        } catch (StockExchangeStartupException e) {
            log.error(STARTUP_EXCEPTION_MESSAGE,e);
        }
    }
}
