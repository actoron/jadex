package jadex.bridge.service.types.context;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 *  Provides access to the application Context specific objects and Android
 *  Resources such as files and preferences.
 */
public interface IContextService
{
//	/**
//	 * Returns a File
//	 * @param name File name
//	 * @return {@link File}
//	 */
//	public File getFile(String name);
//	
//	/**
//	 * Opens a File with the default application.
//	 * @param path
//	 * @throws IOException 
//	 */
//	public void openFile(String path) throws IOException;
//
//	/**
//	 * Gets an Android Shared Preference Container.
//	 * Returns null on Desktop Systems.
//	 * @param preferenceFileName
//	 */
//	public IPreferences getSharedPreferences(String preferenceFileName);
//
//	/**
//	 * Dispatches an Event to the Android UI / Activity.
//	 * Does nothing on Desktop Systems.
//	 * 
//	 * @param event {@link IJadexAndroidEvent}
//	 * @return true, if at least one receiver was registered for this event and delivery was successful, else false.
//	 */
//	// todo: IJadexAndroidEvent?! generic?
//	public boolean dispatchUiEvent(IJadexAndroidEvent event);
//
//	/**
//	 * Get the network ips.
//	 */
//	public List<InetAddress> getNetworkIps();
	
	/**
	 * Returns a File
	 * @param name File name
	 * @return {@link File}
	 */
	@Excluded
	public @Reference(local=true) IFuture<File> getFile(String name);
	
	/**
	 * Opens a File with the default application.
	 * @param path
	 * @throws IOException 
	 */
	public IFuture<Void> openFile(String path) throws IOException;

	/**
	 * Gets an Android Shared Preference Container.
	 * Returns null on Desktop Systems.
	 * @param preferenceFileName
	 */
	public IFuture<IPreferences> getSharedPreferences(String preferenceFileName);

	/**
	 * Dispatches an Event to the Android UI / Activity.
	 * Does nothing on Desktop Systems.
	 * 
	 * @param event {@link IJadexAndroidEvent}
	 * @return true, if at least one receiver was registered for this event and delivery was successful, else false.
	 */
	public IFuture<Boolean> dispatchEvent(IJadexAndroidEvent event);

	/**
	 * Get the network ips.
	 */
	public IFuture<List<InetAddress>> getNetworkIps();
}