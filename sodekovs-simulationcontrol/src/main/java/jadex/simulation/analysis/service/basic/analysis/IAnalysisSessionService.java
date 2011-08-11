package jadex.simulation.analysis.service.basic.analysis;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.view.session.IASessionView;

import java.util.UUID;

public interface IAnalysisSessionService extends IAnalysisService
{

	/**
	 * Create a session for this service with the given configuration (can be null)
	 * If a configuration parameter is not provided, the default value is used.
	 * @param configuration Configuration to use as {@link IAParameterEnsemble}.
	 * @return  id as a {@link UUID} of the session
	 */
	public IFuture createSession(IAParameterEnsemble configuration);

	/**
	 * Close a Session
	 * @param id the id of the session
	 */
	public void closeSession(UUID id);
	
	/**
	 * Get the View of the given Session
	 * @param id the id of the session
	 * @return 
	 */
	public IFuture getSessionView(UUID id);

	/**
	 * Get the Session
	 * @return Set<UUID>, ids of the session
	 */
	public IFuture getSessions();
	
	public IFuture getSessionConfiguration(UUID id);

}