package jadex.platform.service.library;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;

/**
 *  Library service for loading classpath elements.
 */
@Service(ILibraryService.class)
public class LibraryService	implements ILibraryService, IPropertiesProvider
{
	//-------- constants --------
	
	/** The (standard) Library service name. */
	public static final String LIBRARY_SERVICE = "library_service";
	
	/** The pseudo system classpath rid. */
	public static final IResourceIdentifier SYSTEMCPRID;
	
	static
	{
		IResourceIdentifier res = null;
		try
		{
			res = new ResourceIdentifier(new LocalResourceIdentifier(new ComponentIdentifier("PSEUDO"), new URL("http://SYSTEMCPRID")), null);
		}
		catch(Exception e)
		{
			// should not happen
			e.printStackTrace();
		}
		SYSTEMCPRID = res;
	}
	
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** LibraryService listeners. */
	protected Set<ILibraryServiceListener> listeners;

	/** The init urls. */
	protected Object[] initurls;

	/** The class loader futures for currently loading class loaders. */
	protected Map<IResourceIdentifier, Future<DelegationURLClassLoader>> clfuts;
	
	/** The map of managed resources 2xrid (local, remote) -> delegate loader). */
	protected Map<IResourceIdentifier, DelegationURLClassLoader> classloaders;
	
	/** The base classloader. */
	protected ChangeableURLClassLoader baseloader;
	
	/** The delegation root loader. */
	protected DelegationURLClassLoader rootloader;
	protected IResourceIdentifier rootrid;
	// root rid is not set in rootloader as it is not a valid file url

	/** The added links. */
	protected Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> addedlinks;

	/** The remove links. */
	protected Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> removedlinks;
	
	/** The delayed add links (could not directly be added because the parent was not there). */
	protected Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> addtodo;
	
	// cached results
	
	/** The dependencies. */
	protected Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> rids;
	
	/** The non-managed urls (cached for speed). */
//	protected Set<URL>	nonmanaged;
	protected Set<URI>	nonmanaged;
	
	//-------- constructors --------
	
	/** 
	 *  Creates a new LibraryService.
	 */ 
	public LibraryService()
	{
		this((ClassLoader)null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls)
	{
		this(urls, null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param baseloader The base classloader that is parent of all subloaders.
	 */ 
	public LibraryService(ClassLoader baseloader)
	{
		this(null, baseloader, null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls, ClassLoader baseloader)
	{
		this(urls, baseloader, null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls, ClassLoader baseloader, Map<String, Object> properties)
	{
		this.classloaders = new HashMap<IResourceIdentifier, DelegationURLClassLoader>();
		this.clfuts = new HashMap<IResourceIdentifier, Future<DelegationURLClassLoader>>();
		this.listeners	= new LinkedHashSet<ILibraryServiceListener>();
		this.initurls = urls!=null? urls.clone(): null;
		this.baseloader = baseloader!=null? new ChangeableURLClassLoader(null, baseloader)
			: new ChangeableURLClassLoader(null, getClass().getClassLoader());
		this.rootloader = new DelegationURLClassLoader(this.baseloader, null);
		this.addedlinks = new HashSet<Tuple2<IResourceIdentifier,IResourceIdentifier>>();
		this.removedlinks = new HashSet<Tuple2<IResourceIdentifier,IResourceIdentifier>>();
		this.addtodo = new HashSet<Tuple2<IResourceIdentifier,IResourceIdentifier>>();
	}
	
	//-------- methods --------
	
	/**
	 *  Check if rid has local part and if it is null.
	 */
	protected void checkLocalRid(IResourceIdentifier rid)
	{
		if(rid!=null && rid.getLocalIdentifier()!=null && rid.getLocalIdentifier().getUri()==null)
		{
			System.out.println("local null rid found: "+rid);
//			throw new RuntimeException("local rid is null");
		}
	}
	
	/**
	 *  Add a new resource identifier.
	 *  @param parid The optional parent rid.
	 *  @param orid The resource identifier.
	 */
	public IFuture<IResourceIdentifier> addResourceIdentifier(final IResourceIdentifier parid,
		final IResourceIdentifier orid, final boolean workspace)
	{
//		System.out.println("adding: "+orid+" on: "+parid);
		checkLocalRid(parid);
		checkLocalRid(orid);
		
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();

//		if(parid!=null && !rootloader.getAllResourceIdentifiers().contains(parid))
		if(parid!=null && !internalgetAllResourceIdentifiers().contains(parid))
		{
			ret.setException(new RuntimeException("Parent rid unknown: "+parid));
		}
		else
		{
			getDependencies(orid, workspace).addResultListener(new ExceptionDelegationResultListener
				<Tuple2<IResourceIdentifier,Map<IResourceIdentifier,List<IResourceIdentifier>>>, IResourceIdentifier>(ret)
			{
				public void customResultAvailable(Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> deps)
				{
					final IResourceIdentifier rid = deps.getFirstEntity();
	//				
					// If could not be resolved to local (or already was local) consider as exception.
					if(rid.getLocalIdentifier()==null)
					{
						ret.setException(new RuntimeException("Global rid could not be resolved to local: "+rid));
					}
					else
					{
						// Check if file exists
						if(checkUri(rid.getLocalIdentifier().getUri())==null)
						{
							ret.setException(new RuntimeException("Local rid url invalid: "+rid));
						}
						else
						{
	//						System.out.println("add end "+rid+" on: "+parid);
		
							// Must be added with resolved rid (what about resolving parent?)
							addLink(parid, rid);
		
							getClassLoader(rid, deps.getSecondEntity(), parid, workspace).addResultListener(
								new ExceptionDelegationResultListener<DelegationURLClassLoader, IResourceIdentifier>(ret)
							{
								public void customResultAvailable(final DelegationURLClassLoader chil)
								{
									ret.setResult(rid);
								}
							});
						}
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
					super.exceptionOccurred(exception);
				}
			});
			
	//		System.out.println("current: "+SUtil.arrayToString(rootloader.getAllResourceIdentifiers()));
		}
		
//		ret.addResultListener(new IResultListener<IResourceIdentifier>()
//		{
//			public void resultAvailable(IResourceIdentifier result)
//			{
//				System.out.println("good");
//			}
//
//			public void exceptionOccurred(Exception exception)
//			{
//				exception.printStackTrace();
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Remove a resource identifier.
	 *  @param uri The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifier(IResourceIdentifier parid, final IResourceIdentifier rid)
	{
		checkLocalRid(rid);
		checkLocalRid(parid);
		
//		System.out.println("remove "+rid);
		final Future<Void> ret = new Future<Void>();
		
//		if(parid!=null && !rootloader.getAllResourceIdentifiers().contains(parid))
		if(parid!=null && !internalgetAllResourceIdentifiers().contains(parid))
		{
			ret.setException(new RuntimeException("Parent rid unknown: "+parid));
		}
		else
		{
			removeLink(parid, rid);
			removeSupport(rid, parid);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/** 
	 *  Get the root resource identifier.
	 *  @param uri The url.
	 *  @return The corresponding resource identifier.
	 */
	public IResourceIdentifier getRootResourceIdentifier()
	{
		return rootrid;
	}
	
	/**
	 *  Get all resource identifiers (does not include urls of parent loader).
	 *  @return The list of resource identifiers.
	 */
	public IFuture<List<IResourceIdentifier>> getAllResourceIdentifiers()
	{
		return new Future<List<IResourceIdentifier>>(new ArrayList<IResourceIdentifier>(internalgetAllResourceIdentifiers()));
//		return new Future<List<IResourceIdentifier>>(new ArrayList<IResourceIdentifier>(rootloader.getAllResourceIdentifiers()));
	}
	
	// This implementation is incorrect as not for all added rids classloaders are created (if already managed by base loader)
//	/**
//	 *  Get the rids.
//	 */
//	public IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> getResourceIdentifiers()
//	{
//		if(rids==null)
//		{
//			Map<IResourceIdentifier, List<IResourceIdentifier>> deps = new HashMap<IResourceIdentifier, List<IResourceIdentifier>>();
//			
//			List<IResourceIdentifier> todo = new ArrayList<IResourceIdentifier>();
////			todo.add(rootloader.getResourceIdentifier());
//			todo.add(null);
//			
//			while(!todo.isEmpty())
//			{
//				IResourceIdentifier clrid = todo.remove(0);
//				if(SYSTEMCPRID.equals(clrid))
//				{
//					List<IResourceIdentifier> mydeps = new ArrayList<IResourceIdentifier>();
//					Set<URI> nonmans = getInternalNonManagedURLs();
//					for(URI uri: nonmans)
//					{
//						URL url = SUtil.toURL0(uri);
//						if(url!=null)
//							mydeps.add(new ResourceIdentifier(new LocalResourceIdentifier(component.getComponentIdentifier().getRoot(), url), null));
//					}
//					deps.put(clrid, mydeps);
//				}
//				else
//				{
//					DelegationURLClassLoader cl = clrid==null? rootloader: classloaders.get(clrid);
//					List<IResourceIdentifier> mydeps = cl.getDelegateResourceIdentifiers();
//					deps.put(clrid, mydeps);
//					if(clrid==null)
//					{
//						mydeps.add(SYSTEMCPRID);
//					}
//					for(IResourceIdentifier rid: mydeps)
//					{
//						if(!deps.containsKey(rid))
//						{
//							todo.add(rid);
//						}
//					}
//				}
//			}
//			
//			rids = new Tuple2<IResourceIdentifier, Map<IResourceIdentifier,List<IResourceIdentifier>>>(rootloader.getResourceIdentifier(), deps);
//		}
//		
//		return new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(rids);
//	}
	
	/**
	 *  Get the rids.
	 */
	public IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> getResourceIdentifiers()
	{
		return new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(internalGetResourceIdentifiers());
	}
	
	/**
	 * 
	 */
	public Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> internalGetResourceIdentifiers()
	{
		if(rids==null)
		{
			Map<IResourceIdentifier, List<IResourceIdentifier>> deps = new HashMap<IResourceIdentifier, List<IResourceIdentifier>>();

			// rootloader rid
			List<IResourceIdentifier> mydeps = rootloader.getDelegateResourceIdentifiers();
			deps.put(null, mydeps);
			mydeps.add(SYSTEMCPRID);
			
			// baseloader rid
			mydeps = new ArrayList<IResourceIdentifier>();
			Set<URI> nonmans = getInternalNonManagedURLs();
			for(URI uri: nonmans)
			{
				mydeps.add(new ResourceIdentifier(new LocalResourceIdentifier(component.getId().getRoot(), SUtil.toURL(uri)), null));
			}
			deps.put(SYSTEMCPRID, mydeps);
			
			// other rids
			for(Tuple2<IResourceIdentifier, IResourceIdentifier> link: addedlinks)
			{
				mydeps = deps.get(link.getFirstEntity());
				if(mydeps==null)
				{
					mydeps = new ArrayList<IResourceIdentifier>();
					deps.put(link.getFirstEntity(), mydeps);
				}
				if(!mydeps.contains(link.getSecondEntity()))
				{
					mydeps.add(link.getSecondEntity());
				}
			}
			
			rids = new Tuple2<IResourceIdentifier, Map<IResourceIdentifier,List<IResourceIdentifier>>>(rootloader.getResourceIdentifier(), deps);
		}
		
		return rids;
	}
	
	/**
	 *  Add a new url.
	 *  @param uri The resource identifier.
	 */
	public IFuture<IResourceIdentifier> addURL(final IResourceIdentifier parid, URL purl)
	{
		checkLocalRid(parid);
		
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();

//		System.out.println("add url: "+url);
		
		URL url = checkUrl(purl);
		if(url==null)
		{
			ret.setException(new RuntimeException("URL not backed by local file: "+purl));
			return ret;
		}
//		System.out.println("add url2: "+url);
		
		// Normalize files to avoid duplicate urls.
		if("file".equals(url.getProtocol()))
		{
			try
			{
				url	= SUtil.getFile(url).getCanonicalFile().toURI().toURL();
			}
			catch(Exception e)
			{
			}
		}
		
		internalGetResourceIdentifier(url).addResultListener(
			new DelegationResultListener<IResourceIdentifier>(ret)
		{
			public void customResultAvailable(final IResourceIdentifier rid)
			{
				// todo: should be true?
				addResourceIdentifier(parid, rid, true).addResultListener(
					new ExceptionDelegationResultListener<IResourceIdentifier, IResourceIdentifier>(ret)
				{
					public void customResultAvailable(IResourceIdentifier result)
					{
						ret.setResult(rid);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Remove a new url.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeURL(final IResourceIdentifier parid, final URL url)
	{
		final Future<Void> ret = new Future<Void>();
		
		internalGetResourceIdentifier(url).addResultListener(
			new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
		{
			public void customResultAvailable(IResourceIdentifier result)
			{
				removeResourceIdentifier(parid, result).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a top level url. A top level url will
	 *  be available for all subordinated resources. 
	 *  @param uri The url.
	 */
	public IFuture<Void> addTopLevelURL(@CheckNotNull URL purl)
	{
		URL url = checkUrl(purl);
		if(url==null)
			return new Future<Void>(new RuntimeException("URL not backed by local file: "+purl));
		
		baseloader.addURL(url);
		nonmanaged = null;
		IResourceIdentifier rid = new ResourceIdentifier(new LocalResourceIdentifier(component.getId().getRoot(), url), null);
		addLink(SYSTEMCPRID, rid);
		notifyAdditionListeners(SYSTEMCPRID, rid);
		return IFuture.DONE;
	}

	/**
	 *  Remove a top level url. A top level url will
	 *  be available for all subordinated resources. 
	 *  @param url The url.
	 *  
	 *  note: top level url removal will only take 
	 *  effect after restart of the platform.
	 */
	public IFuture<Void> removeTopLevelURL(@CheckNotNull URL url)
	{
		baseloader.removeURL(url);
		nonmanaged = null;
		IResourceIdentifier rid = new ResourceIdentifier(new LocalResourceIdentifier(component.getId().getRoot(), url), null);
		removeLink(SYSTEMCPRID, rid);
		notifyRemovalListeners(SYSTEMCPRID, rid);
		return IFuture.DONE;
	}
	
	/**
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getNonManagedURLs()
	{
		Set<URI> res = getInternalNonManagedURLs();
		List<URL> ret = new ArrayList<URL>();
		if(res!=null)
		{
			for(URI uri: res)
			{
				try
				{
					ret.add(uri.toURL());
				}
				catch(Exception e)
				{
					System.out.println("Problem with url: "+uri);
				}
			}
		}
		return new Future<List<URL>>(ret);
	}
	
	/**
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The set of urls.
	 */
	protected Set<URI>	getInternalNonManagedURLs()
	{
		if(nonmanaged==null)
		{
			nonmanaged	= new LinkedHashSet<URI>();
			collectClasspathURLs(baseloader, nonmanaged, new HashSet<String>());
		}
		return nonmanaged;
	}
	
	/**
	 *  Get all urls (managed, indirect and non-managed from parent loader).
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getAllURLs()
	{
//		final long	start	= System.currentTimeMillis();
		final Future<List<URL>> ret = new Future<List<URL>>();
		
		getAllResourceIdentifiers().addResultListener(new ExceptionDelegationResultListener<List<IResourceIdentifier>, List<URL>>(ret)
		{
			public void customResultAvailable(List<IResourceIdentifier> result)
			{
				final List<URL> res = new ArrayList<URL>();
				for(int i=0; i<result.size(); i++)
				{
					if(!rootrid.equals(result.get(i)))
					{
						URL url = SUtil.toURL0(result.get(i).getLocalIdentifier().getUri());
						if(url!=null)
							res.add(url);
					}
				}
				
				Set<URI> re = getInternalNonManagedURLs();
				for(URI uri: re)
				{
					URL url = SUtil.toURL0(uri);
					if(url!=null)
						res.add(url);
				}
//				res.addAll();
				
//				System.out.println("getAllUrls: "+(System.currentTimeMillis()-start));
				ret.setResult(res);
			}
		});
		
		return ret;
	}
		
	/** 
	 *  Returns the current ClassLoader.
	 *  @return the current ClassLoader
	 */
	@Excluded
	public @Reference IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid)
	{
		return getClassLoader(rid, true);
	}
	
	/** 
	 *  Returns the current ClassLoader.
	 *  @param rid The resource identifier (null for current global loader).
	 *  @return the current ClassLoader
	 */
	@Excluded()
	public IFuture<ClassLoader> getClassLoader(final IResourceIdentifier rid, boolean workspace)
	{
		checkLocalRid(rid);
		final Future<ClassLoader> ret = new Future<ClassLoader>();
		
		if(rid==null || rid.equals(rootloader.getResourceIdentifier()))
		{
			ret.setResult(rootloader);
//			System.out.println("root classloader: "+rid);
		}
		else if(ResourceIdentifier.isLocal(rid, component.getId().getRoot()) && getInternalNonManagedURLs().contains(rid.getLocalIdentifier().getUri()))
		{
			ret.setResult(baseloader);
//			System.out.println("base classloader: "+rid);
		}	
		else
		{
			if(!internalGetResourceIdentifiers().getSecondEntity().containsKey(rid))
			{
				addLink(null, rid);
			}
			
			getClassLoader(rid, null, rootloader.getResourceIdentifier(), workspace).addResultListener(new ExceptionDelegationResultListener<DelegationURLClassLoader, ClassLoader>(ret)
			{
				public void customResultAvailable(DelegationURLClassLoader result)
				{
					ret.setResult(result);
//					System.out.println("custom classloader: "+result.hashCode()+" "+rid);
				}
			});
		}
		
		return ret;
	}
	
	/** 
	 *  Get the resource identifier for an url.
	 *  @return The resource identifier.
	 */
	public IFuture<IResourceIdentifier> getResourceIdentifier(final URL url)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		
		component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IDependencyService.class, ServiceScope.PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IDependencyService, IResourceIdentifier>(ret)
		{
			public void customResultAvailable(IDependencyService drs)
			{
				drs.getResourceIdentifier(url).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
			}
		});
		
		return ret;
	}

	//-------- listener methods --------
	
    /**
	 *  Add an Library Service listener.
	 *  The listener is registered for changes in the loaded library states.
	 *  @param listener The listener to be added.
	 */
	public IFuture<Void> addLibraryServiceListener(ILibraryServiceListener listener)
	{
		if(listener==null)
			throw new IllegalArgumentException();
			
		listeners.add(listener);
		return IFuture.DONE;
	}

	/**
	 *  Remove an Library Service listener.
	 *  @param listener  The listener to be removed.
	 */
	public IFuture<Void> removeLibraryServiceListener(ILibraryServiceListener listener)
	{
		listeners.remove(listener);
		return IFuture.DONE;
	}
	
	//-------- internal methods --------
	
	/**
	 *  Get or create a classloader for a rid.
	 */
	protected IFuture<DelegationURLClassLoader> getClassLoader(final IResourceIdentifier rid, 
		Map<IResourceIdentifier, List<IResourceIdentifier>> alldeps, final IResourceIdentifier support, final boolean workspace)
	{
		final Future<DelegationURLClassLoader> ret;
		
		// full=(global, local) calls followed by any other call are ok, because global and local can be cached
		// pure global call followed by pure local call -> would mean rids have not been resolved
		// pure local call followed by pure global call -> would mean rids have not been resolved
		if(ResourceIdentifier.isLocal(rid, component.getId().getRoot()) && getInternalNonManagedURLs().contains(rid.getLocalIdentifier().getUri()))
		{
			ret	= new Future<DelegationURLClassLoader>((DelegationURLClassLoader)null);
			notifyAdditionListeners(support, rid);
		}
		else if(clfuts.containsKey(rid))
		{
			ret	= clfuts.get(rid);
		}
		else
		{
	//		final URL url = rid.getLocalIdentifier().getSecondEntity();
			
			ret = new Future<DelegationURLClassLoader>();
			DelegationURLClassLoader cl = (DelegationURLClassLoader)classloaders.get(rid);
			
			if(cl!=null)
			{
				addSupport(rid, support);
				ret.setResult(cl);
			}
			else
			{
				clfuts.put(rid, ret);
//				if(rid.getGlobalIdentifier()==null)
//					System.out.println("getClassLoader(): "+rid);
				
				if(alldeps==null)
				{
//					System.out.println("getdeps in getcl: "+component.getComponentIdentifier()+", "+rid);
					getDependencies(rid, workspace).addResultListener(
						new ExceptionDelegationResultListener<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>, DelegationURLClassLoader>(ret)
					{
						public void customResultAvailable(Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> deps)
						{
							createClassLoader(deps.getFirstEntity(), deps.getSecondEntity(), support, workspace).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret)
							{
								public void customResultAvailable(DelegationURLClassLoader result)
								{
									clfuts.remove(rid);
									super.customResultAvailable(result);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									clfuts.remove(rid);
									super.exceptionOccurred(exception);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							clfuts.remove(rid);
							super.exceptionOccurred(exception);
						}
					});
				}
				else
				{
//					System.out.println("create cl: "+component.getComponentIdentifier()+", "+rid);
					createClassLoader(rid, alldeps, support, workspace).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret)
					{
						public void customResultAvailable(DelegationURLClassLoader result)
						{
							clfuts.remove(rid);
							super.customResultAvailable(result);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							clfuts.remove(rid);
							super.exceptionOccurred(exception);
						}
					});
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a new classloader.
	 */
	protected IFuture<DelegationURLClassLoader> createClassLoader(final IResourceIdentifier rid, 
		final Map<IResourceIdentifier, List<IResourceIdentifier>> alldeps, final IResourceIdentifier support, final boolean workspace)
	{
		checkLocalRid(rid);
		
		// Class loaders shouldn't be created for local URLs, which are already available in base class loader.
		assert rid.getLocalIdentifier()==null || !ResourceIdentifier.isLocal(rid, component.getId().getRoot()) || !getInternalNonManagedURLs().contains(rid.getLocalIdentifier().getUri());
		
		final Future<DelegationURLClassLoader> ret = new Future<DelegationURLClassLoader>();
		
		if(isAvailable(rid))
		{
			final DelegationURLClassLoader cl = createNewDelegationClassLoader(rid, baseloader, null);
			classloaders.put(rid, cl);
			
//			System.out.println("createClassLoader() put: "+component.getComponentIdentifier()+", "+rid);
			
			final List<IResourceIdentifier> deps = alldeps.get(rid);
			CollectionResultListener<DelegationURLClassLoader> lis = new CollectionResultListener<DelegationURLClassLoader>
				(deps.size(), true, new ExceptionDelegationResultListener<Collection<DelegationURLClassLoader>, DelegationURLClassLoader>(ret)
			{
				public void customResultAvailable(Collection<DelegationURLClassLoader> result)
				{
					// Strip null values of provided dependencies from results.
					for(Iterator<DelegationURLClassLoader> it=result.iterator(); it.hasNext(); )
					{
						if(it.next()==null)
							it.remove();
					}
					
					for(DelegationURLClassLoader dcl: result)
					{
						cl.addDelegateClassLoader(dcl);
					}
					addSupport(rid, support);
					ret.setResult(cl);
				}
			});
			
			for(int i=0; i<deps.size(); i++)
			{
				IResourceIdentifier myrid = (IResourceIdentifier)deps.get(i);
				getClassLoader(myrid, alldeps, rid, workspace).addResultListener(lis);
			}
		}
		else
		{
			downloadResource(rid)
				.addResultListener(new ExceptionDelegationResultListener<Void, DelegationURLClassLoader>(ret)
			{
				public void customResultAvailable(Void result)
				{
					createClassLoader(rid, alldeps, support, workspace).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret));
				}
			});
		}
		
		return ret;
	}

	/**
	 * Handle instantiation here, so the DelegationURLClassLoader can be another
	 * implementation.
	 * @param rid
	 * @param baseloader
	 * @param delegates
	 * @return {@link DelegationURLClassLoader} or subclass.
	 */
	protected DelegationURLClassLoader createNewDelegationClassLoader(final IResourceIdentifier rid, ClassLoader baseloader, DelegationURLClassLoader[] delegates)
	{
		URL url = getRidUrl(rid);
		return new DelegationURLClassLoader(rid, url, baseloader, delegates);
	}
	
	/**
	 *  Get the local file url for a rid.
	 */
	protected URL getRidUrl(final IResourceIdentifier rid)
	{
		URL	url;
		if(ResourceIdentifier.isLocal(rid, component.getId().getRoot()))
		{
			url	= rid!=null && rid.getLocalIdentifier()!=null && rid.getLocalIdentifier().getUri()!=null? SUtil.toURL(rid.getLocalIdentifier().getUri()): null;
		}
		else
		{
			File	jar	= getHashRidFile(rid);
			if(!jar.exists())
			{
				throw new RuntimeException("Resource not found: "+jar);
			}
			else
			{
				try
				{
					url	= jar.toURI().toURL();
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		return url;
	}
	
	/**
	 * Test, if a resource is available locally.
	 */
	protected boolean	isAvailable(IResourceIdentifier rid)	
	{
		// Do not check existence of manually added (local) resources
		return ResourceIdentifier.isLocal(rid, component.getId().getRoot())
			|| getHashRidFile(rid).exists();
	}

	/**
	 *  Get the file for a hash rid.
	 */
	protected File	getHashRidFile(IResourceIdentifier rid)
	{
		assert ResourceIdentifier.isHashGid(rid);

		// http://tools.ietf.org/html/rfc3548#section-4 for local storage of hashed resources
		String	name	= rid.getGlobalIdentifier().getResourceId().substring(2).replace('+', '-').replace('/', '_') + ".jar";
		IContextService localService = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IContextService.class));
		// use contextService to get private data dir on android
		IFuture<File> future = localService.getFile(SUtil.JADEXDIR + "resources/"+name);
		File file = future.get();
		return file;
	}
	
	/**
	 *  Download a resource from another platform.
	 */
	protected IFuture<Void>	downloadResource(final IResourceIdentifier rid)
	{
		assert rid!=null && rid.getLocalIdentifier()!=null && rid.getLocalIdentifier().getComponentIdentifier()!=null;
		
		final Future<Void>	ret	= new Future<Void>();
		final IComponentIdentifier	remote	= rid.getLocalIdentifier().getComponentIdentifier();
		component.getExternalAccessAsync(remote).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(IExternalAccess exta)
			{
				exta.searchService( new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM))
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
				{
					public void customResultAvailable(ILibraryService ls)
					{
						ls.getResourceAsStream(rid)
							.addResultListener(new ExceptionDelegationResultListener<IInputConnection, Void>(ret)
						{
							public void customResultAvailable(IInputConnection icon)
							{
								try
								{
									File	f	= getHashRidFile(rid);
									f.getParentFile().mkdirs();
									final OutputStream	os	= new BufferedOutputStream(new FileOutputStream(f));
									icon.writeToOutputStream(os, component.getExternalAccess())
										.addResultListener(new IIntermediateResultListener<Long>()
									{
										public void exceptionOccurred(Exception exception)
										{
											exception.printStackTrace();
											try
											{
												os.close();
											}
											catch(Exception e)
											{
												// ignore
											}
											ret.setException(null);
										}
										
										public void finished()
										{
//													System.out.println("finished");
											try
											{
												os.close();
											}
											catch(Exception e)
											{
												// ignore
											}
											ret.setResult(null);
										}
										
										public void intermediateResultAvailable(Long result)
										{
											// ignore
//													System.out.println("update: "+result);
										}
										
										public void resultAvailable(Collection<Long> result)
										{
											// should not be called.
										}
									});
								}
								catch(FileNotFoundException e)
								{
									throw new RuntimeException(e);
								}
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get a resource as stream (jar).
	 */
	public IFuture<IInputConnection>	getResourceAsStream(IResourceIdentifier rid)
	{
		try
		{
			final InputStream	is;
			final File	file	= SUtil.getFile(getRidUrl(rid));
			
			if(file.isDirectory())
			{
				final PipedOutputStream	pos	= new PipedOutputStream();
				is	= new PipedInputStream(pos, 8192*4);
				
				component.getExternalAccess().searchService( new ServiceQuery<>( IDaemonThreadPoolService.class, ServiceScope.PLATFORM))
					.addResultListener(new IResultListener<IDaemonThreadPoolService>()
				{
					public void resultAvailable(IDaemonThreadPoolService tps)
					{
						tps.execute(new Runnable()
						{
							public void run()
							{
								SUtil.writeDirectory(file, new BufferedOutputStream(pos));
//								try
//								{
//									is.close();
//								}
//								catch(IOException e)
//								{
//								}
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// Shouldn't happen...
						exception.printStackTrace();
					}
				});
			}
			else
			{
				is	= new FileInputStream(file);
			}
			
			ServiceOutputConnection	soc	= new ServiceOutputConnection();
			soc.writeFromInputStream(is, component.getExternalAccess())
				.addResultListener(new IntermediateDefaultResultListener<Long>()
			{
				public void finished()
				{
					try
					{
						is.close();
					}
					catch(IOException e)
					{
						// ignore
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					try
					{
						is.close();
					}
					catch(IOException e)
					{
						// ignore
					}
				}
			});
			
			return new Future<IInputConnection>(soc.getInputConnection());
		}
		catch(IOException e)
		{
			return new Future<IInputConnection>(e);
		}
	}

	
	/**
	 *  Get the dependent urls.
	 */
	protected IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> 
		getDependencies(final IResourceIdentifier rid, final boolean workspace)
	{
		final Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> ret = new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>();
		
		component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IDependencyService.class, ServiceScope.PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IDependencyService, Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(ret)
		{
			public void customResultAvailable(IDependencyService drs)
			{
				drs.loadDependencies(rid, workspace).addResultListener(new DelegationResultListener<Tuple2<IResourceIdentifier, Map<IResourceIdentifier,List<IResourceIdentifier>>>>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add support for a rid.
	 */
	protected void addSupport(IResourceIdentifier rid, IResourceIdentifier parid)
	{
		if(rid==null)
			throw new IllegalArgumentException("Rid must not null.");
		checkLocalRid(rid);		
		checkLocalRid(parid);
		
		DelegationURLClassLoader pacl = parid==null || rootrid.equals(parid)? rootloader: (DelegationURLClassLoader)classloaders.get(parid);
		// special case that parid is local and already handled by baseloader
		if(pacl==null && ResourceIdentifier.isLocal(parid, component.getId().getRoot()) && getInternalNonManagedURLs().contains(parid.getLocalIdentifier().getUri()))
		{
			pacl = rootloader;
		}
		DelegationURLClassLoader cl = (DelegationURLClassLoader)classloaders.get(rid);
		pacl.addDelegateClassLoader(cl);
		
		if(cl.addParentClassLoader(pacl))
		{
			rids = null;
			notifyAdditionListeners(parid, rid);
		}
		
		// Check if pending user entries can be restored
		for(Iterator<Tuple2<IResourceIdentifier, IResourceIdentifier>> it=addtodo.iterator(); it.hasNext(); )
		{
			Tuple2<IResourceIdentifier, IResourceIdentifier> link = it.next();
			if(rid.equals(link.getFirstEntity()))
			{
				addResourceIdentifier(link.getFirstEntity(), link.getSecondEntity(), true);
				it.remove();
			}
		}
	}
	
	/**
	 *  Remove support for a rid.
	 */
	protected void removeSupport(IResourceIdentifier rid, IResourceIdentifier parid)
	{
		if(rid==null)
			throw new IllegalArgumentException("Rid must not null.");

		DelegationURLClassLoader pacl = parid==null || rootrid.equals(parid)? rootloader: (DelegationURLClassLoader)classloaders.get(parid);
		// special case that parid is local and already handled by baseloader
		if(pacl==null && ResourceIdentifier.isLocal(parid, component.getId().getRoot()) && getInternalNonManagedURLs().contains(parid.getLocalIdentifier().getUri()))
		{
			pacl = rootloader;
		}
		DelegationURLClassLoader cl = (DelegationURLClassLoader)classloaders.get(rid);
		pacl.removeDelegateClassLoader(cl);
		if(cl.removeParentClassLoader(pacl))
		{
			rids = null;
			notifyRemovalListeners(parid, rid);
		}
		
		// If last support, delete removeSupport to children.
		if(!cl.hasParentClassLoader())
		{
			DelegationURLClassLoader[] dels = cl.getDelegateClassLoaders();
			for(DelegationURLClassLoader del: dels)
			{
				removeSupport(del.getResourceIdentifier(), rid);
			}
			classloaders.remove(rid);
		}
	}
	
	/**
	 *  Notify listeners about addition.
	 */
	protected void notifyAdditionListeners(final IResourceIdentifier parid, final IResourceIdentifier rid)
	{
		boolean rem = addedlinks.contains(new Tuple2<IResourceIdentifier, IResourceIdentifier>(parid, rid));
		// Do not notify listeners with lock held!
		ILibraryServiceListener[] lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		for(int i=0; i<lis.length; i++)
		{
			final ILibraryServiceListener liscopy = lis[i];
//			System.out.println("added: "+parid+" "+rid);
			lis[i].resourceIdentifierAdded(parid, rid, rem).addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
				}
				public void exceptionOccurred(Exception exception) 
				{
					// todo: how to handle timeouts?! allow manual retry?
	//				exception.printStackTrace();
					removeLibraryServiceListener(liscopy);
				};
			});
		}
	}
	
	/**
	 *  Notify listeners about removal.
	 */
	protected void notifyRemovalListeners(final IResourceIdentifier parid, final IResourceIdentifier rid)
	{
		// Do not notify listeners with lock held!
		ILibraryServiceListener[] lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		for(int i=0; i<lis.length; i++)
		{
			final ILibraryServiceListener liscopy = lis[i];
			lis[i].resourceIdentifierRemoved(parid, rid).addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
				}
				public void exceptionOccurred(Exception exception) 
				{
					// todo: how to handle timeouts?! allow manual retry?
	//				exception.printStackTrace();
					removeLibraryServiceListener(liscopy);
				};
			});
		}
	}
	
	/**
	 *  Get the resource identifier for an url.
	 */
	protected IFuture<IResourceIdentifier> internalGetResourceIdentifier(final URL url)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		
		component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IDependencyService.class, ServiceScope.PLATFORM))
			.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IDependencyService, IResourceIdentifier>(ret)
		{
			public void customResultAvailable(IDependencyService drs)
			{
				drs.getResourceIdentifier(url).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
			}
		}));
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		try
		{
			this.rootrid = new ResourceIdentifier(new LocalResourceIdentifier(component.getId(), new URL("http://ROOTRID")), null);
			this.rootloader.setResourceIdentifier(rootrid);
//			this.classloaders.put(rootrid, rootloader);
		}
		catch(Exception e)
		{
			// should not happen
			e.printStackTrace();
		}
		
		final Future<Void>	urlsdone	= new Future<Void>();
		if(initurls!=null)
		{
			CounterResultListener<IResourceIdentifier> lis = new CounterResultListener<IResourceIdentifier>(
				initurls.length, new DelegationResultListener<Void>(urlsdone));
			for(int i=0; i<initurls.length; i++)
			{
				addURL(null, SUtil.toURL(initurls[i])).addResultListener(lis);
			}
		}
		else
		{
			urlsdone.setResult(null);
		}
		
		final Future<Void>	ret	= new Future<Void>();
		urlsdone.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result) 
			{
				ISettingsService settings	= component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISettingsService.class).setMultiplicity(Multiplicity.ZERO_ONE));
				if(settings!=null)
				{
					settings.registerPropertiesProvider(LIBRARY_SERVICE, LibraryService.this)
						.addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					// No settings service: ignore
					ret.setResult(null);
				}
			}
		});
		return ret;
	}

	/** 
	 *  Shutdown the service.
	 *  Releases all cached resources and shuts down the library service.
	 *  @param listener The listener.
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdownService()
	{
//		System.out.println("shut");
		final Future<Void>	saved	= new Future<Void>();
		ISettingsService settings	= component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISettingsService.class).setMultiplicity(Multiplicity.ZERO_ONE));
		if(settings!=null)
		{
			settings.deregisterPropertiesProvider(LIBRARY_SERVICE)
				.addResultListener(new DelegationResultListener<Void>(saved));
		}
		else
		{
			// No settings service: ignore
			saved.setResult(null);
		}
		
		final Future<Void>	ret	= new Future<Void>();
		saved.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				synchronized(this)
				{
//					libcl = null;
					listeners.clear();
					ret.setResult(null);
				}
			}
		});
			
		return ret;
	}

	//-------- IPropertiesProvider interface --------
		
	/**
	 *  Collect all URLs belonging to a class loader.
	 */
	protected void	collectClasspathURLs(ClassLoader classloader, Set<URI> set, Set<String> jarnames)
	{
		assert classloader!=null;
		
		if(classloader.getParent()!=null)
		{
			collectClasspathURLs(classloader.getParent(), set, jarnames);
		}
		
		if(classloader instanceof URLClassLoader)
		{
			URL[] urls = ((URLClassLoader)classloader).getURLs();
			for(int i=0; i<urls.length; i++)
			{
				String	name	= SUtil.getFile(urls[i]).getName();
				if(name.endsWith(".jar"))
				{
					String jarname	= getJarName(name);
					jarnames.add(jarname);
				}
			}
			
			for(int i = 0; i < urls.length; i++)
			{
				URI uri = SUtil.toURI0(urls[i]);
				if(uri!=null)
				{
					set.add(uri);
					collectManifestURLs(uri, set, jarnames);
				}
			}
		}
		
//		System.out.println("non man: "+classloader+" "+set+" "+jarnames);
	}
	
	/**
	 *  Get the name of a jar file without extension and version info.
	 */
	protected static String	getJarName(String filename)
	{
		String	ret	= filename;
		int	slash	= filename.lastIndexOf('/');
		if(slash!=-1)
		{
			ret	= ret.substring(slash+1);
		}
		Scanner	s	= new Scanner(ret);
		s.findWithinHorizon("(.*?)(-[0-9]+\\.|\\.jar)", 0);
		ret	= s.match().group(1);
//		System.out.println("jar: "+filename+" is "+ret);
		s.close();
		return ret;
	}
	
	/**
	 *  Collect all URLs as specified in a manifest.
	 */
	protected void	collectManifestURLs(URI url, Set<URI> set, Set<String> jarnames)
	{
//		System.out.println("collectMainifestUrls: "+url);
		
		File	file	= SUtil.urlToFile(url.toString());
		if(file!=null && file.exists() && !file.isDirectory())	// Todo: load manifest also from directories!?
		{
			JarFile jarfile = null;
	        try 
	        {
	            jarfile	= new JarFile(file);
	            Manifest manifest = jarfile.getManifest();
	            if(manifest!=null)
	            {
	                String	classpath	= manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
	                if(classpath!=null)
	                {
	                	StringTokenizer	tok	= new StringTokenizer(classpath, " ");
	            		while(tok.hasMoreElements())
	            		{
	            			String path = tok.nextToken();
	            			File	urlfile;
	            			
	            			// Search in directory of original jar (todo: also search in local dir!?)
	            			urlfile = new File(file.getParentFile(), path);
	            			
	            			// Try as absolute path
	            			if(!urlfile.exists())
	            			{
	            				urlfile	= new File(path);
	            			}
	            			
	            			// Try as url
	            			if(!urlfile.exists())
	            			{
	            				urlfile	= SUtil.urlToFile(path);
	            			}
	
	            			if(urlfile!=null && urlfile.exists())
	            			{
		            			try
			                	{
		            				if(urlfile.getName().endsWith(".jar"))
		            				{
		            					String jarname	= getJarName(urlfile.getName());
		            					jarnames.add(jarname);
		            				}
		            				URL depurl = urlfile.toURI().toURL();
		            				if(set.add(depurl.toURI()))
		            				{
		            					collectManifestURLs(depurl.toURI(), set, jarnames);
		            				}
		            			}
		                    	catch (Exception e)
		                    	{
		                    		component.getLogger().warning("Error collecting manifest URLs for "+urlfile+": "+e);
		                    	}
	                    	}
	            			else if(!path.endsWith(".jar") || !jarnames.contains(getJarName(path)))
	            			{
	            				component.getLogger().info("Jar not found: "+file+", "+path);
	            			}
	               		}
	                }
	            }
		    }
		    catch(Exception e)
		    {
				component.getLogger().warning("Error collecting manifest URLs for "+url+": "+e);
		    }
	        finally
	        {
	        	try
	        	{
	        		if(jarfile!=null)
	        			jarfile.close();
	        	}
	        	catch(Exception e)
	        	{
	        	}
	        }
		}
	}
	
	/**
	 *  Add a link.
	 */
	protected void addLink(IResourceIdentifier parid, IResourceIdentifier rid)
	{
		Tuple2<IResourceIdentifier, IResourceIdentifier> link = new Tuple2<IResourceIdentifier, IResourceIdentifier>(parid, rid);
		if(!removedlinks.remove(link))
		{
			addedlinks.add(link);
		}
	}
	
	/**
	 *  Remove a link.
	 */
	protected void removeLink(IResourceIdentifier parid, IResourceIdentifier rid)
	{
		Tuple2<IResourceIdentifier, IResourceIdentifier> link = new Tuple2<IResourceIdentifier, IResourceIdentifier>(parid, rid);
		if(!addedlinks.remove(link))
		{
			removedlinks.add(link);
		}
	}
	
	/**
	 *  Get the removable links.
	 */
	public IFuture<Set<Tuple2<IResourceIdentifier, IResourceIdentifier>>> getRemovableLinks()
	{
		Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> ret = (Set<Tuple2<IResourceIdentifier, IResourceIdentifier>>)((HashSet)addedlinks).clone();
		return new Future<Set<Tuple2<IResourceIdentifier, IResourceIdentifier>>>(ret);
	}

	/**
	 *  Update from given properties.
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		Properties[] links = props.getSubproperties("link");
		
		Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> todo = new HashSet<Tuple2<IResourceIdentifier, IResourceIdentifier>>();
		
		addtodo.clear();
		for(int i=0; i<links.length; i++)
		{
			Properties pa = links[i].getSubproperty("a");
			Properties pb = links[i].getSubproperty("b");
			
			IResourceIdentifier a = ResourceIdentifier.ridFromProperties(pa, component.getId().getRoot());
			IResourceIdentifier b = ResourceIdentifier.ridFromProperties(pb, component.getId().getRoot());
			
			if(SYSTEMCPRID.equals(a))
			{
				addTopLevelURL(SUtil.toURL(b.getLocalIdentifier().getUri()));
			}
			else
			{
//				if(a==null || rootloader.getAllResourceIdentifiers().contains(a))
				if(a==null || internalgetAllResourceIdentifiers().contains(a))
				{
					todo.add(new Tuple2<IResourceIdentifier, IResourceIdentifier>(a, b));
				}
				else
				{
					addtodo.add(new Tuple2<IResourceIdentifier, IResourceIdentifier>(a, b));
				}
			}
		}
		
		for(Tuple2<IResourceIdentifier, IResourceIdentifier> link: todo)
		{
			addResourceIdentifier(link.getFirstEntity(), link.getSecondEntity(), true); // workspace?
		}
		
//		System.out.println("todo: "+todo);
//		System.out.println("addtodo: "+addtodo);
		
		return IFuture.DONE;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties props = new Properties();
		
		for(Tuple2<IResourceIdentifier, IResourceIdentifier> link: addedlinks)
		{
			Properties plink = new Properties();
			Properties a = ResourceIdentifier.ridToProperties(link.getFirstEntity(), component.getId().getRoot());
			Properties b = ResourceIdentifier.ridToProperties(link.getSecondEntity(), component.getId().getRoot());
			plink.addSubproperties("a", a);
			plink.addSubproperties("b", b);
			props.addSubproperties("link", plink);
		}
		
		return new Future<Properties>(props);		
	}
		
	/**
	 *  Check if a local url is backed by a file.
	 */
	protected URL checkUri(URI uri)
	{
		try
		{
			return checkUrl(uri.toURL());
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 *  Check if a local url is backed by a file.
	 */
	protected URL checkUrl(URL url)
	{
		URL ret = null;
		
		if("file".equals(url.getProtocol()))
		{
			File f = SUtil.getFile(url);
			if(f.exists())
			{
				try
				{
					ret = f.getCanonicalFile().toURI().toURL();
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get all managed resource identifiers inlcuding all subdependencies.
	 *  @return The resource identifiers.
	 */
	public Set<IResourceIdentifier> internalgetAllResourceIdentifiers()
	{
		Set<IResourceIdentifier> ret = new HashSet<IResourceIdentifier>();
		Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> all = internalGetResourceIdentifiers();
		for(Map.Entry<IResourceIdentifier, List<IResourceIdentifier>> entry: all.getSecondEntity().entrySet())
		{
			ret.add(entry.getKey());
			ret.addAll(entry.getValue());
		}
		ret.remove(null);
		return ret;
	}
}

