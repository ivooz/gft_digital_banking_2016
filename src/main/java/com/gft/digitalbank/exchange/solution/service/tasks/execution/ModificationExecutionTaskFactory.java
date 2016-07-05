package com.gft.digitalbank.exchange.solution.service.tasks.execution;

import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.service.processing.ModificationExecutionTaskProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class ModificationExecutionTaskFactory {

    @Inject
    private ModificationExecutionTaskProcessor modificationTaskProcessor;

    public ModificationExecutionTask createModificationTask(Modification modification) {
        return new ModificationExecutionTask(modificationTaskProcessor, modification);
    }
}
