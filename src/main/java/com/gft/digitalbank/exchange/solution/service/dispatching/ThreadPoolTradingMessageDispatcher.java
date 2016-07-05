package com.gft.digitalbank.exchange.solution.service.dispatching;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.events.OrderNotFoundMessageBroker;
import com.gft.digitalbank.exchange.solution.service.tasks.scheduling.CancelSchedulingTaskFactory;
import com.gft.digitalbank.exchange.solution.service.tasks.scheduling.ModificationSchedulingTaskFactory;
import com.gft.digitalbank.exchange.solution.service.tasks.scheduling.OrderSchedulingTaskFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class ThreadPoolTradingMessageDispatcher implements TradingMessageDispatcher {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 0;

    private final OrderSchedulingTaskFactory orderTaskFactory;
    private final ModificationSchedulingTaskFactory modificationTaskFactory;
    private final CancelSchedulingTaskFactory cancelTaskFactory;
    private final OrderNotFoundMessageBroker orderNotFoundMessageBroker;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Inject
    public ThreadPoolTradingMessageDispatcher(OrderSchedulingTaskFactory orderTaskFactory,
                                              ModificationSchedulingTaskFactory modificationTaskFactory,
                                              CancelSchedulingTaskFactory cancelTaskFactory,
                                              OrderNotFoundMessageBroker orderNotFoundMessageBroker) {
        this.orderTaskFactory = orderTaskFactory;
        this.modificationTaskFactory = modificationTaskFactory;
        this.cancelTaskFactory = cancelTaskFactory;
        this.orderNotFoundMessageBroker = orderNotFoundMessageBroker;
        this.threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, new PriorityBlockingQueue<>());
        this.orderNotFoundMessageBroker.subscribe(this);
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
    }

    @Override
    public void dispatchOrder(Order order) {
        threadPoolExecutor.execute(orderTaskFactory.createOrderTask(order));
    }

    @Override
    public void dispatchModification(Modification modification) {
        threadPoolExecutor.execute(modificationTaskFactory.createModificationTask(modification));
    }

    @Override
    public void dispatchCancel(Cancel cancel) {
        threadPoolExecutor.execute(cancelTaskFactory.createCancelTask(cancel));
    }

    @Override
    public void onOrderNotFound(Runnable runnable) {
        //Back to the queue
        scheduledThreadPoolExecutor.schedule(runnable,10,TimeUnit.MILLISECONDS);
    }

    @Override
    //TODO handle exception
    public void shutdownAndAwaitTermination() throws InterruptedException {
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.shutdown();
        scheduledThreadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

}
