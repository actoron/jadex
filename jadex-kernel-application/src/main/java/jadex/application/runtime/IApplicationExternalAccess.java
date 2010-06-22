package jadex.application.runtime;

import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;


/**
 *  External access interface for applications.
 */
public interface IApplicationExternalAccess	extends IExternalAccess
{
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IFuture getSpace(String name);
}
