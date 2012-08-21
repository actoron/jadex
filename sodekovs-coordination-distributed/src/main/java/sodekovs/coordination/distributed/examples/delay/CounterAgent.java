package sodekovs.coordination.distributed.examples.delay;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import deco4mas.distributed.coordinate.interpreter.agent_state.CoordinationComponentStep;

@Agent
public class CounterAgent extends MicroAgent {

	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Void> agentCreated() {
		Integer counter = 0;
		waitFor(10000, new CountStep(counter));
		return IFuture.DONE;
	}

	public class CountStep extends CoordinationComponentStep {

		public Integer counter = null;

		public CountStep(Integer counter) {
			this.counter = counter;
		}

		@SuppressWarnings("unchecked")
		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			counter++;
			waitFor(1000, this);
			return IFuture.DONE;
		}

	}

	public class PrintStep extends CoordinationComponentStep {

		public Integer counter = null;

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("counter " + counter);
			return IFuture.DONE;
		}
	}
}
