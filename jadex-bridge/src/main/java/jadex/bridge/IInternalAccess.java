package jadex.bridge;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
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
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IExternalAccess getParent();
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren();
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier();
	
	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IServiceContainer getServiceContainer();
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent();
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public IResultListener createResultListener(IResultListener listener);
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener);
	
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
	public IFuture addComponentListener(IComponentListener listener);
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture removeComponentListener(IComponentListener listener);
		
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments();
	
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public Map getResults();
	
//	/**
//	 *  Get the model name of a component type.
//	 *  @param ctype The component type.
//	 *  @return The model name of this component type.
//	 */
//	public IFuture getFileName(String ctype);
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public IFuture waitFor(long delay, IComponentStep step);
	
	// todo:?
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public IFuture waitForImmediate(long delay, IComponentStep step);
}
