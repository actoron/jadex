package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.IEventReceiver;
import jadex.android.exception.WrongEventClassException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.commons.future.IFuture;
import android.os.Binder;

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
	
	public IFuture<IExternalAccess> startJadexPlatform()
	{
		return jadexAndroidContext.startJadexPlatform();
	}

	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels)
	{
		return jadexAndroidContext.startJadexPlatform(kernels);
	}

	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId)
	{
		return jadexAndroidContext.startJadexPlatform(kernels, platformId);
	}

	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId, String options)
	{
		return jadexAndroidContext.startJadexPlatform(kernels, platformId, options);
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
