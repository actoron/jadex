package jadex.bridge;

import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;
import jadex.commons.service.IServiceProvider;

/**
 *  The interface for accessing components from the outside.
 *  To be specialized for concrete component types.
 */
public interface IExternalAccess extends IRemotable
{
	//-------- cache --------
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel();

	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IComponentIdentifier getParent();
	
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
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleStep(IComponentStep step);
	
	//-------- normal --------
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren();
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent();
	
	//-------- exclude --------
	
	// todo: do we want this? should getArg() deliver only args supplied from
	// outside or also values that are default/initial values in the model.
	// problem: this would require to store the arguments for the whole lifetime of the component.
	/**
	 *  Get argument value.
	 *  @param name The argument name.
	 *  @return The argument value.
	 * /
	public Object getArgumentValue(String name);*/
}
