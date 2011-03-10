package deco4mas.examples.micro.tspaces;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

import java.util.Map;

import deco4mas.coordinate.micro.CoordinateComponentStep;
import deco4mas.examples.micro.tspaces.SenderAgent.CounterIncrementStep;

/**
 * The {@link InformCounterIncrementStep} in this class is called whenever the
 * coordination framework detects the that the {@link CounterIncrementStep} in
 * the {@link SenderAgent} was started. The coordination framework then starts
 * the {@link InformCounterIncrementStep}.
 * 
 * @author Thomas Preisler
 */
public class ReceiverAgent extends MicroAgent {

	@Override
	public IFuture agentCreated() {
		return super.agentCreated();
	}

	@Override
	public void executeBody() {
		// does nothing
	}

	@Override
	public IFuture agentKilled() {
		return super.agentKilled();
	}

	/**
	 * This step is called from the coordination framework. The class has to
	 * extend {@link CoordinateComponentStep} to make sure that the constructor
	 * is implemented. The coordination parameter are passed to this step over
	 * the {@link Map} argument in the constructor.
	 * 
	 * @author Thomas Preisler
	 */
	public class InformCounterIncrementStep extends CoordinateComponentStep {

		private Integer counter = 0;

		/**
		 * Constructor.
		 * 
		 * @param coordinatenParameter
		 *            a {@link Map} containing the coordination parameters.
		 */
		public InformCounterIncrementStep(Map<String, Object> coordinatenParameter) {
			super(coordinatenParameter);

			this.counter = (Integer) coordinatenParameter.get("counter");
		}

		@Override
		public Object execute(IInternalAccess ia) {
			// just print the current value of counter
			System.out.println("***** InformCounterIncrementStep called: " + counter);
			return null;
		}
	}
}