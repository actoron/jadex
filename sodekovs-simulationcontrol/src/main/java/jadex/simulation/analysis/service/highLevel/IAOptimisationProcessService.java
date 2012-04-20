package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to perform a optimization
 * 
 * @author 5Haubeck
 */
public interface IAOptimisationProcessService extends IAnalysisSessionService
{
	/**
	 * Perform a optimization
	 * 
	 * @param session
	 *            if any, already opened session
	 * @return Object, if any, result of execution
	 */
	public IFuture optimize(String session);
}
