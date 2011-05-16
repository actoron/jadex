package jadex.simulation.analysis.common.services;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

import java.util.UUID;

public interface IAnalysisSessionService extends IAnalysisService
{

	/**
	 * Create a session for this service with the given configuration (can be null)
	 * If a configuration parameter is not provided, the default value is used.
	 * @param configuration Configuration to use as {@link IAParameterEnsemble}.
	 * @return  id as a {@link UUID} of the session
	 */
	public abstract IFuture createSession(IAParameterEnsemble configuration);

	/**
	 * Close a Session
	 * @param id the id of the session
	 */
	public abstract void closeSession(UUID id);
	
	public abstract IFuture getSessionView(UUID id);

	public abstract IFuture getSessions();

}