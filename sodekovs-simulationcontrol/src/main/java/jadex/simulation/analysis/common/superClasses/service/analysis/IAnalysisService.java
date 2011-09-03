package jadex.simulation.analysis.common.superClasses.service.analysis;

import jadex.bridge.service.IInternalService;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;

public interface IAnalysisService extends IInternalService, IAObservable
{
	/**
	 * Return the workload of the service
	 * @return Workload as a Double between 0 and 100
	 */
	public IFuture getWorkload();
	

	/**
	 * Return the serviceView
	 * @return Serviceview as {@link IFuture}
	 */
	public IFuture getView();	
}
