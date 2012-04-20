package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to plan a general analysis
 * 
 * @author 5Haubeck
 */
public interface IAGeneralPlanningService extends IAnalysisSessionService
{
	/**
	 * Plan a general execution
	 * 
	 * @param session
	 *            if any, already opened session
	 * @return IAExperimentBatcj, experiments to execute
	 */
	public IFuture plan(String session);
}
