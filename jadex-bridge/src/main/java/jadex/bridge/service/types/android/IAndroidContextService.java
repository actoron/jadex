package jadex.bridge.service.types.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Provides Access to the Android Application Context and 
 * Android Resources such as Files and Properties 
 * @author Julian Kalinowski
 */
public interface IAndroidContextService {

	/**
	 * Opens a File OutputStream and returns it
	 * @param name Filename
	 * @return {@link FileOutputStream}
	 * @throws FileNotFoundException
	 */
	public FileOutputStream openFileOutputStream(String name)
			throws FileNotFoundException;

	/**
	 * Opens a File InputStream and returns it
	 * @param name Filename
	 * @return {@link FileInputStream}
	 * @throws FileNotFoundException
	 */
	public FileInputStream openFileInputStream(String name)
			throws FileNotFoundException;
	
	/**
	 * Returns a File
	 * @param name File name
	 * @return {@link File}
	 */
	public File getFile(String name);
	
	/**
	 * Gets an Android Shared Preference Container.
	 * @param preferenceFileName
	 */
	public IPreferences getSharedPreferences(String preferenceFileName);

}