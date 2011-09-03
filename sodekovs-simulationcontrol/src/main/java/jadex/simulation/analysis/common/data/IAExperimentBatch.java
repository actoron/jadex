package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.allocation.IAllocationStrategy;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

import java.util.Map;

/**
 * A Set of Experiments
 * @author 5Haubeck
 *
 */
public interface IAExperimentBatch extends IADataObject
{
	/**
	 * Gets all experiments
	 * @return mapping between names and experiment
	 */
	public Map<String, IAExperiment> getExperiments();

	/**
	 * Get the experiment
	 * @param experiment the experiment name
	 * @return the experiment
	 */
	public IAExperiment getExperiment(String name);

	/**
	 * Add a experiment
	 * @param experiment the experiment to add
	 */
	public void addExperiment(IAExperiment experiment);

	/**
	 * Remove a experiment
	 * @param experiment the experiment to remove
	 */
	public void removeExperiment(IAExperiment experiment);

	/**
	 * Test for evaluating. true if all experiments are allocated
	 * @return true, if evaluated
	 */
	public Boolean isEvaluated();
	
	/**
	 * Return the strategy of the allocations
	 * @return the strategy to allocate
	 */
	public IAllocationStrategy getAllocationStrategy();
	
	/**
	 * Sets the strategy of the allocations
	 * @param strategy the strategy for allocation
	 */
	public void setAllocationStrategy(IAllocationStrategy strategy);

	/**
	 * Set the allocations of the experiments
	 * @param allocations mapping between experiments and Services
	 */
	public void setAllocation(Map<IAExperiment, IAnalysisService> allocations);

	/**
	 * Returns the allocation for given experiment
	 * @param exp the experiment
	 * @return the service to use
	 */
	public IAnalysisService getAllocation(IAExperiment exp);

	/**
	 * Returns all allocations
	 * @return mapping between experiments and Services
	 */
	public Map<IAExperiment, IAnalysisService> getAllocations();
}
