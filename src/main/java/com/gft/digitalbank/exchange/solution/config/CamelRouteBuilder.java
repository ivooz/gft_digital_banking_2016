package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;
import com.gft.digitalbank.exchange.solution.service.scheduling.OrderNotFoundException;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskCreator;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskExecutor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.List;

/**
 * Created by iozi on 2016-07-14.
 */
@Singleton
public class CamelRouteBuilder extends RouteBuilder {

    private final ProcessingMonitor processingMonitor;
    private final SchedulingTaskCreator tradingMessageDispatcher;
    private final SchedulingTaskExecutor schedulingTaskExecutor;

    private List<String> destinations;

    @Inject
    public CamelRouteBuilder(ProcessingMonitor processingMonitor,
                             SchedulingTaskCreator tradingMessageDispatcher,
                             SchedulingTaskExecutor schedulingTaskExecutor) {
        this.processingMonitor = processingMonitor;
        this.tradingMessageDispatcher = tradingMessageDispatcher;
        this.schedulingTaskExecutor = schedulingTaskExecutor;
    }

    public void configure() {
        onException(OrderNotFoundException.class).redeliveryDelay(10).maximumRedeliveries(100);
        for (String destination : destinations) {
            from("activemq:queue:" + destination+"?exchangePattern=InOnly")
                    .choice()
                    .when(body().contains("ORDER"))
                    .unmarshal().json(JsonLibrary.Jackson, Order.class).bean(tradingMessageDispatcher, "dispatchOrder")
                    .when(body().contains("MODIFICATION"))
                    .unmarshal().json(JsonLibrary.Jackson, Modification.class).bean(tradingMessageDispatcher, "dispatchModification")
                    .when(body().contains("CANCEL"))
                    .unmarshal().json(JsonLibrary.Jackson, Cancel.class).bean(tradingMessageDispatcher, "dispatchCancel")
                    .when(body().contains("SHUTDOWN_NOTIFICATION"))
                    .bean(processingMonitor, "decreaseBrokerCounter").stop().end()
                    .bean(schedulingTaskExecutor, "executeSchedulingTask");
        }
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }
}
