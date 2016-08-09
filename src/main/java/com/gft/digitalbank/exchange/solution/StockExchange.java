package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.Exchange;
import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.config.CamelRouteBuilder;
import com.gft.digitalbank.exchange.solution.config.StockExchangeModule;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.ConsumerType;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;

import javax.jms.MessageConsumer;
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
        try {
            processingMonitor.setBrokerCount(destinations.size());
            JmsComponent activeMQComponent = ActiveMQComponent.activeMQComponent("vm://localhost");
            JmsConfiguration configuration = activeMQComponent.getConfiguration();
            configuration.setConsumerType(ConsumerType.Simple);
            camelContext.addComponent("activemq", activeMQComponent);
            camelRouteBuilder.setDestinations(destinations);
            camelContext.addRoutes(camelRouteBuilder);
            camelContext.setAllowUseOriginalMessage(false);
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
