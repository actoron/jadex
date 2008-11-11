package jadex.bridge;

import java.net.URL;
import java.util.List;
import java.util.Set;

/** 
 *  Interface for the Library Service. It provides a platform service
 *  for dynamic loading and unloading of classes, libraries and resources.
 */
public interface ILibraryService
{
	/** 
	 * The (standard) Library service name.
	 */
	public static final String LIBRARY_SERVICE = "library_service";
	
	/** 
	 *  Adds a .jar-file into the dynamic loading context.
	 *  All classes and resources in the .jar-file become available.
	 *  @param path path to the .jar-file
	 */
//	public void addJar(String path);

	/** 
	 *  Removes a .jar-file.
	 *  All classes and resources in the .jar-file become unavailable.
	 *  @param path path to the .jar-file
	 */
//	public void removeJar(String path);
	
	/** 
	 *  Adds a path to the ClassLoader class path.
	 *  All classes and resources within the path become available.
	 *  @param path new path
	 */
//	public void addPath(String path);

	/** 
	 *  Removes a path from the ClassLoader class path.
	 *  All classes and resources within the path become unavailable.
	 *  @param path path that should be removed
	 */
//	public void removePath(String path);
	
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
	 *  Returns the currently loaded .jar-files.
	 *  @return currently loaded .jar-files
	 */
//	public List getLoadedJars();
	
	/** 
	 *  Returns the currently loaded class paths.
	 *  @return currently loaded class paths
	 */
//	public List getLoadedPaths();
	
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
