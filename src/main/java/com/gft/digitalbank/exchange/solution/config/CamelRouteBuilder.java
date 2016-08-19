package com.gft.digitalbank.exchange.solution.config;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.monitoring.ShutdownNotificationProcessor;
import com.gft.digitalbank.exchange.solution.service.scheduling.OrderNotFoundException;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskCreator;
import com.gft.digitalbank.exchange.solution.service.scheduling.SchedulingTaskExecutor;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.NonNull;
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

    public static final String AMQ_MESSAGE_ENDPOINT = "direct:messages";
    public static final String ORDERS_ENDPOINT_NAME = "direct:orders";
    public static final String MODIFICATIONS_ENDPOINT_NAME = "direct:modifications";
    public static final String CANCELS_ENDPOINT_NAME = "direct:cancels";
    public static final String SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME = "direct:shutdown-notifications";
    public static final String SCHEDULING_TASKS_ENDPOINT_NAME = "direct:schedulingTasks";

    public static final String DYNAMIC_ROUTING_ROUTE_ID = "dynamicRouting";
    public static final String ORDERS_ROUTE_ID = "orders";
    public static final String MODIFICATIONS_ROUTE_ID = "modifications";
    public static final String CANCELS_ROUTE_ID = "cancels";
    public static final String SHUTDOWN_ROUTE_ID = "shutdownNotifications";

    private static final String ACTIVEMQ_QUEUE_PREFIX = "activemq:queue:";
    private static final String ORDER_IDENTIFYING_JSONPATH = "$[?(@.messageType=='ORDER')]";
    private static final JsonLibrary UNMARSHALLING_LIBRARY = JsonLibrary.Gson;
    private static final String MODIFICATION_IDENTIFYING_JSONPATH = "$[?(@.messageType=='MODIFICATION')]";
    private static final String CANCEL_IDENTIFYING_JSONPATH = "$[?(@.messageType=='CANCEL')]";
    private static final String SHUTDOWN_NOTIFICATION_IDENTIFYING_JSONPATH = "$[?(@.messageType=='SHUTDOWN_NOTIFICATION')]";
    private static final String CANNOT_CONFIGURE_ROUTES_WITHOUT_QUEUE_DESTINATIONS = "Cannot configure routes without queue destinations.";

    private final ShutdownNotificationProcessor shutdownNotificationProcessor;
    private final SchedulingTaskCreator<Order> orderSchedulingTaskCreator;
    private final SchedulingTaskCreator<Cancel> cancelSchedulingTaskCreator;
    private final SchedulingTaskCreator<Modification> modificationSchedulingTaskCreator;
    private final SchedulingTaskExecutor schedulingTaskExecutor;
    private final int maximumRedeliveriesOnFailure;
    private final int redeliveryDelayOnFailure;

    private List<String> destinations;

    @Inject
    public CamelRouteBuilder(ShutdownNotificationProcessor shutdownNotificationProcessor,
                             SchedulingTaskCreator<Order> orderSchedulingTaskCreator,
                             SchedulingTaskCreator<Cancel> cancelSchedulingTaskCreator,
                             SchedulingTaskCreator<Modification> modificationSchedulingTaskCreator,
                             SchedulingTaskExecutor schedulingTaskExecutor,
                             @Named("camel.failure.redeliveries") int maximumRedeliveriesOnFailure,
                             @Named("camel.failure.delay") int redeliveryDelayOnFailure) {
        this.shutdownNotificationProcessor = shutdownNotificationProcessor;
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

        Preconditions.checkState(destinations != null, CANNOT_CONFIGURE_ROUTES_WITHOUT_QUEUE_DESTINATIONS);
        defineErrorHandling();
        defineRoutes();
    }

    private void defineRoutes() {
        if (!destinations.isEmpty()) {
            from(convertDestinationsToQueueUrls()).to(AMQ_MESSAGE_ENDPOINT);
        }
        defineContentBasedRouting();
        defineOrdersRoute();
        defineModificationsRoute();
        defineCancelsRoute();
        defineShutdownNotificationsRoute();
        defineSchedulingTaskExecutionRoute();
    }

    private void defineContentBasedRouting() {
        from(AMQ_MESSAGE_ENDPOINT)
                .routeId(DYNAMIC_ROUTING_ROUTE_ID)
                .choice()
                .when().jsonpath(ORDER_IDENTIFYING_JSONPATH).to(ORDERS_ENDPOINT_NAME)
                .when().jsonpath(MODIFICATION_IDENTIFYING_JSONPATH).to(MODIFICATIONS_ENDPOINT_NAME)
                .when().jsonpath(CANCEL_IDENTIFYING_JSONPATH).to(CANCELS_ENDPOINT_NAME)
                .when().jsonpath(SHUTDOWN_NOTIFICATION_IDENTIFYING_JSONPATH).to(SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME)
                .end();
    }

    public void setDestinations(@NonNull List<String> destinations) {
        this.destinations = destinations;
    }

    private void defineOrdersRoute() {
        from(ORDERS_ENDPOINT_NAME)
                .routeId(ORDERS_ROUTE_ID)
                .unmarshal().json(UNMARSHALLING_LIBRARY, Order.class)
                .bean(orderSchedulingTaskCreator)
                .to(SCHEDULING_TASKS_ENDPOINT_NAME);
    }

    private void defineModificationsRoute() {
        from(MODIFICATIONS_ENDPOINT_NAME)
                .routeId(MODIFICATIONS_ROUTE_ID)
                .unmarshal().json(UNMARSHALLING_LIBRARY, Modification.class)
                .bean(modificationSchedulingTaskCreator)
                .to(SCHEDULING_TASKS_ENDPOINT_NAME);
    }

    private void defineCancelsRoute() {
        from(CANCELS_ENDPOINT_NAME)
                .routeId(CANCELS_ROUTE_ID)
                .unmarshal().json(UNMARSHALLING_LIBRARY, Cancel.class)
                .bean(cancelSchedulingTaskCreator)
                .to(SCHEDULING_TASKS_ENDPOINT_NAME);
    }

    private void defineShutdownNotificationsRoute() {
        from(SHUTDOWN_NOTIFICATIONS_ENDPOINT_NAME)
                .bean(shutdownNotificationProcessor).stop();
    }

    private void defineSchedulingTaskExecutionRoute() {
        from(SCHEDULING_TASKS_ENDPOINT_NAME)
                .routeId(SHUTDOWN_ROUTE_ID)
                .bean(schedulingTaskExecutor);
    }

    private String[] convertDestinationsToQueueUrls() {
        return destinations.stream()
                .map(ACTIVEMQ_QUEUE_PREFIX::concat)
                .collect(Collectors.toList())
                .toArray(new String[]{});
    }

    private void defineErrorHandling() {
//        errorHandler(defaultErrorHandler()
//                .allowRedeliveryWhileStopping(true)
//                .maximumRedeliveries(maximumRedeliveriesOnFailure)
//                .redeliveryDelay(redeliveryDelayOnFailure)
//                .retryAttemptedLogLevel(LoggingLevel.INFO));

        onException(OrderNotFoundException.class)
                .maximumRedeliveries(maximumRedeliveriesOnFailure)
                .redeliveryDelay(redeliveryDelayOnFailure);

    }

    public int getMaximumRedeliveriesOnFailure() {
        return maximumRedeliveriesOnFailure;
    }

    public int getRedeliveryDelayOnFailure() {
        return redeliveryDelayOnFailure;
    }
}
