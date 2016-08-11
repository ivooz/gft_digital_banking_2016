package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.service.monitoring.ShutdownNotificationListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.ConsumerType;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;

import java.util.List;

/**
 * Created by iozi on 2016-08-11.
 */
@Singleton
public class StockExchangeBootstrapper {

    private final CamelContext camelContext;
    private final CamelRouteBuilder camelRouteBuilder;
    private final ShutdownNotificationListener shutdownNotificationListener;

    @Inject
    public StockExchangeBootstrapper(CamelContext camelContext,
                                     CamelRouteBuilder camelRouteBuilder,
                                     ShutdownNotificationListener shutdownNotificationListener) {
        this.camelContext = camelContext;
        this.camelRouteBuilder = camelRouteBuilder;
        this.shutdownNotificationListener = shutdownNotificationListener;
    }

    public void configure(List<String> destinations) {
        JmsComponent activeMQComponent = ActiveMQComponent.activeMQComponent("vm://localhost");
        JmsConfiguration configuration = activeMQComponent.getConfiguration();
        configuration.setConsumerType(ConsumerType.Simple);
        camelContext.addComponent("activemq", activeMQComponent);
        camelRouteBuilder.setDestinations(destinations);
        shutdownNotificationListener.setBrokerCount(destinations.size());
        try {
            camelContext.addRoutes(camelRouteBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        camelContext.setAllowUseOriginalMessage(false);
    }

    public void registerProcessingListener(ProcessingListener processingListener) {
        this.shutdownNotificationListener.setProcessingListener(processingListener);
    }

    public void start() throws StockExchangeStartupException {
        try {
            camelContext.start();
        } catch (Exception e) {
            //TODO
            throw new StockExchangeStartupException("Unable to start Camel context",e);
        }
    }
}
