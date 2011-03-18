package jadex.bridge;

import jadex.bridge.service.IServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

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
	public IServiceProvider getServiceProvider();
	
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
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name);
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(String name);

	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind);
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(String name, boolean rebind);

	
	/**
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public void addComponentListener(IComponentListener listener);
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public void removeComponentListener(IComponentListener listener);
}
