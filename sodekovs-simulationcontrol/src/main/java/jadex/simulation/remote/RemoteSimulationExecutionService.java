package jadex.simulation.remote;

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
 * Implementation of a remote execution service for (single) experiments.
 */
public class RemoteSimulationExecutionService extends BasicService implements IRemoteSimulationExecutionService {
	// -------- attributes --------

	/** The component. */
	protected ICapability comp;

	// -------- constructors --------

	/**
	 * Create a new shop service.
	 * 
	 * @param comp
	 *            The active component.
	 */
	public RemoteSimulationExecutionService(ICapability comp) {
		super(comp.getServiceProvider().getId(), IRemoteSimulationExecutionService.class, null);

		// System.out.println("created: "+name);
		this.comp = comp;
	}

	// -------- methods --------

	/**
	 * Get the name of the platform.
	 * 
	 * @return The name.
	 */
//	public String getPlatformName() {
//		return name;
//	}

	/**
	 * Simulate an experiment defined as application.xml and configured via an "*.configuration.xml"
	 * 
	 * @param item
	 *            The item.
	 */
	public IFuture executeExperiment(Map applicationArgs, HashMap<String,Object> clientArgs) {
		System.out.println("#RemoteSimulationExecutionService# ****************************************************");
		System.out.println("#RemoteSimulationExecutionService# Called Remote Service:  executeExperiment()");
		
		final Future ret = new Future();

		try {
			// start simulation execution
			IGoal[] goals = (IGoal[]) comp.getGoalbase().getGoals("startExecution");
			if (goals.length > 0) {
				ret.setException(new IllegalStateException("Can only handle one observation at a time."));
			} else {
				final IGoal oe = (IGoal) comp.getGoalbase().createGoal("startExecution");
				oe.getParameter("applicationConf").setValue(applicationArgs);
				oe.getParameter("clientConf").setValue(clientArgs);
				oe.addGoalListener(new IGoalListener() {
					public void goalFinished(AgentEvent ae) {
						System.out.println("#RemoteSimulationExecutionService# Finished service execution at: " + comp.getAgentName());
						if (oe.isSucceeded())
							ret.setResult(comp.getBeliefbase().getBelief("simulationFacts").getFact());
						else
							ret.setException(new RuntimeException("Goal failed"));
					}

					public void goalAdded(AgentEvent ae) {
					}
				});
				comp.getGoalbase().dispatchTopLevelGoal(oe);
			}

		} catch (Exception e) {
			System.out.println("#RemoteSimulationExecutionService# Could not start application...." + e);
		}
		return ret;
	}

//	/**
//	 * Get the string representation.
//	 * 
//	 * @return The string representation.
//	 */
//	public String toString() {
//		return name;
//	}
}
