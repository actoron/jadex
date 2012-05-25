package sodekovs.coordination.distributed.examples.heterogeneous.service;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;

import java.util.HashMap;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
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
		System.out.println("ExampleMicroAgent created ." + this.getAgentAdapter().getComponentIdentifier().getName());

		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> agentKilled() {
		System.out.println("ExampleMicroAgent killed. " + this.getAgentAdapter().getComponentIdentifier().getName());

		return IFuture.DONE;
	}

	public class ReceiveHelloStep extends CoordinationComponentStep {

		public String message = null;

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {

			// check CoordinationContext
			CoordinationSpace coordSpace = (CoordinationSpace) getParentAccess().getExtension("mycoordspace").get(new ThreadSuspendable());
			HashMap<String, Object> appArgs = (HashMap<String, Object>) coordSpace.getExternalAccess().getArguments().get(new ThreadSuspendable());
			String coordinationContextID = (String) appArgs.get("CoordinationContextID");

			System.out.println("#ExampleMicroAgent " + ExampleMicroAgent.this.getAgentName() + " - I belong to CoordinationContext: " + coordinationContextID
					+ "#  execute() in ReceiveHelloStep called with message:");
			System.out.println("\t" + message);
			return IFuture.DONE;
		}
	}
}