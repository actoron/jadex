package jadex.bridge.service.types.library;

import jadex.bridge.IInputConnection;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

import java.net.URL;
import java.util.List;

/** 
 *  Interface for the Library Service. It provides a platform service
 *  for dynamic loading and unloading of classes, libraries and resources.
 */
@GuiClassName("jadex.tools.libtool.LibServiceBrowser")
public interface ILibraryService
{
	//-------- methods --------
	
	/**
	 *  Add a new resource identifier.
	 *  @param rid The resource identifier.
	 */
	public IFuture<IResourceIdentifier> addResourceIdentifier(@CheckNotNull IResourceIdentifier rid, boolean workspace);
	
	/**
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifier(@CheckNotNull IResourceIdentifier rid);
	
	/**
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifierCompletely(@CheckNotNull IResourceIdentifier rid);
	
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
	public IFuture<IResourceIdentifier> addURL(@CheckNotNull URL url);
//	public IFuture<IResourceIdentifier> addURL(@CheckNotNull URL url, boolean workspace);
	
	/**
	 *  Remove a url.
	 *  @param url The url.
	 */
	public IFuture<Void> removeURL(@CheckNotNull URL url);
	
	/**
	 *  Remove a url completely (all references).
	 *  @param url The url.
	 */
	public IFuture<Void> removeURLCompletely(@CheckNotNull URL url);
	
	/**
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getNonManagedURLs();	
		
	/** 
	 *  Returns the current ClassLoader.
	 *  @return the current ClassLoader
	 */
	@Excluded
	public @Reference IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid);
	
	/** 
	 *  Returns the current ClassLoader.
	 *  @return the current ClassLoader
	 */
	@Excluded
	public @Reference IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid, boolean workspace);
	
//	/**
//	 *  Load a class given a class identifier.
//	 *  @param clid The class identifier.
//	 *  @return The class for the identifier.
//	 */
//	@Excluded
//	public IFuture<Class> loadClass(final IClassIdentifier clid);
	
	/** 
	 *  Returns the resource identifier.
	 *  @return The resource identifier.
	 */
	public IFuture<IResourceIdentifier> getResourceIdentifier(URL url);

	//-------- listener methods --------
	
	/**
     *  Add an Library Service listener.
     *  The listener is registered for changes in the loaded library states.
     *  @param listener The listener to be added.
     */
    public IFuture<Void> addLibraryServiceListener(@CheckNotNull ILibraryServiceListener listener);
    
    /**
     *  Remove an Library Service listener.
     *  @param listener  The listener to be removed.
     */
    public IFuture<Void> removeLibraryServiceListener(@CheckNotNull ILibraryServiceListener listener);


	/**
	 *  Get all urls (managed and non-managed).
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getAllURLs();
	
	/**
	 *  Get the jar for a rid. 
	 *  @param rid The rid.
	 *  @return The jar.
	 */
	public IFuture<IInputConnection> getJar(IResourceIdentifier rid);
	
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
