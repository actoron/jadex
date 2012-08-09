/**
 * 
 */
package sodekovs.coordination.distributed.examples.multiple_links;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import deco4mas.distributed.coordinate.interpreter.agent_state.CoordinationComponentStep;

/**
 * @author thomas
 * 
 */
@Agent
public class ConversationAgent extends MicroAgent {

	private String name = null;

	@Override
	public IFuture<Void> agentCreated() {
		this.name = getComponentAdapter().getComponentIdentifier().getName();

		String message = this.name + " greets you stranger!";
		saySomething(message);
		return IFuture.DONE;
	}

	@SuppressWarnings("unchecked")
	private void saySomething(String message) {
		waitFor(5000, new SpeakQuietStep(message));
		waitFor(1000, new SpeakLoudStep(message));
	}

	public class SpeakLoudStep extends CoordinationComponentStep {

		public String message = null;

		public SpeakLoudStep(String message) {
			this.message = message.toUpperCase();
		}

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println(name + " says: " + message);
			return IFuture.DONE;
		}
	}

	public class SpeakQuietStep extends CoordinationComponentStep {

		public String message = null;

		public SpeakQuietStep(String message) {
			this.message = message.toLowerCase();
		}

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println(name + " says: " + message);
			return IFuture.DONE;
		}
	}

	public class ListenStep extends CoordinationComponentStep {

		public String message = null;

		@Override
		public IFuture<Void> execute(IInternalAccess arg0) {
			System.out.println(name + " hears: " + message);
			return IFuture.DONE;
		}

	}
}
