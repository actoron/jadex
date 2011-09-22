package deco4mas.examples.micro.tspaces;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import deco4mas.coordinate.annotation.CoordinationParameter;
import deco4mas.coordinate.interpreter.agent_state.CoordinationComponentStep;
import deco4mas.examples.micro.tspaces.ReceiverAgent.InformCounterIncrementStep;
import deco4mas.examples.micro.tspaces.SenderAgent.CounterIncrementStep;

/**
 * The {@link InformCounterIncrementStep} in this class is called whenever the coordination framework detects the that the {@link CounterIncrementStep} in the {@link SenderAgent} was started. The
 * coordination framework then starts the {@link InformCounterIncrementStep}.
 * 
 * @author Thomas Preisler
 */
public class ReceiverAgent extends MicroAgent {

	/**
	 * This step is called from the coordination framework. The class attributes match the match the coordination parameters. They have to be "public" so they are accessible from the coordination
	 * framework. The name of the attributes is the local name in the coordination configuration file.
	 * 
	 * @author Thomas Preisler
	 */
	public class InformCounterIncrementStep extends CoordinationComponentStep {

		@CoordinationParameter
		public Integer counter = 0;

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			// just print the current value of counter
			System.out.println("***** InformCounterIncrementStep called: " + counter);
			return IFuture.DONE;
		}
	}
}