package jadex.bridge;

import java.util.Map;

import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The interface for accessing components from the outside.
 *  
 *  Methods may be called from any thread, but the result listener will not be scheduled back to the calling thread!
 */
@Reference
public interface IExternalAccess //extends INFPropertyProvider//extends IRemotable
{
	//-------- cache --------
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel();

//	/**
//	 *  Get the parent (if any).
//	 *  @return The parent.
//	 */
//	public IComponentIdentifier getParent();
	
//	/**
//	 *  Get the parent access (if any).
//	 *  @return The parent access.
//	 */
//	public IExternalAccess getParentAccess();
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier();
	
//	/**
//	 *  Get the id of the component including addresses.
//	 *  @return	The component id.
//	 */
//	public IFuture<ITransportComponentIdentifier> getTransportComponentIdentifier();
	
	/**
	 *  @deprecated From version 3.0 - replaced with external access.
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IExternalAccess getServiceProvider();
	
	/**
	 *  @deprecated From version 3.0 - replaced with external access.
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IExternalAccess getServiceContainer();
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @return The result of the step.
	 */
	public <T>	IFuture<T> scheduleStep(IComponentStep<T> step);
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @return The result of the step.
	 */
	public <T>	IFuture<T> scheduleStep(int priority, IComponentStep<T> step);
	
//	/**
//	 *  Execute some code on the component's thread.
//	 *  Unlike scheduleStep(), the action will also be executed
//	 *  while the component is suspended.
//	 *  @param action	Code to be executed on the component's thread.
//	 *  @return The result of the step.
//	 */
//	public <T>	IFuture<T> scheduleImmediate(IComponentStep<T> step);
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime);

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step);
	
	/**
	 *  Test if current thread is an external thread.
	 *  @return True if the current thread is not the component thread.
	 */
	public boolean isExternalThread();

	//-------- normal --------
		
	/**
	 *  Create a subcomponent.
	 *  @param component The instance info.
	 */
	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component);
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent();
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(Exception e);
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(String type);
	
	/**
	 *  Get the model name of a component type.
	 *  @param ctype The component type.
	 *  @return The model name of this component type.
	 */
	public IFuture<String> getFileName(String ctype);
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType();

	/**
	 *  Subscribe to component events.
	 *  @param filter An optional filter.
	 *  @param initial True, for receiving the current state.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel elm);
	
	/**
	 *  Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults();
	
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public IFuture<Map<String, Object>> getResults();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IFuture<Map<String, Object>> getArguments();
	
	//-------- exclude --------
	
//	/**
//	 *  Get a space of the application.
//	 *  @param name	The name of the space.
//	 *  @return	The space.
//	 */
//	public IFuture<IExtensionInstance> getExtension(final String name);
	
	// todo: do we want this? should getArg() deliver only args supplied from
	// outside or also values that are default/initial values in the model.
	// problem: this would require to store the arguments for the whole lifetime of the component.
	/**
	 *  Get argument value.
	 *  @param name The argument name.
	 *  @return The argument value.
	 * /
	public Object getArgumentValue(String name);*/
	
//	/**
//	 *  Test if current thread is external thread.
//	 *  @return True if the current thread is not the component thread.
//	 */
//	public IFuture<Boolean> isExternalThread();
//	
//	/**
//	 *  Check if the component is directly available.
//	 *  An external access becomes invalid, when a component
//	 *  is persisted or terminated.
//	 */
//	public boolean	isValid();
	
//	/**
//	 *  Get an external interface feature.
//	 *  @param type The interface type of the feature.
//	 *  @return The feature.
//	 */
//	public <T> T getExternalComponentFeature(Class<T> type);

}
