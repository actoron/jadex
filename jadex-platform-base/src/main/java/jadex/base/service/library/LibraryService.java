package jadex.base.service.library;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.bridge.service.types.settings.ISettingsService;
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
import jadex.commons.future.IResultListener;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 *  Library service for loading classpath elements.
 */
@Service(ILibraryService.class)
public class LibraryService	implements ILibraryService, IPropertiesProvider
{
	//-------- constants --------
	
	/** 
	 * The (standard) Library service name.
	 */
	public static final String LIBRARY_SERVICE = "library_service";
	
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
	
	/** The map of managed resources (url (for local case) -> delegate loader). */
	protected Map<IResourceIdentifier, DelegationURLClassLoader> classloaders;
	
	/** The dependencies. */
	protected Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> rids;
	
	/** The base classloader. */
	protected ClassLoader baseloader;
	
	/** The delegation root loader. */
	protected DelegationURLClassLoader rootloader;
	
	/** The non-managed urls (cached for speed). */
	protected Set<URL>	nonmanaged;
	
	
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
		this.initurls = urls!=null? urls.clone(): urls;
		this.baseloader = baseloader!=null? baseloader: getClass().getClassLoader();
		this.rootloader = new DelegationURLClassLoader(this.baseloader, null);
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new resource identifier.
	 *  @param parid The optional parent rid.
	 *  @param rid The resource identifier.
	 */
	public IFuture<IResourceIdentifier> addResourceIdentifier(final IResourceIdentifier parid,
		IResourceIdentifier rid, final boolean workspace)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		
		getDependencies(rid, workspace).addResultListener(new ExceptionDelegationResultListener
			<Tuple2<IResourceIdentifier,Map<IResourceIdentifier,List<IResourceIdentifier>>>, IResourceIdentifier>(ret)
		{
			public void customResultAvailable(Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> deps)
			{
				final IResourceIdentifier rid = deps.getFirstEntity();
//				System.out.println("add end "+rid);
				
				getClassLoader(rid, deps.getSecondEntity(), parid, workspace).addResultListener(
					new ExceptionDelegationResultListener<DelegationURLClassLoader, IResourceIdentifier>(ret)
				{
					public void customResultAvailable(final DelegationURLClassLoader chil)
					{
						ret.setResult(rid);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		
//		System.out.println("current: "+SUtil.arrayToString(rootloader.getAllResourceIdentifiers()));
		
		return ret;
	}
	
	/**
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifier(IResourceIdentifier parid, final IResourceIdentifier rid)
	{
//		System.out.println("remove "+rid);
		removeSupport(rid, parid);
		return IFuture.DONE;
	}
	
//	/**
//	 *  Get all managed (directly added i.e. top-level) resource identifiers.
//	 *  @return The list of resource identifiers.
//	 */
//	public IFuture<List<IResourceIdentifier>> getManagedResourceIdentifiers()
//	{
//		return new Future<List<IResourceIdentifier>>(new ArrayList<IResourceIdentifier>(rootloader.getAllResourceIdentifiers()));
//	}
//	
//	/**
//	 *  Get all resource identifiers (also indirectly managed). 
//	 */
//	public IFuture<List<IResourceIdentifier>> getIndirectResourceIdentifiers()
//	{
//		List<IResourceIdentifier> ret = new ArrayList<IResourceIdentifier>();
//		for(Iterator<DelegationURLClassLoader> it=classloaders.values().iterator(); it.hasNext(); )
//		{
//			IResourceIdentifier rid = it.next().getResourceIdentifier();
//			List<IResourceIdentifier> toprids = rootloader.getDelegateResourceIdentifiers();
//			if(!toprids.contains(rid))
//			{
//				ret.add(rid);
//			}
//		}
//		return new Future<List<IResourceIdentifier>>(ret);
//	}
	
	/**
	 *  Get all resource identifiers (does not include urls of parent loader).
	 *  @return The list of resource identifiers.
	 */
	public IFuture<List<IResourceIdentifier>> getAllResourceIdentifiers()
	{
		return new Future<List<IResourceIdentifier>>(new ArrayList<IResourceIdentifier>(rootloader.getAllResourceIdentifiers()));
	}
	
	/**
	 *  Get the rids.
	 */
	public IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> getResourceIdentifiers()
	{
		if(rids==null)
		{
			Map<IResourceIdentifier, List<IResourceIdentifier>> deps = new HashMap<IResourceIdentifier, List<IResourceIdentifier>>();
			
			List<IResourceIdentifier> todo = new ArrayList<IResourceIdentifier>();
			todo.add(rootloader.getResourceIdentifier());
			
			while(!todo.isEmpty())
			{
				IResourceIdentifier clrid = todo.remove(0);
				DelegationURLClassLoader cl = clrid==null? rootloader: classloaders.get(clrid);
				List<IResourceIdentifier> mydeps = cl.getDelegateResourceIdentifiers();
				deps.put(clrid, mydeps);
				for(IResourceIdentifier rid: mydeps)
				{
					if(!deps.containsKey(rid))
					{
						todo.add(rid);
					}
				}
			}
			
			rids = new Tuple2<IResourceIdentifier, Map<IResourceIdentifier,List<IResourceIdentifier>>>(rootloader.getResourceIdentifier(), deps);
		}
		
		return new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(rids);
	}
	
	/**
	 *  Add a new url.
	 *  @param url The resource identifier.
	 */
	public IFuture<IResourceIdentifier> addURL(final IResourceIdentifier parid, final URL url)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
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
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getNonManagedURLs()
	{
		return new Future<List<URL>>(new ArrayList<URL>(getInternalNonManagedURLs()));
	}
	
	/**
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The set of urls.
	 */
	protected Set<URL>	getInternalNonManagedURLs()
	{
		if(nonmanaged==null)
		{
			nonmanaged	= new LinkedHashSet<URL>();
			collectClasspathURLs(baseloader, nonmanaged);
		}
		return nonmanaged;
	}
	
	/**
	 *  Get all urls (managed, indirect and non-managed from parent loader).
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getAllURLs()
	{
		final Future<List<URL>> ret = new Future<List<URL>>();
		getAllResourceIdentifiers().addResultListener(new ExceptionDelegationResultListener<List<IResourceIdentifier>, List<URL>>(ret)
		{
			public void customResultAvailable(List<IResourceIdentifier> result)
			{
				final List<URL> res = new ArrayList<URL>();
				for(int i=0; i<result.size(); i++)
				{
					res.add(result.get(i).getLocalIdentifier().getUrl());
				}
				
				res.addAll(getInternalNonManagedURLs());
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
		final Future<ClassLoader> ret = new Future<ClassLoader>();
		
		if(rid==null)
		{
			ret.setResult(rootloader);
//			System.out.println("root classloader: "+rid);
		}
		else if(isLocal(rid) && getInternalNonManagedURLs().contains(rid.getLocalIdentifier().getUrl()))
		{
			ret.setResult(baseloader);
//			System.out.println("base classloader: "+rid);
		}	
		else
		{
			// Resolve global rid or local rid from same platform.
			if(rid.getGlobalIdentifier()!=null || isLocal(rid))
			{
				getClassLoader(rid, null, rootloader.getResourceIdentifier(), workspace).addResultListener(new ExceptionDelegationResultListener<DelegationURLClassLoader, ClassLoader>(ret)
				{
					public void customResultAvailable(DelegationURLClassLoader result)
					{
						ret.setResult(result);
//						System.out.println("custom classloader: "+result.hashCode()+" "+rid);
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
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
		
		component.getServiceContainer().searchService(IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
		
		if(isLocal(rid) && getInternalNonManagedURLs().contains(rid.getLocalIdentifier().getUrl()))
		{
			ret	= new Future<DelegationURLClassLoader>((DelegationURLClassLoader)null);
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
//					System.out.println("getdeps in getcl: "+rid);
					getDependencies(rid, workspace).addResultListener(
						new ExceptionDelegationResultListener<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>, DelegationURLClassLoader>(ret)
					{
						public void customResultAvailable(Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> deps)
						{
							createClassLoader(rid, deps.getSecondEntity(), support, workspace).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret)
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
		Map<IResourceIdentifier, List<IResourceIdentifier>> alldeps, final IResourceIdentifier support, final boolean workspace)
	{
		// Class loaders shouldn't be created for local URLs, which are already available in base class loader.
		assert rid.getLocalIdentifier()==null || !isLocal(rid) || !getInternalNonManagedURLs().contains(rid.getLocalIdentifier().getUrl());
		
		final Future<DelegationURLClassLoader> ret = new Future<DelegationURLClassLoader>();
//		final URL url = rid.getLocalIdentifier().getSecondEntity();
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
				
				DelegationURLClassLoader[] delegates = (DelegationURLClassLoader[])result.toArray(new DelegationURLClassLoader[result.size()]);
				DelegationURLClassLoader cl = new DelegationURLClassLoader(rid, baseloader, delegates);
				classloaders.put(rid, cl);
//				System.out.println("createClassLoader() put: "+rid);
				addSupport(rid, support);
				ret.setResult(cl);
			}
		});
		
		for(int i=0; i<deps.size(); i++)
		{
			IResourceIdentifier mydep = (IResourceIdentifier)deps.get(i);
//			IComponentIdentifier platcid = ((IComponentIdentifier)getServiceIdentifier().getProviderId()).getRoot();
//			IResourceIdentifier myrid = new ResourceIdentifier(new Tuple2<IComponentIdentifier, URL>(platcid, mydep), null);
			getClassLoader(mydep, alldeps, support, workspace).addResultListener(lis);
		}
		return ret;
	}
	
	/**
	 *  Get the dependent urls.
	 */
	protected IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> 
		getDependencies(final IResourceIdentifier rid, final boolean workspace)
	{
		final Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> ret = new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>();
		
		component.getServiceContainer().searchService(IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
	protected void addSupport(IResourceIdentifier rid, IResourceIdentifier support)
	{
		if(rid!=null)
		{
			DelegationURLClassLoader pacl = support==null? rootloader: (DelegationURLClassLoader)classloaders.get(support);
			DelegationURLClassLoader cl = (DelegationURLClassLoader)classloaders.get(rid);
			pacl.addDelegateClassLoader(cl);
			if(cl.addParentClassLoader(pacl))
			{
				rids = null;
				notifyAdditionListeners(rid);
			}
		}
	}
	
	/**
	 *  Remove support for a rid.
	 */
	protected void removeSupport(IResourceIdentifier rid, IResourceIdentifier support)
	{
		if(rid!=null)
		{
			DelegationURLClassLoader pacl = support==null? rootloader: (DelegationURLClassLoader)classloaders.get(support);
			DelegationURLClassLoader cl = (DelegationURLClassLoader)classloaders.get(rid);
			pacl.removeDelegateClassLoader(cl);
			
			// If last support, delete removeSupport to children.
			if(cl.removeParentClassLoader(pacl))
			{
				rids = null;
				notifyRemovalListeners(rid);
				
				DelegationURLClassLoader[] dels = cl.getDelegateClassLoaders();
				for(DelegationURLClassLoader del: dels)
				{
					removeSupport(del.getResourceIdentifier(), rid);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	protected void notifyAdditionListeners(final IResourceIdentifier rid)
	{
		// Do not notify listeners with lock held!
		ILibraryServiceListener[] lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		for(int i=0; i<lis.length; i++)
		{
			final ILibraryServiceListener liscopy = lis[i];
			lis[i].resourceIdentifierAdded(rid).addResultListener(new IResultListener<Void>()
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
	 * 
	 */
	protected void notifyRemovalListeners(final IResourceIdentifier rid)
	{
		// Do not notify listeners with lock held!
		ILibraryServiceListener[] lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
		for(int i=0; i<lis.length; i++)
		{
			final ILibraryServiceListener liscopy = lis[i];
			lis[i].resourceIdentifierRemoved(rid).addResultListener(new IResultListener<Void>()
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
	
//	/**
//	 *  Get the jar for a rid. 
//	 *  @param rid The rid.
//	 *  @return The jar.
//	 */
//	public IFuture<IInputConnection> getJar(IResourceIdentifier rid)
//	{
//		final Future<IInputConnection> ret = new Future<IInputConnection>();
//		
//		boolean fin = false;
//		
//		// Has rid a local file desc on this platform?
//		if(isLocal(rid))
//		{
//			URL url = rid.getLocalIdentifier().getUrl();
//			File f = new File(url.getFile());
//			if(url.getFile().endsWith(".jar") && f.exists())
//			{
//				try
//				{
//					FileInputStream fis = new FileInputStream(f);
//					ServiceOutputConnection soc = new ServiceOutputConnection();
//					ret.setResult(soc.getInputConnection());
//					soc.writeFromInputStream(fis, component.getExternalAccess());
//					fin = true;
//				}
//				catch(IOException e)
//				{
//				}
//			}
//		}
//	
//		if(!fin)
//			ret.setException(new RuntimeException("Could not find resource: "+rid));
//		
////		if(!fin && rid.getGlobalIdentifier()!=null)
////		{
////			// todo: get local location from dependency service? 
////		}
//		
//		return ret;
//	}
	
	/**
	 * 
	 */
	protected IFuture<IResourceIdentifier> internalGetResourceIdentifier(final URL url)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		
		component.getServiceContainer().searchService(IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(component.createResultListener(new ExceptionDelegationResultListener<IDependencyService, IResourceIdentifier>(ret)
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
				component.getServiceContainer().searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ISettingsService, Void>(ret)
				{
					public void customResultAvailable(ISettingsService settings)
					{
						settings.registerPropertiesProvider(LIBRARY_SERVICE, LibraryService.this)
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
					public void exceptionOccurred(Exception exception)
					{
						// No settings service: ignore
						ret.setResult(null);
					}
				});
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
		component.getServiceContainer().searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<ISettingsService, Void>(saved)
		{
			public void customResultAvailable(ISettingsService settings)
			{
				settings.deregisterPropertiesProvider(LIBRARY_SERVICE)
					.addResultListener(new DelegationResultListener<Void>(saved));
			}
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore
				saved.setResult(null);
			}
		});
		
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
	 *  Update from given properties.
	 */
	public IFuture<Void> setProperties(Properties props)
	{
//		// Do not remove existing urls?
//		// todo: treat arguments and 
//		// Remove existing urls
////		libcl = new DelegationURLClassLoader(ClassLoader.getSystemClassLoader(), initurls);
//		
//		// todo: fix me / does not work getClassLoader is async
//		if(initurls!=null)
//		{
//			for(int i=0; i<initurls.length; i++)
//			{
//				addURL(toURL(initurls[i]));
//			}
//		}
////		updateGlobalClassLoader();
//		
//		// Add new urls.
//		Property[]	entries	= props.getProperties("entry");
//		for(int i=0; i<entries.length; i++)
//		{
//			addPath(entries[i].getValue());
//		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
//		String[]	entries;
//		if(libcl != null)
//		{
//			synchronized(this)
//			{
//				List urls = new ArrayList(libcl.getAllURLs());
//				entries	= new String[urls.size()];
//				for(int i=0; i<entries.length; i++)
//				{
//					
//					String	url	= urls.get(i).toString();
//					File	file	= urlToFile(url.toString());
//					if(file!=null)
//					{
//						String	filename	= SUtil.convertPathToRelative(file.getAbsolutePath());
//						String fileurl;
//						try
//						{
//							// URI wouldn't allow relative names, so pretend its an absolute path.
//							fileurl = "file:"+new URI("file", null, "/"+filename.replace('\\', '/'), null).toURL().toString().substring(6);
//						}
//						catch(Exception e)
//						{
//							fileurl	= "file:"+filename.replace('\\', '/');
//						}
//						if(url.startsWith("jar:file:"))
//						{
//							entries[i]	= "jar:" + fileurl;
//						}
//						else
//						{
//							entries[i]	= fileurl;
//						}
//						if(url.endsWith("!/") && entries[i].endsWith("!"))
//							entries[i]	+= "/";	// Stripped by new File(...).
//					}
//					else
//					{
//						entries[i]	= url;
//					}
//				}
//			}
//		}
//		else
//		{
//			entries = new String[0];
//		}
//		
//		Properties props	= new Properties();
//		for(int i=0; i<entries.length; i++)
//		{
//			props.addProperty(new Property("entry", entries[i]));
//		}
//		return new Future<Properties>(props);		
		return new Future<Properties>(new Properties());
	}
	
	/**
	 *  Collect all URLs belonging to a class loader.
	 */
	protected void	collectClasspathURLs(ClassLoader classloader, Set<URL> set)
	{
		assert classloader!=null;
		
		if(classloader.getParent()!=null)
		{
			collectClasspathURLs(classloader.getParent(), set);
		}
		
		if(classloader instanceof URLClassLoader)
		{
			URL[] urls = ((URLClassLoader)classloader).getURLs();
			for(int i = 0; i < urls.length; i++)
			{
				set.add(urls[i]);
				collectManifestURLs(urls[i], set);
			}
		}
	}
	
	/**
	 *  Collect all URLs as specified in a manifest.
	 */
	protected void	collectManifestURLs(URL url, Set<URL> set)
	{
		File	file	= SUtil.urlToFile(url.toString());
		if(file!=null && file.exists() && !file.isDirectory())	// Todo: load manifest also from directories!?
		{
	        try 
	        {
	            JarFile	jarfile	= new JarFile(file);
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
		            				URL depurl = urlfile.toURI().toURL();
		            				set.add(depurl);
		            				collectManifestURLs(depurl, set);
		            			}
		                    	catch (Exception e)
		                    	{
		                    		component.getLogger().warning("Error collecting manifest URLs for "+urlfile+": "+e);
		                    	}
	                    	}
	            			else
	            			{
	            				component.getLogger().warning("Jar not found: "+file+", "+path);
	            			}
	               		}
	                }
	            }
		    }
		    catch(Exception e)
		    {
				component.getLogger().warning("Error collecting manifest URLs for "+url+": "+e);
		    }
		}
	}
	
	/**
	 *  Test if a rid is local to this platform.
	 */
	protected boolean	isLocal(IResourceIdentifier rid)
	{
		return rid.getLocalIdentifier()!=null && rid.getLocalIdentifier().getComponentIdentifier().equals(component.getComponentIdentifier().getRoot());		
	}
}

