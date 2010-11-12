package jadex.bridge;

import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.IServiceProvider;

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
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess();
}
