package jadex.bridge;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITargetResolver 
{
	/** The target resolver class (to dynamically resolve the called service). */
	public static final String TARGETRESOLVER = "targetresolver";
	
	/**
	 * 
	 */
	public IFuture<IService> determineTarget(IServiceIdentifier sid, IExternalAccess agent);
}
