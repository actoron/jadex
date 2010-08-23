package jadex.commons.service.library;

import jadex.commons.IFuture;
import jadex.commons.service.IService;

import java.net.URL;

/** 
 *  Interface for the Library Service. It provides a platform service
 *  for dynamic loading and unloading of classes, libraries and resources.
 */
public interface ILibraryService extends IService
{
	//-------- constants --------
	
	/** 
	 * The (standard) Library service name.
	 */
	public static final String LIBRARY_SERVICE = "library_service";
	
	//-------- methods --------
	
	/**
	 *  Add a new url.
	 *  @param url The url.
	 */
	public void addURL(URL url);
	
	/**
	 *  Add a path.
	 *  @param path The path.
	 */
	public void addPath(String path);
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public void removeURL(URL url);
	
	/**
	 *  Get all managed entries as URLs.
	 *  @return The list of urls.
	 */
	public IFuture getURLs();
	
	/**
	 *  Get other contained (but not directly managed) URLs.
	 *  @return The list of urls.
	 */
	public IFuture getNonManagedURLs();
	
	/** 
	 *  Returns the current ClassLoader.
	 *  @return the current ClassLoader
	 */
	public ClassLoader getClassLoader();
	
	//-------- listener methods --------
	
	/**
     *  Add an Library Service listener.
     *  The listener is registered for changes in the loaded library states.
     *  @param listener The listener to be added.
     */
    public void addLibraryServiceListener(ILibraryServiceListener listener);
    
    /**
     *  Remove an Library Service listener.
     *  @param listener  The listener to be removed.
     */
    public void removeLibraryServiceListener(ILibraryServiceListener listener);
}
