package jadex.platform.service.processengine;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  This monitoring service can be used to start bpmn processes
 *  according to its defined start events (currently time, rule).
 */
@Service
public interface IProcessEngineService
{
	/**
	 *  Add a bpmn model that is monitored for start events.
	 *  @param model The bpmn model
	 *  @param rid The resource identifier (null for all platform jar resources).
	 *  @param cortype The correlation factory.
	 */
	public ISubscriptionIntermediateFuture<ProcessEngineEvent> addBpmnModel(@CheckNotNull String model, IResourceIdentifier rid);
	
	/**
	 *  Remove a bpmn model.
	 *  @param model The bpmn model
	 *  @param rid The resource identifier (null for all platform jar resources).
	 */
	public IFuture<Void> removeBpmnModel(@CheckNotNull String model, IResourceIdentifier urid);

	/**
	 *  Get the currently monitored processes.
	 *  @return The currently observed bpmn models.
	 */
	public IIntermediateFuture<Tuple2<String, IResourceIdentifier>> getBpmnModels();
	
	/**
	 *  Process an event.
	 */
//	public ISubscriptionIntermediateFuture<ProcessEngineEvent> processEvent(IEvent event);
//	public ISubscriptionIntermediateFuture<ProcessEngineEvent> processEvent(Object event, String type);
	public IFuture<Void> processEvent(Object event, String type);
	
//	/**
//	 *  Subscribe to events of the monitoring starter.
//	 */
//	public IIntermediateFuture<MonitoringStarterEvent> subscribe();
}
