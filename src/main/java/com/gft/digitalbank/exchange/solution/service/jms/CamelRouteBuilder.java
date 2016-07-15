package com.gft.digitalbank.exchange.solution.service.jms;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;
import com.gft.digitalbank.exchange.solution.service.tasks.scheduling.OrderNotFoundException;
import com.gft.digitalbank.exchange.solution.service.tasks.scheduling.SchedulingTaskCreator;
import com.gft.digitalbank.exchange.solution.service.tasks.scheduling.SchedulingTaskExecutor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by iozi on 2016-07-14.
 */
@Singleton
public class CamelRouteBuilder extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingMonitor.class);

    @Inject
    ProcessingMonitor processingMonitor;

    @Inject
    SchedulingTaskCreator tradingMessageDispatcher;

    @Inject
    SchedulingTaskExecutor schedulingTaskExecutor;

    private List<String> destinations;

    public void configure() {

        onException(OrderNotFoundException.class).redeliveryDelay(10).maximumRedeliveries(100);

        for (String destination : destinations) {
            from("activemq:queue:" + destination)
                    .choice()
                    .when().jsonpath("$[?(@.messageType=='ORDER')]")
                    .unmarshal().json(JsonLibrary.Gson, Order.class).bean(tradingMessageDispatcher, "dispatchOrder")
                    .when().jsonpath("$[?(@.messageType=='SHUTDOWN_NOTIFICATION')]")
                    .bean(processingMonitor, "decreaseBrokerCounter").stop()
                    .when().jsonpath("$[?(@.messageType=='MODIFICATION')]")
                    .unmarshal().json(JsonLibrary.Gson, Modification.class).bean(tradingMessageDispatcher, "dispatchModification")
                    .when().jsonpath("$[?(@.messageType=='CANCEL')]")
                    .unmarshal().json(JsonLibrary.Gson, Cancel.class).bean(tradingMessageDispatcher, "dispatchCancel").end()
                    .bean(schedulingTaskExecutor, "executeSchedulingTask");
        }
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }
}
