package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to perform a general analysis
 * 
 * @author 5Haubeck
 */
public interface IAGeneralAnalysisProcessService extends IAnalysisSessionService
{
	/**
	 * Perform a general analysis
	 * 
	 * @param session
	 *            if any, already opened session
	 * @return Object, if any, result of execution
	 */
	public IFuture analyse(String session);

}
