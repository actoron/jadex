package deco4mas.examples.micro.tspaces;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import deco4mas.coordinate.annotation.CoordinationParameter;

/**
 * @author Thomas Preisler
 */
public class SenderAgent extends MicroAgent {

	@CoordinationParameter(steps = { "CounterIncrementStep" })
	public Integer counter = 0;

	@Override
	public IFuture agentCreated() {
		return super.agentCreated();
	}

	@Override
	public void executeBody() {
		waitFor(7000, new CounterIncrementStep());
	}

	@Override
	public IFuture agentKilled() {
		return super.agentKilled();
	}

	public class CounterIncrementStep implements IComponentStep {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * jadex.bridge.IComponentStep#execute(jadex.bridge.IInternalAccess)
		 */
		@Override
		public Object execute(IInternalAccess ia) {
			if (counter < 5) {
				counter++;
				System.out.println("***** Executing CounterIncrementStep: " + counter);

				if (counter < 5) {
					waitFor(2500, new CounterIncrementStep());
				}
			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CounterIncrementStep";
		}
	}
}