package jadex.bridge;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.IValueFetcher;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.util.Map;
import java.util.logging.Logger;

/**
 *  Common interface for all component types. Is used when
 *  scheduleStep() is called and the executing thread is the
 *  component thread.
 */
public interface IInternalAccess
{
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel();

	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration();
	
	/**
	 *  Get the parent access (if any).
	 *  @return The parent access.
	 */
	public IExternalAccess getParentAccess();
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier();
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	public IComponentDescription	getComponentDescription();
	
	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IServiceContainer getServiceContainer();
	
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
	public IExternalAccess getExternalAccess();
	
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
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public IFuture<Void> addComponentListener(IComponentListener listener);
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture<Void> removeComponentListener(IComponentListener listener);
		
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
	
	// todo: generic interface does not match MicroAgent implementation (returns IFuture<TimerWrapper>).
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step);
	
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
}
