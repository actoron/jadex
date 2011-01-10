package jadex.component.runtime;

import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;


/**
 *  External access interface for applications.
 */
public interface IComponentExternalAccess	extends IExternalAccess
{
	// todo: move to IExternalAccess
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren(String type);
	
	/**
	 *  Get the model name of a component type.
	 *  @param ctype The component type.
	 *  @return The model name of this component type.
	 */
	public IFuture getFileName(String ctype);
	
	// toto: use futrue
//	/**
//	 *  Get a space of the application.
//	 *  @param name	The name of the space.
//	 *  @return	The space.
//	 */
//	public ISpace getSpace(String name);
}
