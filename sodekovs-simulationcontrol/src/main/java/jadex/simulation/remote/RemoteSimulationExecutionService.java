package jadex.simulation.remote;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.FileHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a remote execution service for (single) experiments.
 */
public class RemoteSimulationExecutionService extends BasicService implements IRemoteSimulationExecutionService {
	// -------- attributes --------

	/** The component. */
	protected ICapability comp;

	/** The name of the platform. */
	protected String name;

	// -------- constructors --------

	/**
	 * Create a new shop service.
	 * 
	 * @param comp
	 *            The active component.
	 */
	public RemoteSimulationExecutionService(ICapability comp, String name) {
		super(comp.getServiceProvider().getId(), IRemoteSimulationExecutionService.class, null);

		// System.out.println("created: "+name);
		this.comp = comp;
		this.name = name;
	}

	// -------- methods --------

	/**
	 * Get the name of the platform.
	 * 
	 * @return The name.
	 */
	public String getPlatformName() {
		return name;
	}

	/**
	 * Simulate an experiment defined as application.xml
	 * 
	 * @param item
	 *            The item.
	 */
	public IFuture executeExperiment(String appName, String applicationDescription, String configName, Map args) {
		System.out.println("Called Remote Service.");
		final Future ret = new Future();

		try {
			// persist application description
			final String fileName = System.getProperty("user.dir") + "\\ApplicationDescription.application.xml";
			FileHandler.writeToFile(fileName, applicationDescription);
			((Map) (args.get(Constants.SIMULATION_FACTS_FOR_CLIENT))).put(Constants.FILE_PATH, fileName);
			
			// init agent and store required execution information for client agent			
			comp.getBeliefbase().getBelief("simulationFacts").setFact(args.get(Constants.SIMULATION_FACTS_FOR_CLIENT));			
			

			// start simulation execution
			IGoal[] goals = (IGoal[]) comp.getGoalbase().getGoals("startExecution");
			if (goals.length > 0) {
				ret.setException(new IllegalStateException("Can only handle one observation at a time."));
			} else {
				final IGoal oe = (IGoal) comp.getGoalbase().createGoal("startExecution");
				oe.getParameter("appName").setValue(appName);
				oe.getParameter("args").setValue(args);
				oe.addGoalListener(new IGoalListener() {
					public void goalFinished(AgentEvent ae) {
						System.out.println("observation finished at: " + comp.getAgentName());
						FileHandler.deleteFile(fileName);
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
			System.out.println("Could not start application...." + e);
		}
		return ret;
	}

	/**
	 * Get the string representation.
	 * 
	 * @return The string representation.
	 */
	public String toString() {
		return name;
	}
}
