package com.gft.digitalbank.exchange.solution.service.tasks.scheduling;

import com.gft.digitalbank.exchange.solution.service.processing.ProductLedgerIndex;
import com.gft.digitalbank.exchange.solution.service.tasks.execution.ExecutionTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Created by iozi on 2016-07-01.
 */
@Singleton
public class TasksExecutionFinisher {

    @Inject
    private ProductLedgerIndex productLedgerIndex;

    public void finishAllTasks() {
        productLedgerIndex.getAllLedgers().parallelStream().forEach(productLedger -> {
                    while (true) {
                        Optional<ExecutionTask> executionTask = productLedger.getNextTaskToExecute();
                        if(!executionTask.isPresent()) {
                            break;
                        }
                        executionTask.get().execute(productLedger);
                    }
                }
        );
    }
}
