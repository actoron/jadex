package jadex.bridge.service.types.library;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IInputConnection;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

/** 
 *  Interface for the Library Service. It provides a platform service
 *  for dynamic loading and unloading of classes, libraries and resources.
 */
@Service(system=true)
@GuiClassName("jadex.tools.libtool.LibServiceBrowser")
public interface ILibraryService
{
	//-------- rid handling --------
	
	/**
	 *  Add a new resource identifier.
	 *  @param parid The optional parent rid.
	 *  @param rid The resource identifier.
	 */
	public IFuture<IResourceIdentifier> addResourceIdentifier(IResourceIdentifier parid,
		@CheckNotNull IResourceIdentifier rid, boolean workspace);
	
	/**
	 *  Remove a resource identifier.
	 *  @param parid The optional parent rid.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifier(IResourceIdentifier parid, 
		@CheckNotNull IResourceIdentifier rid);
		
	/**
	 *  Get the removable links.
	 */
	public IFuture<Set<Tuple2<IResourceIdentifier, IResourceIdentifier>>> getRemovableLinks();
	
	/**
	 *  Get all resource identifiers (does not include rids (urls) of parent loader).
	 *  @return The list of resource identifiers.
	 */
	public IFuture<List<IResourceIdentifier>> getAllResourceIdentifiers();
	
	/**
	 *  Get the resource identifier dependencies. Includes also system urls via a pseudo
	 *  rid defined as constant in LibraryService.SYSTEMCPRID.
	 */
	public IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> getResourceIdentifiers();
	
	/** 
	 *  Returns the classloader for a resource identifier.
	 *  @param rid The resource identifier.
	 *  @return The classloader.
	 */
	@Excluded
	public @Reference IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid);
	
	/** 
	 *  Returns the classloader for a resource identifier.
	 *  @param rid The resource identifier.
	 *  @param workspace True if workspace resolution is ok.
	 *  @return The classloader.
	 */
	@Excluded
	public @Reference IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid, boolean workspace);
	
	//-------- remote rid handling --------
	
	/**
	 *  Get a resource as stream (jar).
	 */
	public IFuture<IInputConnection>	getResourceAsStream(IResourceIdentifier rid);
	
	//-------- url handling --------
	
	/**
	 *  Add a new url as resource identifier.
	 *  @param parid The resource identifier (null for root entry).
	 *  @param url The url.
	 */
	public IFuture<IResourceIdentifier> addURL(IResourceIdentifier parid, @CheckNotNull URL url);
	
	/**
	 *  Remove a url.
	 *  @param parid The resource identifier (null for root entry).
	 *  @param url The url.
	 */
	public IFuture<Void> removeURL(IResourceIdentifier parid, @CheckNotNull URL url);

	/** 
	 *  Returns the resource identifier for a url.
	 *  @param url The url.
	 *  @return The corresponding resource identifier.
	 */
	public IFuture<IResourceIdentifier> getResourceIdentifier(URL url);

	/** 
	 *  Get the top-level resource identifier.
	 *  @param url The url.
	 *  @return The corresponding resource identifier.
	 */
	public IResourceIdentifier getRootResourceIdentifier();
	
	/**
	 *  Add a top level url. A top level url will
	 *  be available for all subordinated resources. 
	 *  @param url The url.
	 */
	public IFuture<Void> addTopLevelURL(@CheckNotNull URL url);

	/**
	 *  Remove a top level url. A top level url will
	 *  be available for all subordinated resources. 
	 *  @param url The url.
	 *  
	 *  note: top level url removal will only take 
	 *  effect after restart of the platform.
	 */
	public IFuture<Void> removeTopLevelURL(@CheckNotNull URL url);
	
	/**
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getNonManagedURLs();	
	
	/**
	 *  Get all urls (managed and non-managed). Uses getAllResourceIdentifiers() 
	 *  for managed and getNonManagedURLs() for unmanaged.
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getAllURLs();
	
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
	
}
