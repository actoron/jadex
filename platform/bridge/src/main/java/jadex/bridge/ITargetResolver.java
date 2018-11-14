package jadex.bridge;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.IFuture;

/**
 *  The target resolver is used to determine dynamically a new service
 *  target by an intelligent proxy. 
 */
public interface ITargetResolver 
{
	/** The target resolver class (to dynamically resolve the called service). */
	public static final String TARGETRESOLVER = "targetresolver";
	
	/**
	 *  Determine the target of a call.
	 *  @param sid The service identifier of the original call.
	 *  @param agent The external access.
	 *  @return The new service that should be called instead of the original one.
	 */
	public IFuture<IService> determineTarget(IServiceIdentifier sid, IExternalAccess agent, IServiceIdentifier broken);
}
