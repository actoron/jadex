package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to perform a execution phase of the general analysis
 * 
 * @author 5Haubeck
 */
public interface IAGeneralExecuteService extends IAnalysisSessionService
{
	/**
	 * Simulate/Evaluate given experiments
	 * 
	 * @param session
	 *            if any, already opened session
	 * @param experiments
	 *            experiments to evaluate
	 * @return IAExperimentBatch, executed results
	 */
	public IFuture execute(String session, IAExperimentBatch experiments);
}
