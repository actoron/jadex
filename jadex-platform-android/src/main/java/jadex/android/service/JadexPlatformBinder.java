package jadex.android.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;
import jadex.commons.future.IFuture;

/**
 * Provides access to the Platform service.
 */
public abstract class JadexPlatformBinder extends JadexEventBinder implements IJadexPlatformBinder
{
	//-------- attributes --------
	
	/** The Jadex Context Holder */
	private JadexPlatformManager manager;

	/**
	 * Constructor
	 * @param context
	 */
	public JadexPlatformBinder(JadexPlatformManager context)
	{
		this.manager = context;
	}
	
	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformID)
	{
		return manager.getExternalPlatformAccess(platformID);
	}

	public boolean isPlatformRunning(IComponentIdentifier platformID)
	{
		return manager.isPlatformRunning(platformID);
	}

	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformID)
	{
		return manager.getCMS(platformID);
	}
	
	@Override
	public IFuture<IMessageService> getMS(IComponentIdentifier platformID)
	{
		return manager.getMS(platformID);
	}
	
	public void shutdownJadexPlatforms()
	{
		manager.shutdownJadexPlatforms();
	}

	public void shutdownJadexPlatform(IComponentIdentifier platformID)
	{
		manager.shutdownJadexPlatform(platformID);
	}
	
}
