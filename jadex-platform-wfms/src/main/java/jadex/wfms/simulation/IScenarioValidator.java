package jadex.wfms.simulation;

import java.util.List;

/**
 */
public interface IScenarioValidator
{
	
	/**
	 *  Generates a report based on the information gathered at runtime for a single scenario.
	 *  
	 *  @param runtimeData Data gathered from the simulation runs, including input information.
	 *  @return A report based on the simulation data.
	 */
	public Report generateReport(List runtimeData);
}
