package jadex.simulation.analysis.buildingBlocks.simulation;

import jadex.commons.future.IFuture;
import jadex.bridge.service.IService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.services.IAnalysisService;

import java.util.Set;

import javax.swing.JFrame;

/**
 *  The simulation execution interface for executing (single) experiments.
 */
public interface IExecuteExperimentService extends IAnalysisService
{

	/**
	 *  Execute a experiment
	 *  @param exp {@link IAExperiment}
	 */
	public IFuture executeExperiment(IAExperiment exp);
	
	public IFuture getView();

	public IFuture getView(JFrame frame);
}
