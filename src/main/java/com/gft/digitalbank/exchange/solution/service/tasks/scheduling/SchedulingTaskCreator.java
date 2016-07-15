package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class SchedulingTaskCreator {

    private final OrderSchedulingTaskFactory orderTaskFactory;
    private final ModificationSchedulingTaskFactory modificationTaskFactory;
    private final CancelSchedulingTaskFactory cancelTaskFactory;

    @Inject
    public SchedulingTaskCreator(OrderSchedulingTaskFactory orderTaskFactory,
                                 ModificationSchedulingTaskFactory modificationTaskFactory,
                                 CancelSchedulingTaskFactory cancelTaskFactory) {
        this.orderTaskFactory = orderTaskFactory;
        this.modificationTaskFactory = modificationTaskFactory;
        this.cancelTaskFactory = cancelTaskFactory;
    }

    public SchedulingTask dispatchOrder(Order order) {
        return orderTaskFactory.createOrderTask(order);
    }

    public SchedulingTask dispatchModification(Modification modification) {
        return modificationTaskFactory.createModificationTask(modification);
    }

    public SchedulingTask dispatchCancel(Cancel cancel) {
        return cancelTaskFactory.createCancelTask(cancel);
    }
}
