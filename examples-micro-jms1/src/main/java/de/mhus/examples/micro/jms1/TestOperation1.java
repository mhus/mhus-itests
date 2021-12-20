package de.mhus.examples.micro.jms1;

import org.osgi.service.component.annotations.Component;

import de.mhus.lib.annotations.strategy.OperationDescription;
import de.mhus.lib.core.operation.AbstractOperation;
import de.mhus.lib.core.operation.Operation;
import de.mhus.lib.core.operation.OperationResult;
import de.mhus.lib.core.operation.Successful;
import de.mhus.lib.core.operation.TaskContext;

@Component(service = Operation.class)
@OperationDescription(version = "1.0.0", labels="test=Aloa")
public class TestOperation1 extends AbstractOperation {

	@Override
	protected OperationResult doExecute2(TaskContext context) throws Exception {
		return new Successful(this, "Hello " + context.getParameters().getString("name", "World"), 1);
	}

}
