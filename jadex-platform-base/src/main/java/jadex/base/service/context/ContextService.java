package jadex.base.service.context;

import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.context.IPreferences;
import jadex.commons.SNonAndroid;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * Java SE Implementation of {@link IContextService}
 */
public class ContextService extends BasicService implements jadex.bridge.service.types.context.IContextService
{

	public ContextService(IServiceProvider provider)
	{
		super(provider.getId(), IContextService.class, null);
	}

	// -------- methods ----------

	/**
	 * Returns a File
	 * 
	 * @param name
	 *            File name
	 * @return {@link File}
	 */
	public File getFile(String name)
	{
		return new File(name);
	}

	/**
	 * Gets a Shared Preference Container. Returns null on Desktop Systems.
	 * 
	 * @param preferenceFileName
	 */
	public IPreferences getSharedPreferences(String preferenceFileName)
	{
		return null;
	}

	/**
	 * Dispatches an Event to the Android UI / Activity. Does nothing on Desktop
	 * Systems.
	 */
	public boolean dispatchUiEvent(IJadexAndroidEvent event)
	{
		return false;
	}

	/**
	 * Get the network ips.
	 */
	public List<InetAddress> getNetworkIps()
	{
		// The indirection is needed because this Class is loaded even if it's not instanciated on Android.
		return SNonAndroid.getNetworkIps();
	}

	/**
	 * Opens a File with the default application.
	 * @param path
	 * @throws IOException 
	 */
	public void openFile(String path) throws IOException
	{
		// The indirection is needed because this Class is loaded even if it's not instanciated on Android.
		SNonAndroid.openFile(path);
	}
}
