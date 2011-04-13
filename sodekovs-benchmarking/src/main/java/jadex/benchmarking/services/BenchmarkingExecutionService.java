package jadex.benchmarking.services;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the related interface.
 */
public class BenchmarkingExecutionService extends BasicService implements IBenchmarkingExecutionService {


	/** The component. */
	protected ICapability comp;



	/**
	 * Create a new shop service.
	 * 
	 * @param comp
	 *            The active component.
	 */
	public BenchmarkingExecutionService(ICapability comp) {
		super(comp.getServiceProvider().getId(), IBenchmarkingExecutionService.class, null);

		// System.out.println("created: "+name);
		this.comp = comp;
	}

	@Override
	public IFuture getBenchmarkStatus() {
		final Future ret = new Future();
		
		ret.setResult(comp.getAgentName());
		
		return ret;
	}

}
