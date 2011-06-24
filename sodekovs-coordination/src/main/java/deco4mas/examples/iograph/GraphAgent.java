/**
 * 
 */
package deco4mas.examples.iograph;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IArgument;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import deco4mas.coordinate.annotation.CoordinationParameter;
import deco4mas.coordinate.annotation.CoordinationStep;
import deco4mas.mechanism.graph.IOGraph;

/**
 * A simple agent used in the {@link IOGraph} example. The agents send there local id as coordination information to the next agent as specified in the {@link IOGraph}.
 * 
 * @author Thomas Preisler
 */
public class GraphAgent extends MicroAgent {

	private String graphId = null;

	@Override
	public IFuture agentCreated() {
		graphId = (String) getArgument("graphId");

		return super.agentCreated();
	}

	@Override
	public void executeBody() {
		if (graphId.equals("1")) {
			System.out.println("GraphAgent " + graphId + " is going to send a coordination information containing his id.");
			waitFor(7000, new SendStep(graphId));
		}
	}

	@Override
	public IFuture agentKilled() {
		return super.agentKilled();
	}

	/**
	 * Returns the {@link MicroAgentMetaInfo}.
	 * 
	 * @return the {@link MicroAgentMetaInfo}
	 */
	public static MicroAgentMetaInfo getMetaInfo() {
		MicroAgentMetaInfo meta = new MicroAgentMetaInfo();
		meta.setArguments(new IArgument[] { new Argument("graphId", "The Agents Graph Id", "String") });
		return meta;
	}

	/**
	 * This step is called to send the id as a coordination information to the next agent specified in the {@link IOGraph}.
	 */
	@CoordinationStep
	public class SendStep implements IComponentStep {

		@CoordinationParameter
		public String id = null;

		public SendStep(String id) {
			this.id = id;
		}

		@Override
		public Object execute(IInternalAccess ia) {
			System.out.println("SendStep called in GraphAgent " + id);
			return null;
		}
	}

	/**
	 * This step is called when a coordination information is received.
	 */
	@CoordinationStep
	public class ReceiveStep implements IComponentStep {

		@CoordinationParameter
		public String receivedId = null;

		@Override
		public Object execute(IInternalAccess ia) {
			System.out.println("ReceiveStep called in GraphAgent " + graphId + " receiving the id from GraphAgent " + receivedId);
			waitForTick(new SendStep(graphId));
			return null;
		}
	}
}