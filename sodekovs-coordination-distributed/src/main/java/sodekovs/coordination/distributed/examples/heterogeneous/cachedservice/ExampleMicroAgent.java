package sodekovs.coordination.distributed.examples.heterogeneous.cachedservice;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import deco4mas.distributed.coordinate.interpreter.agent_state.CoordinationComponentStep;

/**
 * The example micro agent. The {@link ReceiveHelloStep} is called by the coordination framework if an according event occurs. The parameter {@link ReceiveHelloStep#message} is received over the
 * coordination framework.
 * 
 * @author Thomas Preisler
 */
@Agent
public class ExampleMicroAgent extends MicroAgent {

	@Override
	public IFuture<Void> agentCreated() {
		System.out.println("ExampleMicroAgent created." + ExampleMicroAgent.this.getAgentName());

		return IFuture.DONE;
	}

	@Override
	public void executeBody() {
	}

	public class ReceiveHelloStep extends CoordinationComponentStep {

		public String message = null;

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("ExampleMicroAgent" + ExampleMicroAgent.this.getAgentName() + " execute() in ReceiveHelloStep called with message:");
			System.out.println("\t" + message);
			return IFuture.DONE;
		}
	}
}