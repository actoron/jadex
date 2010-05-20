package jadex.bridge;

import jadex.service.IServiceProvider;

/**
 *  The interface for accessing components from the outside.
 *  To be specialized for concrete component types.
 */
public interface IExternalAccess extends IServiceProvider
{
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public ILoadableComponentModel	getModel();

	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier();
	
	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IExternalAccess getParent();
	
//	/**
//	 *  Get service.
//	 *  @return The service implementation.
//	 */
//	public Object getService(Class type);
	
	/**
	 *  Get service interfaces.
	 *  @return Array of provided services.
	 * /
	public Class[] getServiceInterfaces();*/
	
	// todo:?!
//	/**
//	 *  Get the children (if any).
//	 *  @return The children.
//	 */
//	public IComponentIdentifier[] getChildren();
	
}
