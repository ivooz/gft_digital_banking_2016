package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.service.monitoring.ShutdownNotificationListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.ConsumerType;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;

import java.util.List;

/**
 * Responsible for setting up Camel components and routes
 * <p>
 * Created by iozi on 2016-08-11.
 */
@Slf4j
@Singleton
public class CamelConfigurer {

    private static final String MQ_COMPONENT_NAME = "activemq";
    private static final String UNABLE_TO_ADD_CAMEL_ROUTES_EXCEPTION_MESSAGE = "Unable to add Camel routes";
    public static final String UNABLE_TO_START_CAMEL_CONTEXT_EXCEPTION_MESSAGE = "Unable to start Camel context";

    private final CamelContext camelContext;
    private final CamelRouteBuilder camelRouteBuilder;
    private final ShutdownNotificationListener shutdownNotificationListener;

    @Inject
    public CamelConfigurer(CamelContext camelContext,
                           CamelRouteBuilder camelRouteBuilder,
                           ShutdownNotificationListener shutdownNotificationListener) {
        this.camelContext = camelContext;
        this.camelRouteBuilder = camelRouteBuilder;
        this.shutdownNotificationListener = shutdownNotificationListener;
    }

    /**
     * Connects to ActiveMQ queues using ActiveMQ Component.
     * Adds Camel routes and additional Camel and AMQ settings.
     *
     * @param destinations AMQ queue names
     * @param brokerURL    for the AMQ connection
     * @throws StockExchangeStartupException
     */
    public void configure(@NonNull List<String> destinations, @NonNull String brokerURL) throws StockExchangeStartupException {
        JmsComponent activeMQComponent = ActiveMQComponent.activeMQComponent(brokerURL);
        JmsConfiguration configuration = activeMQComponent.getConfiguration();
        configuration.setConsumerType(ConsumerType.Simple);
        camelContext.addComponent(MQ_COMPONENT_NAME, activeMQComponent);
        camelRouteBuilder.setDestinations(destinations);
        shutdownNotificationListener.setBrokerCount(destinations.size());
        try {
            camelContext.addRoutes(camelRouteBuilder);
        } catch (Exception ex) {
            throw new StockExchangeStartupException(UNABLE_TO_ADD_CAMEL_ROUTES_EXCEPTION_MESSAGE, ex);
        }
        camelContext.setAllowUseOriginalMessage(false);
    }

    /**
     * Forwards ProcessingListener to the components that need it.
     *
     * @param processingListener to forward
     */
    public void registerProcessingListener(@NonNull ProcessingListener processingListener) {
        this.shutdownNotificationListener.setProcessingListener(processingListener);
    }

    /**
     * Starts cCamel context
     *
     * @throws StockExchangeStartupException
     */
    public void start() throws StockExchangeStartupException {
        try {
            camelContext.start();
        } catch (Exception ex) {
            throw new StockExchangeStartupException(UNABLE_TO_START_CAMEL_CONTEXT_EXCEPTION_MESSAGE, ex);
        }
    }
}
