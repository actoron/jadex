package jadex.platform.service.cms;

import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInterpreter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.ComponentCreationInfo;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.kernelbase.ExternalAccess;

/**
 *  Standalone platform component implementation.
 */
public class PlatformComponent implements IPlatformComponentAccess, IInternalAccess
{
	//-------- attributes --------
	
	/** The creation info. */
	protected ComponentCreationInfo	info;
	
	/** The service container. */
	protected ComponentServiceContainer	sc;
	
	
	//-------- IPlatformComponentAccess interface --------
	
	/**
	 *  Perform the initialization of the component.
	 *  
	 *  @param info The component creation info.
	 *  @param interpreter The kernel specific component part.
	 *  @return A future to indicate when the initialization is done.
	 */
	public IFuture<Void>	init(ComponentCreationInfo info, IComponentInterpreter interpreter)
	{
		this.info	= info;
		this.sc	= new ComponentServiceContainer(null, this, false);	// Todo
		return IFuture.DONE;
	}
	
	/**
	 *  Get the user view of this platform component.
	 *  
	 *  @return An internal access exposing user operations of the component.
	 */
	public IInternalAccess	getInternalAccess()
	{
		return this;
	}
	
	/**
	 *  Execute a step of the component.
	 *  Used for platform bootstrapping, until execution service is running.
	 *  
	 *  @return true, if component wants to be executed again. 
	 */
	public boolean executeStep()
	{
		return false;
	}
	
	//-------- IInternalAccess interface --------
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		return info.getModel();
	}

	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration()
	{
		return info.getConfiguration();
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return info.getComponentIdentifier();
	}
	
	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IServiceContainer getServiceContainer()
	{
		return sc;
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent();
	
//	/**
//	 *  Test if component has been killed.
//	 *  @return True, if has been killed.
//	 */
//	public boolean isKilled();
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener);
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener);
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		// Todo: shadow access and invalidation
		return new ExternalAccess(this);
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger();
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	public IValueFetcher getFetcher();
		
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments();
	
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public Map<String, Object> getResults();
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value);
	
	/**
	 *  Get the class loader of the component.
	 */
	public ClassLoader	getClassLoader();
	
//	/**
//	 *  Get the model name of a component type.
//	 *  @param ctype The component type.
//	 *  @return The model name of this component type.
//	 */
//	public IFuture getFileName(String ctype);
	
	/**
	 *  Execute a component step.
	 */
	public <T>	IFuture<T> scheduleStep(IComponentStep<T> step);
	
	/**
	 *  Execute an immediate component step,
	 *  i.e., the step is executed also when the component is currently suspended.
	 */
	public <T>	IFuture<T> scheduleImmediate(IComponentStep<T> step);
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime);

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step);

	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay, boolean realtime);
	
	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay);
	
	// todo:?
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public IFuture waitForImmediate(long delay, IComponentStep step);

	/**
	 *  Test if current thread is the component thread.
	 *  @return True if the current thread is the component thread.
	 */
	public boolean isComponentThread();
	
	/**
	 *  Subscribe to component events.
	 *  @param filter An optional filter.
	 *  @param initial True, for receiving the current state.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel elm);

	/**
	 *  Publish a monitoring event. This event is automatically send
	 *  to the monitoring service of the platform (if any). 
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event, PublishTarget pt);
	
	/**
	 *  Check if event targets exist.
	 */
	public boolean hasEventTargets(PublishTarget pt, PublishEventLevel pi);
}
