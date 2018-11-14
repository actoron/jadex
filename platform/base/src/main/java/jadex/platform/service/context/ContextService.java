package jadex.platform.service.context;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.context.IPreferences;
import jadex.commons.SNonAndroid;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * Java SE Implementation of {@link IContextService}
 */
public class ContextService extends BasicService implements jadex.bridge.service.types.context.IContextService
{
	/**
	 *  Create a new ContextService.
	 */
	public ContextService(IComponentIdentifier provider)
	{
		super(provider, IContextService.class, null);
	}

	// -------- methods ----------

//	/**
//	 * Returns a File
//	 * @param name File name
//	 * @return {@link File}
//	 */
//	public File getFile(String name)
//	{
//		return new File(name);
//	}
//
//	/**
//	 * Gets a Shared Preference Container. Returns null on Desktop Systems.
//	 * @param preferenceFileName
//	 */
//	public IPreferences getSharedPreferences(String preferenceFileName)
//	{
//		return null;
//	}
//
//	/**
//	 * Dispatches an Event to the Android UI / Activity. Does nothing on Desktop
//	 * Systems.
//	 */
//	public boolean dispatchUiEvent(IJadexAndroidEvent event)
//	{
//		return false;
//	}
//
//	/**
//	 * Get the network ips.
//	 */
//	public List<InetAddress> getNetworkIps()
//	{
//		// The indirection is needed because this Class is loaded even if it's not instantiated on Android.
//		return SNonAndroid.getNetworkIps();
//	}
//
//	/**
//	 * Opens a File with the default application.
//	 * @param path
//	 * @throws IOException 
//	 */
//	public void openFile(String path) throws IOException
//	{
//		// The indirection is needed because this Class is loaded even if it's not instantiated on Android.
//		SNonAndroid.openFile(path);
//	}
	
	/**
	 * Returns a File
	 * @param name File name
	 * @return {@link File}
	 */
	public IFuture<File> getFile(String name)
	{
		return new Future<File>(new File(name));
	}

	/**
	 * Gets a Shared Preference Container. Returns null on Desktop Systems.
	 * @param preferenceFileName
	 */
	public IFuture<IPreferences> getSharedPreferences(String preferenceFileName)
	{
		return new Future<IPreferences>((IPreferences)null);
	}

	/**
	 * Dispatches an Event to the Android UI / Activity. Does nothing on Desktop
	 * Systems.
	 */
	public IFuture<Boolean> dispatchEvent(IJadexAndroidEvent event)
	{
		return new Future<Boolean>(Boolean.FALSE);
	}

	/**
	 * Get the network ips.
	 */
	public IFuture<List<InetAddress>> getNetworkIps()
	{
		// The indirection is needed because this Class is loaded even if it's not instantiated on Android.
		return new Future<List<InetAddress>>(SNonAndroid.getNetworkIps());
	}

	/**
	 * Opens a File with the default application.
	 * @param path
	 * @throws IOException 
	 */
	public IFuture<Void> openFile(String path) throws IOException
	{
		// The indirection is needed because this Class is loaded even if it's not instantiated on Android.
		SNonAndroid.openFile(path);
		return IFuture.DONE;
	}
	
	public IFuture<Void> shutdownService()
	{
		return super.shutdownService();
	}
}
