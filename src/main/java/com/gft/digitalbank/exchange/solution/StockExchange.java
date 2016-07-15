package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.Exchange;
import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.service.jms.CamelRouteBuilder;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;

import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Your solution must implement the {@link Exchange} interface.
 */
public class StockExchange implements Exchange {

    List<MessageConsumer> messageConsumers = new ArrayList<>();

    private ProcessingMonitor processingMonitor;
    private CamelRouteBuilder camelRouteBuilder;
    private CamelContext camelContext;

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
        Context context = null;
        try {
            context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
            JmsComponent jmsComponent = JmsComponent.jmsComponent(connectionFactory);
            JmsConfiguration configuration = new JmsConfiguration();
            configuration.setConnectionFactory(connectionFactory);
            jmsComponent.setConfiguration(configuration);
            camelContext.addComponent("activemq", jmsComponent);
            camelRouteBuilder.setDestinations(destinations);
            camelContext.addRoutes(camelRouteBuilder);
        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
