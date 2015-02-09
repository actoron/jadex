package jadex.extension.envsupport;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.IEnvironmentSpace;

/**
 *  Accessor service for old envsupport.
 */
public interface IEnvironmentService
{
	/**
	 *	Get the environment space. 
	 */
	public @Reference(remote=false) IFuture<IEnvironmentSpace>	getSpace();
}
