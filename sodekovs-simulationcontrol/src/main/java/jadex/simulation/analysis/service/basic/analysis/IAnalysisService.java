package jadex.simulation.analysis.service.basic.analysis;

import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.events.service.IAServiceObservable;

import java.util.Set;

public interface IAnalysisService extends IInternalService, IAServiceObservable
{
	/**
	 * Return the workload of the service
	 * @return Workload as a Double between 0 and 100
	 */
	public IFuture getWorkload();
	
	/**
	 * Synchronize Object of the {@link IADataObject}
	 * @return mutex of the dataObject
	 */
	public Object getMutex();
	
	public IFuture getView();
	
	/**
	 * Returns the modes this service support
	 * @return modes as a StringSet
	 */
//	public Set<String> getSupportedModes();
	
}
