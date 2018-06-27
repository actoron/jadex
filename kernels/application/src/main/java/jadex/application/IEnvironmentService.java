package jadex.application;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 *  Accessor service for old extensions (spaces).
 */
public interface IEnvironmentService
{
	/**
	 *	Get a space. 
	 */
	public @Reference(remote=false) IFuture<Object>	getSpace(String name);
}
