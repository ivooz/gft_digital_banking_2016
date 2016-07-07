package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.Exchange;
import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.service.jms.JMSSessionManager;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.jms.MessageConsumer;
import java.util.ArrayList;
import java.util.List;

/**
 * Your solution must implement the {@link Exchange} interface.
 */
public class StockExchange implements Exchange {

    List<MessageConsumer> messageConsumers = new ArrayList<>();

    private ProcessingListener processingListener;
    private JMSSessionManager brokerConnector;
    private ProcessingMonitor processingMonitor;

    public StockExchange() {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        this.brokerConnector = injector.getInstance(JMSSessionManager.class);
        this.processingMonitor = injector.getInstance(ProcessingMonitor.class);
    }

    @Override
    public void register(ProcessingListener processingListener) {
        this.processingListener = processingListener;
        processingMonitor.setProcessingListener(processingListener);
    }

    @Override
    public void setDestinations(List<String> destinations) {
        processingMonitor.setBrokerCount(destinations.size());
        brokerConnector.connectWithBrokers(destinations);
    }

    @Override
    public void start() {
    }
}
