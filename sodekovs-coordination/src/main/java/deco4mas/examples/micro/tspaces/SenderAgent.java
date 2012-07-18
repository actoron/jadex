package deco4mas.examples.micro.tspaces;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import deco4mas.coordinate.annotation.CoordinationParameter;
import deco4mas.coordinate.interpreter.agent_state.CoordinationComponentStep;

/**
 * This class is under observation by the coordination framework. Whenever the {@link CounterIncrementStep} is started this is event is observed by the coordination framework and the attribute
 * {@link SenderAgent#counter} is propagated to the {@link ReceiverAgent}.
 * 
 * @author Thomas Preisler
 */
public class SenderAgent extends MicroAgent {

	@Override
	public IFuture<Void> agentCreated() {
		// wait 7s then start the first step
		waitFor(7000, new CounterIncrementStep(0));

		return IFuture.DONE;
	}

	/**
	 * Whenever this step is started the coordination framework observes this event and propagates the {@link SenderAgent#counter} attribute to the {@link ReceiverAgent}. The step is referenced in the
	 * DeCoMAS configuration file by it simple class name (not full qualified).
	 * 
	 * @author Thomas Preisler
	 */
	public class CounterIncrementStep extends CoordinationComponentStep {

		/**
		 * This attribute is propagated through the coordination framework. It has to be public so it could be read using reflection in the coordination framework. The annotation is currently optional
		 * and only helps to understand the code better.
		 */
		@CoordinationParameter
		public int counter = -1;

		public CounterIncrementStep(int counter) {
			this.counter = counter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see jadex.bridge.IComponentStep#execute(jadex.bridge.IInternalAccess)
		 */
		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			// only execute it 5 times
			if (counter < 5) {
				counter++;
				System.out.println("***** Executing CounterIncrementStep: " + counter);

				if (counter < 5) {
					// wait for 2,5s then start the next step
					waitFor(2500, new CounterIncrementStep(counter));
				}
			}

			return IFuture.DONE;
		}
	}
}