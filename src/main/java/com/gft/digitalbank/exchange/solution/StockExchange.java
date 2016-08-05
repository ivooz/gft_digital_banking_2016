package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.Exchange;
import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.config.CamelRouteBuilder;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Your solution must implement the {@link Exchange} interface.
 */
public class StockExchange implements Exchange {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockExchange.class);

    private final ProcessingMonitor processingMonitor;
    private final CamelRouteBuilder camelRouteBuilder;
    private final CamelContext camelContext;

    public StockExchange() {
        Injector injector = Guice.createInjector(new StockExchangeModule());
        this.processingMonitor = injector.getInstance(ProcessingMonitor.class);
        this.camelRouteBuilder = injector.getInstance(CamelRouteBuilder.class);
        this.camelContext = injector.getInstance(CamelContext.class);
    }

    @Override
    public void register(ProcessingListener processingListener) {
        processingMonitor.setProcessingListener(processingListener);
    }

    @Override
    public void setDestinations(List<String> destinations) {
        processingMonitor.setBrokerCount(destinations.size());
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        activeMQConnectionFactory.setDispatchAsync(true);
        activeMQConnectionFactory.setCopyMessageOnSend(false);
        activeMQConnectionFactory.getPrefetchPolicy().setAll(100000);
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        pooledConnectionFactory.setMaxConnections(10);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(500);
        JmsComponent activeMQComponent = ActiveMQComponent.jmsComponentAutoAcknowledge(pooledConnectionFactory);
        JmsConfiguration jmsConfiguration = activeMQComponent.getConfiguration();
        jmsConfiguration.setTransacted(false);
        jmsConfiguration.setCacheLevelName("CACHE_CONSUMER");
        jmsConfiguration.setConcurrentConsumers(25);
        jmsConfiguration.setMaxConcurrentConsumers(25);
        jmsConfiguration.setAsyncConsumer(true);
        camelContext.addComponent("activemq", activeMQComponent);
        camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxPoolSize(500);
        camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setPoolSize(200);
        camelRouteBuilder.setDestinations(destinations);
        try {
            camelContext.addRoutes(camelRouteBuilder);
        } catch (Exception ex) {
            LOGGER.error("Failed to create Camel routes:", ex);
        }
        camelContext.setAllowUseOriginalMessage(false);
        camelContext.setStreamCaching(true);
    }

    @Override
    public void start() {
        try {
            camelContext.start();
        } catch (Exception ex) {
            LOGGER.error("Failed to start Camel:", ex);
        }
    }
}
