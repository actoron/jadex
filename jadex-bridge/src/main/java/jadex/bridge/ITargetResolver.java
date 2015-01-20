package jadex.bridge;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITargetResolver 
{
	/**
	 * 
	 */
	public IFuture<IService> determineTarget(IServiceIdentifier sid, IExternalAccess agent);
}
