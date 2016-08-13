package com.gft.digitalbank.exchange.solution.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;

/**
 * Created by Ivo on 12/08/16.
 */
public class AsyncTaskRunner {

    public void runWaitingTask(Lock lock, Runnable runnable) {
        CompletableFuture.runAsync(() -> {
            lock.tryLock();
            runnable.run();
        });
    }
}
