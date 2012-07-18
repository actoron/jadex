/**
 * 
 */
package deco4mas.examples.iograph;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import deco4mas.coordinate.annotation.CoordinationParameter;
import deco4mas.coordinate.annotation.CoordinationStep;
import deco4mas.coordinate.interpreter.agent_state.CoordinationComponentStep;
import deco4mas.mechanism.graph.IOGraph;

/**
 * A simple agent used in the {@link IOGraph} example. The agents send there local id as coordination information to the next agent as specified in the {@link IOGraph}.
 * 
 * @author Thomas Preisler
 */
public class GraphAgent extends MicroAgent {

	private String name = null;

	@Override
	public IFuture<Void> agentCreated() {
		name = getComponentDescription().getName().getLocalName();

		if (name.equals("Graph1")) {
			System.out.println("GraphAgent " + name + " is going to send a coordination information containing his id.");
			waitFor(7000, new SendStep(name));
		}

		return IFuture.DONE;
	}

	/**
	 * This step is called to send the id as a coordination information to the next agent specified in the {@link IOGraph}.
	 */
	@CoordinationStep
	public class SendStep extends CoordinationComponentStep {

		@CoordinationParameter
		public String id = null;

		public SendStep(String id) {
			this.id = id;
		}

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("SendStep called in GraphAgent " + id);
			return IFuture.DONE;
		}
	}

	/**
	 * This step is called when a coordination information is received.
	 */
	@CoordinationStep
	public class ReceiveStep extends CoordinationComponentStep {

		@CoordinationParameter
		public String receivedId = null;

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("ReceiveStep called in GraphAgent " + name + " receiving the id from GraphAgent " + receivedId);
			waitForTick(new SendStep(name));
			return IFuture.DONE;
		}
	}
}