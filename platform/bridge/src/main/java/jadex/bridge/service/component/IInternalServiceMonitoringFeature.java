package jadex.bridge.service.component;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for monitoring provided and required services
 */
public interface IInternalServiceMonitoringFeature
{
	/**
	 *  Get the required service info for a name.
	 *  @param name	The required service name.
	 */
	// Hack!!! used by multi invoker?
	public RequiredServiceInfo	getServiceInfo(String name);

	//-------- all declared services (e.g. JCC component details) --------
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getRequiredServiceInfos();
	
	//-------- listening to events (e.g. ComAnalyzer) --------
	
	/**
	 *  Listen to service call events (call, result and commands).
	 */
	public ISubscriptionIntermediateFuture<ServiceCallEvent>	getServiceEvents();

	//-------- posting events (e.g. Interceptors) --------
	
	/**
	 *  Post a service call event.
	 */
	public void	postServiceEvent(ServiceCallEvent event);

	/**
	 *  Check if there is someone monitoring.
	 *  To Avoid posting when nobody is listening.
	 */
	public boolean isMonitoring();
}
