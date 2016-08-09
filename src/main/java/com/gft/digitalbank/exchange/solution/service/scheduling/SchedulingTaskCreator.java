package com.gft.digitalbank.exchange.solution.service.scheduling;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class SchedulingTaskCreator {

    private final OrderSchedulingTaskFactory orderSchedulingTaskFactory;
    private final ModificationSchedulingTaskFactory modificationSchedulingTaskFactory;
    private final CancelSchedulingTaskFactory cancelSchedulingTaskFactory;

    @Inject
    public SchedulingTaskCreator(OrderSchedulingTaskFactory orderSchedulingTaskFactory,
                                 ModificationSchedulingTaskFactory modificationSchedulingTaskFactory,
                                 CancelSchedulingTaskFactory cancelSchedulingTaskFactory) {
        this.orderSchedulingTaskFactory = orderSchedulingTaskFactory;
        this.modificationSchedulingTaskFactory = modificationSchedulingTaskFactory;
        this.cancelSchedulingTaskFactory = cancelSchedulingTaskFactory;
    }

    public SchedulingTask createOrderSchedulingTask(Order order) {
        return orderSchedulingTaskFactory.createOrderTask(order);
    }

    public SchedulingTask createModificationSchedulingTask(Modification modification) {
        return modificationSchedulingTaskFactory.createModificationTask(modification);
    }

    public SchedulingTask createCancelSchedulingTask(Cancel cancel) {
        return cancelSchedulingTaskFactory.createCancelTask(cancel);
    }
}
