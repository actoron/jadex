package jadex.simulation.analysis.buildingBlocks.simulation;

import jadex.commons.IFuture;
import jadex.commons.service.IService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;

import java.util.Set;

import javax.swing.JFrame;

/**
 *  The simulation execution interface for executing (single) experiments.
 */
public interface IExecuteExperimentService	extends IService
{

	/**
	 *  Execute a experiment
	 *  @param exp {@link IAExperiment}
	 */
	public IFuture executeExperiment(IAExperiment exp);
	
	/**
	 * Return the model which the service support
	 * @return modeltypes as Set
	 */
	public Set<String> supportedModels();
	
	public IFuture getView();

	public IFuture getView(JFrame frame);
}
