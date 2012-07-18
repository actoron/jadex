package deco4mas.examples.heterogeneous;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import deco4mas.coordinate.annotation.CoordinationParameter;
import deco4mas.coordinate.annotation.CoordinationStep;
import deco4mas.coordinate.interpreter.agent_state.CoordinationComponentStep;

/**
 * The example micro agent. After creation the agents waits for 10s before the {@link SayHelloStep} is started. This {@link IComponentStep} is observed by the coordination framework and the parameter
 * {@link SayHelloStep#message} is passed over the coordination medium to the receiving agents. The {@link ReceiveHelloStep} is called by the coordination framework if an according event occurs. The
 * parameter {@link ReceiveHelloStep#message} is received over the coordination framework.
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

	@CoordinationStep
	public class SayHelloStep extends CoordinationComponentStep {

		@CoordinationParameter
		public String message = null;

		public SayHelloStep(String message) {
			this.message = message;
		}

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("ExampleMicroAgent execute() in SayHelloStep called with message:");
			System.out.println("\t" + message);
			return IFuture.DONE;
		}
	}

	@CoordinationStep
	public class ReceiveHelloStep extends CoordinationComponentStep {

		@CoordinationParameter
		public String message = null;

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("ExampleMicroAgent" + ExampleMicroAgent.this.getAgentName() + " execute() in ReceiveHelloStep called with message:");
			System.out.println("\t" + message);
			return IFuture.DONE;
		}
	}
}