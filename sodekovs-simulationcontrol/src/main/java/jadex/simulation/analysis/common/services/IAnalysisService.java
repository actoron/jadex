package jadex.simulation.analysis.common.services;

import jadex.commons.future.IFuture;
import jadex.bridge.service.IService;
import jadex.simulation.analysis.common.events.service.IAServiceObservable;

import java.util.Set;

public interface IAnalysisService extends IService, IAServiceObservable
{
	/**
	 * Return the workload of the service
	 * @return Workload as a Double between 0 and 100
	 */
	public IFuture getWorkload();
	
	/**
	 * Returns the modes this service support
	 * @return modes as a StringSet
	 */
	public Set<String> getSupportedModes();
	
}
