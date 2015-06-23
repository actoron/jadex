package jadex.bridge.service.types.platform;

import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.IFuture;


/**
 * Interface for the platform binder object.
 */
public interface IJadexPlatformBinder extends IJadexMultiPlatformBinder, IJadexPlatformInterface
{
	/**
	 * Retrieves the CMS of the Platform with the given ID.
	 * 
 	 * @deprecated use getService() or getsService() instead.
	 */
	public IFuture<IComponentManagementService> getCMS();
	
	/**
	 * Retrieves the MS of the Platform with the given ID.
	 * 
	 * @deprecated use getService() or getsService() instead.
	 */
	public IFuture<IMessageService> getMS();
}
