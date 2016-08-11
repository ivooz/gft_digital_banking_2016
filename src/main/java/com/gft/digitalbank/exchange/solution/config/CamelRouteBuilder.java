package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.monitoring.ShutdownNotificationListener;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskCreator;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskExecutor;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.NonNull;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for creating Apache camel routes that define the flow of messages across the program components.
 * <p>
 * Created by Ivo Zieliński on 2016-07-14.
 */
@Singleton
public class CamelRouteBuilder extends RouteBuilder {

    private static final String ACTIVEMQ_QUEUE_PREFIX = "activemq:queue:";
    private static final String ORDER_IDENTIFYING_JSONPATH = "$[?(@.messageType=='ORDER')]";
    private static final String SCHEDULING_TASK_CREATION_METHOD_NAME = "createSchedulingTask";
    private static final JsonLibrary UNMARSHALLING_LIBRARY = JsonLibrary.Gson;
    private static final String MODIFICATION_IDENTIFYING_JSONPATH = "$[?(@.messageType=='MODIFICATION')]";
    private static final String CANCEL_IDENTIFYING_JSONPATH = "$[?(@.messageType=='CANCEL')]";
    private static final String SHUTDOWN_NOTIFICATION_IDENTIFYING_JSONPATH = "$[?(@.messageType=='SHUTDOWN_NOTIFICATION')]";
    private static final String SHUTDOWN_NOTIFICATION_HANDLER_METHOD_NAME = "handleShutdownNotification";
    private static final String SCHEDULING_TASK_EXECUTOR_METHOD_NAME = "executeSchedulingTask";
    private static final String CANNOT_CONFIGURE_ROUTES_WITHOUT_QUEUE_DESTINATIONS = "Cannot configure routes without queue destinations.";

    private final ShutdownNotificationListener shutdownNotificationListener;
    private final SchedulingTaskCreator<Order> orderSchedulingTaskCreator;
    private final SchedulingTaskCreator<Cancel> cancelSchedulingTaskCreator;
    private final SchedulingTaskCreator<Modification> modificationSchedulingTaskCreator;
    private final SchedulingTaskExecutor schedulingTaskExecutor;
    private final int maximumRedeliveriesOnFailure;
    private final int redeliveryDelayOnFailure;

    private List<String> destinations;

    @Inject
    public CamelRouteBuilder(ShutdownNotificationListener shutdownNotificationListener,
                             SchedulingTaskCreator<Order> orderSchedulingTaskCreator,
                             SchedulingTaskCreator<Cancel> cancelSchedulingTaskCreator,
                             SchedulingTaskCreator<Modification> modificationSchedulingTaskCreator,
                             SchedulingTaskExecutor schedulingTaskExecutor,
                             @Named("camel.failure.redeliveries") int maximumRedeliveriesOnFailure,
                             @Named("camel.failure.delay") int redeliveryDelayOnFailure) {
        this.shutdownNotificationListener = shutdownNotificationListener;
        this.orderSchedulingTaskCreator = orderSchedulingTaskCreator;
        this.cancelSchedulingTaskCreator = cancelSchedulingTaskCreator;
        this.modificationSchedulingTaskCreator = modificationSchedulingTaskCreator;
        this.schedulingTaskExecutor = schedulingTaskExecutor;
        this.maximumRedeliveriesOnFailure = maximumRedeliveriesOnFailure;
        this.redeliveryDelayOnFailure = redeliveryDelayOnFailure;
    }

    /**
     * Configures the Camel routes given the previously supplied list of destinations.
     */
    public void configure() {
        Preconditions.checkNotNull(destinations, CANNOT_CONFIGURE_ROUTES_WITHOUT_QUEUE_DESTINATIONS);
        defineErrorHandling();
        defineRoutes();
    }

    private void defineRoutes() {
        from(convertDestinationsToQueueUrls())
                .choice()
                //Create task handling dependent on the message type
                .when().jsonpath(ORDER_IDENTIFYING_JSONPATH)
                .unmarshal().json(UNMARSHALLING_LIBRARY, Order.class)
                .bean(orderSchedulingTaskCreator, SCHEDULING_TASK_CREATION_METHOD_NAME)
                .when().jsonpath(MODIFICATION_IDENTIFYING_JSONPATH)
                .unmarshal().json(UNMARSHALLING_LIBRARY, Modification.class)
                .bean(modificationSchedulingTaskCreator, SCHEDULING_TASK_CREATION_METHOD_NAME)
                .when().jsonpath(CANCEL_IDENTIFYING_JSONPATH)
                .unmarshal().json(UNMARSHALLING_LIBRARY, Cancel.class)
                .bean(cancelSchedulingTaskCreator, SCHEDULING_TASK_CREATION_METHOD_NAME)
                .when().jsonpath(SHUTDOWN_NOTIFICATION_IDENTIFYING_JSONPATH)
                .bean(shutdownNotificationListener, SHUTDOWN_NOTIFICATION_HANDLER_METHOD_NAME).stop().end()
                .bean(schedulingTaskExecutor, SCHEDULING_TASK_EXECUTOR_METHOD_NAME);
    }

    public void setDestinations(@NonNull List<String> destinations) {
        this.destinations = destinations;
    }

    private String[] convertDestinationsToQueueUrls() {
        return destinations.stream()
                .map(ACTIVEMQ_QUEUE_PREFIX::concat)
                .collect(Collectors.toList())
                .toArray(new String[]{});
    }

    private void defineErrorHandling() {
        errorHandler(defaultErrorHandler()
                .allowRedeliveryWhileStopping(true)
                .maximumRedeliveries(maximumRedeliveriesOnFailure)
                .redeliveryDelay(redeliveryDelayOnFailure)
                .retryAttemptedLogLevel(LoggingLevel.INFO));
    }

}
