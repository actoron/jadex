package jadex.android.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;

/**
 * Provides access to the Platform service.
 */
public abstract class JadexPlatformBinder extends JadexEventBinder implements IJadexPlatformBinder
{
	//-------- attributes --------
	
	/** The Jadex Context Holder */
	private JadexPlatformManager jadexAndroidContext;

	/**
	 * Constructor
	 * @param context
	 */
	public JadexPlatformBinder(JadexPlatformManager context)
	{
		this.jadexAndroidContext = context;
	}
	
	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformID)
	{
		return jadexAndroidContext.getExternalPlatformAccess(platformID);
	}

	public boolean isPlatformRunning(IComponentIdentifier platformID)
	{
		return jadexAndroidContext.isPlatformRunning(platformID);
	}

	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformID)
	{
		return jadexAndroidContext.getCMS(platformID);
	}
	
	public void shutdownJadexPlatforms()
	{
		jadexAndroidContext.shutdownJadexPlatforms();
	}

	public void shutdownJadexPlatform(IComponentIdentifier platformID)
	{
		jadexAndroidContext.shutdownJadexPlatform(platformID);
	}
	
}
