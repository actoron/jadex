package jadex.simulation.analysis.common.services;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.commons.service.IService;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.IAServiceObservable;

public interface IAnalysisService extends IService, IAServiceObservable
{
	/**
	 * Return the workload of the service
	 * @return Workload as a {@link AWorkload}
	 */
	public IFuture getWorkload();
	
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
	
}
