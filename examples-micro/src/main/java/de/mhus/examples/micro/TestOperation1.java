package de.mhus.examples.micro;

import org.osgi.service.component.annotations.Component;

import de.mhus.lib.annotations.strategy.OperationService;
import de.mhus.lib.core.operation.AbstractOperation;
import de.mhus.lib.core.operation.Operation;
import de.mhus.lib.core.operation.OperationResult;
import de.mhus.lib.core.operation.Successful;
import de.mhus.lib.core.operation.TaskContext;

@Component(service = Operation.class)
@OperationService(version = "1.0.0", labels="test=Aloa")
public class TestOperation1 extends AbstractOperation {

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		return new Successful(this, "Hello " + context.getParameters().getString("name", "World"), 1);
	}

}
