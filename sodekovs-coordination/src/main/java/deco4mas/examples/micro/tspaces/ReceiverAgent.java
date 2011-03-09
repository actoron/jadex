package deco4mas.examples.micro.tspaces;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

import java.util.Map;

import deco4mas.coordinate.micro.CoordinateComponentStep;

/**
 * @author Thomas Preisler
 */
public class ReceiverAgent extends MicroAgent {

	@Override
	public IFuture agentCreated() {
		return super.agentCreated();
	}

	@Override
	public void executeBody() {

	}

	@Override
	public IFuture agentKilled() {
		return super.agentKilled();
	}

	public class InformCounterIncrementStep extends CoordinateComponentStep {

		public InformCounterIncrementStep(Map<String, Object> coordinatenParameter) {
			super(coordinatenParameter);

			this.counter = (Integer) coordinatenParameter.get("counter");
		}

		private Integer counter = 0;

		@Override
		public Object execute(IInternalAccess ia) {
			System.out.println("***** InformCounterIncrementStep called: " + counter);
			return null;
		}
	}
}