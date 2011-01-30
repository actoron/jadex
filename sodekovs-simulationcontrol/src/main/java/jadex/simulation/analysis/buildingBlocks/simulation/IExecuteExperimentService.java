package jadex.simulation.analysis.buildingBlocks.simulation;

import java.util.Set;

import javax.swing.JFrame;

import jadex.commons.IFuture;
import jadex.commons.service.IService;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;

/**
 *  The simulation execution interface for executing (single) experiments.
 */
public interface IExecuteExperimentService	extends IService
{

	/**
	 *  Execute a experiment
	 *  @param expJob {@link IAExperimentJob}
	 */
	public IFuture executeExperiment(IAExperimentJob expJob);
	
	/**
	 * Return the model which the service support
	 * @return modeltypes as Set
	 */
	public Set<String> supportedModels();
	
	public IFuture getView();

	public IFuture getView(JFrame frame);
}
