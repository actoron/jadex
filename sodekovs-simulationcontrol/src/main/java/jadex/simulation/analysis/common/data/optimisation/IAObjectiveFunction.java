package jadex.simulation.analysis.common.data.optimisation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;

/**
 * Objective function for simulation
 * @author 5Haubeck
 *
 */
public interface IAObjectiveFunction
{
	/**
	 * Evaluate the Ensemble and return objective value
	 * @return Double objective value
	 */
	public IFuture evaluate(IAParameterEnsemble ensemble);
	
	/**
	 * Returns the goal of the objective function. Minimize = false
	 * @return Boolean, Goal of the objective
	 */
	public Boolean MinGoal();
}
