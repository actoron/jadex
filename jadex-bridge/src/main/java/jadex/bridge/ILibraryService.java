package jadex.bridge;

import java.net.URL;
import java.util.List;

/** 
 *  Interface for the Library Service. It provides a platform service
 *  for dynamic loading and unloading of classes, libraries and resources.
 */
public interface ILibraryService extends IPlatformService
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
	 *  Remove a url.
	 *  @param url The url.
	 */
	public void removeURL(URL url);
	
	/**
	 *  Get all managed entries as URLs.
	 *  @return url The urls.
	 */
	public List getURLs();
	
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
