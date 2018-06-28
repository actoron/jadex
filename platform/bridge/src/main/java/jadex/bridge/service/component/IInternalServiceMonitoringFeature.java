package jadex.bridge.service.component;

import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for monitoring provided and required services
 */
public interface IInternalServiceMonitoringFeature
{
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
