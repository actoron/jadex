package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to perform a validation
 * 
 * @author 5Haubeck
 */
public interface IAValidationProcessService extends IAnalysisSessionService
{
	/**
	 * Perform a validation
	 * 
	 * @param session
	 *            if any, already opened session
	 * @return Object, if any, result of execution
	 */
	public IFuture validate(String session);
}
