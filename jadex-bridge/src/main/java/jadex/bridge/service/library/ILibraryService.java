package jadex.bridge.service.library;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.commons.future.IFuture;

import java.net.URL;
import java.util.List;

/** 
 *  Interface for the Library Service. It provides a platform service
 *  for dynamic loading and unloading of classes, libraries and resources.
 */
@GuiClassName("jadex.tools.libtool.LibServiceBrowser")
public interface ILibraryService extends IService
{
	//-------- constants --------
	
	/** 
	 * The (standard) Library service name.
	 */
	public static final String LIBRARY_SERVICE = "library_service";
	
	//-------- methods --------
	
	/**
	 *  Add a new resource identifier.
	 *  @param rid The resource identifier.
	 */
	public IFuture<Void> addResourceIdentifier(IResourceIdentifier rid);
	
	/**
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifier(IResourceIdentifier rid);
	
	/**
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifierCompletely(IResourceIdentifier rid);
	
	/**
	 *  Get all managed (directly added i.e. top-level) resource identifiers.
	 *  @return The list of resource identifiers.
	 */
	public IFuture<List<IResourceIdentifier>> getManagedResourceIdentifiers();
	
	/**
	 *  Get all resource identifiers (also indirectly managed. 
	 */
	public IFuture<List<IResourceIdentifier>> getIndirectResourceIdentifiers();
	
	/**
	 *   Get all resource identifiers (does not include rids (urls) of parent loader).
	 *  @return The list of resource identifiers.
	 */
	public IFuture<List<IResourceIdentifier>> getAllResourceIdentifiers();
	
	
	/**
	 *  Add a new url.
	 *  @param url The url.
	 */
	public IFuture<Void> addURL(URL url);
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public IFuture<Void> removeURL(URL url);
	
	/**
	 *  Remove a url completely (all references).
	 *  @param url The url.
	 */
	public IFuture<Void> removeURLCompletely(URL url);
	
	/**
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getNonManagedURLs();	
		
	/** 
	 *  Returns the current ClassLoader.
	 *  @return the current ClassLoader
	 */
	@Excluded()
	public IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid);
	
	/** 
	 *  Returns the resource identifier.
	 *  @return The resource identifier.
	 */
	@Excluded()
	public IFuture<IResourceIdentifier> getResourceIdentifier(URL url);

	//-------- listener methods --------
	
	/**
     *  Add an Library Service listener.
     *  The listener is registered for changes in the loaded library states.
     *  @param listener The listener to be added.
     */
    public IFuture<Void> addLibraryServiceListener(ILibraryServiceListener listener);
    
    /**
     *  Remove an Library Service listener.
     *  @param listener  The listener to be removed.
     */
    public IFuture<Void> removeLibraryServiceListener(ILibraryServiceListener listener);


	/**
	 *  Get all urls (managed and non-managed).
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getAllURLs();
	
//	/**
//	 *  Get the non-managed classpath entries as strings.
//	 *  @return Classpath entries as a list of strings.
//	 */
//	public IFuture<List<String>> getURLStrings();
//	
//	/**
//	 *  Get the non-managed classpath entries.
//	 *  @return Classpath entries as a list of strings.
//	 */
//	public IFuture<List<String>> getNonManagedURLStrings();
//	
//	/**
//	 *  Get all managed entries as URLs.
//	 *  @return The list of urls.
//	 */
//	public IFuture<List<URL>> getURLs();
//	


//	/**
//	 *  Get a class definition.
//	 *  @param name The class name.
//	 *  @return The class definition as byte array.
//	 */
//	public IFuture<byte[]> getClassDefinition(String name);
	
//	/** 
//	 *  Returns the current ClassLoader.
//	 *  @return the current ClassLoader
//	 */
//	@Excluded()
//	public ClassLoader getClassLoader();
}
