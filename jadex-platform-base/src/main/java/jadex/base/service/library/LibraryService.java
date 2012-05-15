package jadex.base.service.library;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Excluded;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	
	/** Rid support, for a rid all rids are saved that support it. */
	protected Map<IResourceIdentifier, Set<IResourceIdentifier>> ridsupport;
	
	/** The primary managed rids and the number of their support. */
	protected Map<IResourceIdentifier, Integer> managedrids;
	
	/** The global class loader (cached for speed). */
	// todo: remove!?
	protected ClassLoader	globalcl;
	
	/** The base classloader. */
	protected ClassLoader baseloader;
	
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
		this.ridsupport = new LinkedHashMap<IResourceIdentifier, Set<IResourceIdentifier>>();
		this.managedrids = new LinkedHashMap<IResourceIdentifier, Integer>();
		this.initurls = urls;
		this.baseloader = baseloader!=null? baseloader: getClass().getClassLoader();
	}
	
	
	//-------- methods --------
	
	/**
	 *  Add a new resource identifier.
	 *  @param rid The resource identifier.
	 */
	public IFuture<Void> addResourceIdentifier(final IResourceIdentifier rid)
	{
//		System.out.println("add "+rid);
		final Future<Void> ret = new Future<Void>();
		
		getClassLoader(rid, null, rid).addResultListener(
			new ExceptionDelegationResultListener<DelegationURLClassLoader, Void>(ret)
		{
			public void customResultAvailable(DelegationURLClassLoader result)
			{
				addManaged(rid);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifier(final IResourceIdentifier rid)
	{
//		System.out.println("remove "+rid);
		
		final Future<Void> ret = new Future<Void>();
		
		getClassLoader(rid, null, null).addResultListener(
			new ExceptionDelegationResultListener<DelegationURLClassLoader, Void>(ret)
		{
			public void customResultAvailable(DelegationURLClassLoader result)
			{
				boolean removed = removeManaged(rid);
				if(removed)
				{
					if(result!=null)
					{
						for(Iterator<IResourceIdentifier> it=result.getAllResourceIdentifiers().iterator(); it.hasNext(); )
						{
							IResourceIdentifier dep = it.next();
							removeSupport(dep, rid);
						}
					}
					
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
//								exception.printStackTrace();
								removeLibraryServiceListener(liscopy);
							};
						});
					}
				}
				
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifierCompletely(final IResourceIdentifier rid)
	{
//		System.out.println("remove "+rid);
		
		final Future<Void> ret = new Future<Void>();
		
		getClassLoader(rid, null, null).addResultListener(
			new ExceptionDelegationResultListener<DelegationURLClassLoader, Void>(ret)
		{
			public void customResultAvailable(DelegationURLClassLoader result)
			{
				removeManagedCompletely(rid);
				
				if(result!=null)
				{
					for(Iterator<IResourceIdentifier> it=result.getAllResourceIdentifiers().iterator(); it.hasNext(); )
					{
						IResourceIdentifier dep = it.next();
						removeSupport(dep, rid);
					}
				}

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
//							exception.printStackTrace();
							removeLibraryServiceListener(liscopy);
						};
					});
				}
				
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get all managed (directly added i.e. top-level) resource identifiers.
	 *  @return The list of resource identifiers.
	 */
	public IFuture<List<IResourceIdentifier>> getManagedResourceIdentifiers()
	{
		return new Future<List<IResourceIdentifier>>(new ArrayList<IResourceIdentifier>(managedrids.keySet()));
	}
	
	/**
	 *  Get all resource identifiers (also indirectly managed. 
	 */
	public IFuture<List<IResourceIdentifier>> getIndirectResourceIdentifiers()
	{
		List<IResourceIdentifier> ret = new ArrayList<IResourceIdentifier>();
		for(Iterator<DelegationURLClassLoader> it=classloaders.values().iterator(); it.hasNext(); )
		{
			IResourceIdentifier rid = it.next().getResourceIdentifier();
			if(!managedrids.containsKey(rid))
			{
				ret.add(rid);
			}
		}
		return new Future<List<IResourceIdentifier>>(ret);
	}
	
	/**
	 *  Get all resource identifiers (does not include urls of parent loader).
	 *  @return The list of resource identifiers.
	 */
	public IFuture<List<IResourceIdentifier>> getAllResourceIdentifiers()
	{
		List<IResourceIdentifier> ret = new ArrayList<IResourceIdentifier>();
		for(Iterator<DelegationURLClassLoader> it=classloaders.values().iterator(); it.hasNext(); )
		{
			IResourceIdentifier rid = it.next().getResourceIdentifier();
			ret.add(rid);
		}
		return new Future<List<IResourceIdentifier>>(ret);
	}
	
	/**
	 *  Add a new url.
	 *  @param url The resource identifier.
	 */
	public IFuture<IResourceIdentifier> addURL(final URL url)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		component.getServiceContainer().searchService(IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyService, IResourceIdentifier>(ret)
		{
			public void customResultAvailable(IDependencyService drs)
			{
				drs.getResourceIdentifier(url).addResultListener(
					new DelegationResultListener<IResourceIdentifier>(ret)
				{
					public void customResultAvailable(final IResourceIdentifier rid)
					{
						addResourceIdentifier(rid).addResultListener(
							new ExceptionDelegationResultListener<Void, IResourceIdentifier>(ret)
						{
							public void customResultAvailable(Void result)
							{
								ret.setResult(rid);
							}
						});
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
	public IFuture<Void> removeURL(final URL url)
	{
		final Future<Void> ret = new Future<Void>();
		component.getServiceContainer().searchService(IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyService, Void>(ret)
		{
			public void customResultAvailable(IDependencyService drs)
			{
				drs.getResourceIdentifier(url).addResultListener(
					new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
				{
					public void customResultAvailable(IResourceIdentifier result)
					{
						removeResourceIdentifier(result).addResultListener(new DelegationResultListener<Void>(ret));
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
	public IFuture<Void> removeURLCompletely(final URL url)
	{
		final Future<Void> ret = new Future<Void>();
		component.getServiceContainer().searchService(IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyService, Void>(ret)
		{
			public void customResultAvailable(IDependencyService drs)
			{
				drs.getResourceIdentifier(url).addResultListener(
					new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
				{
					public void customResultAvailable(IResourceIdentifier result)
					{
						removeResourceIdentifierCompletely(result).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
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
	 *  @param rid The resource identifier (null for current global loader).
	 *  @return the current ClassLoader
	 */
	@Excluded()
	public IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid)
	{
		final Future<ClassLoader> ret = new Future<ClassLoader>();
		
		if(rid==null)
		{
			if(globalcl==null)
			{
				DelegationURLClassLoader[] delegates = (DelegationURLClassLoader[])classloaders.values().toArray(new DelegationURLClassLoader[classloaders.size()]);
				globalcl	= new DelegationURLClassLoader(baseloader, delegates);
			}
			ret.setResult(globalcl);
		}
		else if(isLocal(rid) && getInternalNonManagedURLs().contains(rid.getLocalIdentifier().getUrl()))
		{
			ret.setResult(baseloader);
		}	
		else
		{
			// Resolve global rid or local rid from same platform.
			if(rid.getGlobalIdentifier()!=null || isLocal(rid))
			{
				getClassLoader(rid, null, null).addResultListener(new ExceptionDelegationResultListener<DelegationURLClassLoader, ClassLoader>(ret)
				{
					public void customResultAvailable(DelegationURLClassLoader result)
					{
						ret.setResult(result);
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
	 *  Get or create a classloader for an url.
	 */
	protected IFuture<DelegationURLClassLoader> getClassLoader(final IResourceIdentifier rid, 
		Map<IResourceIdentifier, List<IResourceIdentifier>> alldeps, final IResourceIdentifier support)
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
					getDependencies(rid).addResultListener(
						new ExceptionDelegationResultListener<Map<IResourceIdentifier, List<IResourceIdentifier>>, DelegationURLClassLoader>(ret)
					{
						public void customResultAvailable(Map<IResourceIdentifier, List<IResourceIdentifier>> deps)
						{
							createClassLoader(rid, deps, support).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret)
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
					createClassLoader(rid, alldeps, support).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret)
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
	protected IFuture<DelegationURLClassLoader> createClassLoader(final IResourceIdentifier rid, Map<IResourceIdentifier, List<IResourceIdentifier>> alldeps, final IResourceIdentifier support)
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
				globalcl	= null;
				addSupport(rid, support);
				ret.setResult(cl);
				
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
//							exception.printStackTrace();
							removeLibraryServiceListener(liscopy);
						};
					});
				}
			}
		});
		
		for(int i=0; i<deps.size(); i++)
		{
			IResourceIdentifier mydep = (IResourceIdentifier)deps.get(i);
//			IComponentIdentifier platcid = ((IComponentIdentifier)getServiceIdentifier().getProviderId()).getRoot();
//			IResourceIdentifier myrid = new ResourceIdentifier(new Tuple2<IComponentIdentifier, URL>(platcid, mydep), null);
			getClassLoader(mydep, alldeps, support).addResultListener(lis);
		}
		return ret;
	}
	
	/**
	 *  Get the dependent urls.
	 */
	protected IFuture<Map<IResourceIdentifier, List<IResourceIdentifier>>> getDependencies(final IResourceIdentifier rid)
	{
		final Future<Map<IResourceIdentifier, List<IResourceIdentifier>>> ret = new Future<Map<IResourceIdentifier, List<IResourceIdentifier>>>();
		
		component.getServiceContainer().searchService(IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyService, Map<IResourceIdentifier, List<IResourceIdentifier>>>(ret)
		{
			public void customResultAvailable(IDependencyService drs)
			{
				drs.loadDependencies(rid).addResultListener(new DelegationResultListener<Map<IResourceIdentifier,List<IResourceIdentifier>>>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a path.
	 *  @param path The path.
	 */
	protected void addPath(String path)
	{
		addURL(SUtil.toURL(path));
	}
	
	/**
	 *  Add support for a rid.
	 */
	protected void addSupport(IResourceIdentifier rid, IResourceIdentifier support)
	{
		if(rid!=null && support!=null)
		{
			Set<IResourceIdentifier> mysup = ridsupport.get(rid);
			if(mysup==null)
			{
				mysup = new HashSet<IResourceIdentifier>();
				ridsupport.put(rid, mysup);
			}
			mysup.add(support);
		}
	}
	
	/**
	 *  Remove support for a rid.
	 */
	protected void removeSupport(IResourceIdentifier rid, IResourceIdentifier support)
	{
		if(rid!=null && support!=null)
		{
			Set<IResourceIdentifier> mysup = ridsupport.get(rid);
			if(mysup!=null)
			{
				mysup.remove(support);
				if(mysup.size()==0)
				ridsupport.remove(rid);
			}
			else
			{
				throw new RuntimeException("No support found: "+ridsupport);
			}
		}
	}
	
	/**
	 *  Add primary management entry for a rid.
	 */
	protected void addManaged(IResourceIdentifier rid)
	{
		if(rid!=null)
		{
			Integer num = managedrids.get(rid);
			if(num==null)
			{
				managedrids.put(rid, 1);
			}
			else
			{
				managedrids.put(rid, new Integer(num.intValue()+1));
			}
		}
	}
	
	/**
	 *  Remove primary management for a rid.
	 *  @return True, if entry has to be removed.
	 */
	protected boolean removeManaged(IResourceIdentifier rid)
	{
		boolean ret = false;
		
		if(rid!=null)
		{
			Integer num = managedrids.get(rid);
			if(num!=null)
			{
				int now = num.intValue()-1;
				if(now==0)
				{
					managedrids.remove(rid);
					ret = true;
				}
				else
				{
					managedrids.put(rid, managedrids.put(rid, new Integer(num.intValue()-1)));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove primary management for a rid.
	 *  @return True, if entry has to be removed.
	 */
	protected void removeManagedCompletely(IResourceIdentifier rid)
	{
		if(rid!=null)
		{
			managedrids.remove(rid);
		}
	}

	//-------- methods --------

//	/**
//	 *  Add a new url.
//	 *  @param url The url.
//	 */
//	public void addURL(final URL url)
//	{
////		System.out.println("add "+url);
//		ILibraryServiceListener[] tmp = null;
//		
//		synchronized(this)
//		{
//			Integer refcount = (Integer)urlrefcount.get(url);
//			if(refcount != null)
//			{
//				urlrefcount.put(url, new Integer(refcount.intValue() + 1));
//			}
//			else
//			{
//				urlrefcount.put(url, new Integer(1));
//				tmp = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
//			}
//		}
//		
//		if(tmp != null)
//		{
//			final ILibraryServiceListener[] lis = tmp;
//			
//			getClassLoader(url).addResultListener(new DefaultResultListener<DelegationURLClassLoader>()
//			{
//				public void resultAvailable(DelegationURLClassLoader result)
//				{
//					// Do not notify listeners with lock held!
//					
//					for(int i=0; i<lis.length; i++)
//					{
//						final ILibraryServiceListener liscopy = lis[i];
//						lis[i].urlAdded(url).addResultListener(new IResultListener()
//						{
//							public void resultAvailable(Object result)
//							{
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								// todo: how to handle timeouts?! allow manual retry?
////								exception.printStackTrace();
//								removeLibraryServiceListener(liscopy);
//							}
//						});
//					}
//				}
//			});
//		}
//	}
	
//	/**
//	 *  Remove a url.
//	 *  @param url The url.
//	 */
//	public void removeURL(final URL url)
//	{
////		System.out.println("remove "+url);
//		ILibraryServiceListener[] tmp = null;
//		
//		synchronized(this)
//		{
//			Integer refcount = (Integer)urlrefcount.get(url);
//			if(refcount == null)
//				throw new RuntimeException("Unknown URL: "+url);
//			refcount = new Integer(refcount.intValue() - 1);
//			urlrefcount.put(url, refcount);
//			if(refcount.intValue() < 1)
//			{
//				classloadersbyname.remove(url);
//				tmp = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
//			}
//		}
//		
//		// Do not notify listeners with lock held!
//		
//		if(tmp != null)
//		{
//			final ILibraryServiceListener[] lis = tmp;
//			
//			getClassLoader(url).addResultListener(new DefaultResultListener<DelegationURLClassLoader>()
//			{
//				public void resultAvailable(DelegationURLClassLoader result)
//				{
////					updateGlobalClassLoader();
//					
//					for(int i=0; i<lis.length; i++)
//					{
//						final ILibraryServiceListener liscopy = lis[i];
//						lis[i].urlRemoved(url).addResultListener(new IResultListener()
//						{
//							public void resultAvailable(Object result)
//							{
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								// todo: how to handle timeouts?! allow manual retry?
//		//						exception.printStackTrace();
//								removeLibraryServiceListener(liscopy);
//							}
//						});
//					}
//				}
//			});
//		}
//	}
	
//	/**
//	 *  Remove a url completely (all references).
//	 *  @param url The url.
//	 */
//	public void removeURLCompletely(URL url)
//	{
////		System.out.println("rem all "+url);
//		ILibraryServiceListener[] lis = null;
//		synchronized(this)
//		{
//			urlrefcount.remove(url);
//			classloadersbyname.remove(url);
////			updateGlobalClassLoader();
//			lis = (ILibraryServiceListener[])listeners.toArray(new ILibraryServiceListener[listeners.size()]);
//		}
//		
//		// Do not notify listeners with lock held!
//		if(lis != null)
//		{
//			for(int i=0; i<lis.length; i++)
//			{
//				final ILibraryServiceListener liscopy = lis[i];
//				lis[i].urlRemoved(url).addResultListener(new IResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						// todo: how to handle timeouts?! allow manual retry?
////						exception.printStackTrace();
//						removeLibraryServiceListener(liscopy);
//					}
//				});
//			}
//		}
//	}
	
//	/**
//	 *  Get all managed entries as URLs.
//	 *  @return url The urls.
//	 */
//	public synchronized IFuture<List<URL>> getURLs()
//	{
//		return new Future<List<URL>>(new ArrayList<URL>(libcl.getAllURLs()));
//	}
//
//	/**
//	 *  Get other contained (but not directly managed) URLs.
//	 *  @return The list of urls.
//	 */
//	public synchronized IFuture<List<URL>> getNonManagedURLs()
//	{
//		return new Future<List<URL>>(SUtil.getClasspathURLs(libcl));	
//	}
//	
//	/**
//	 *  Get all urls (managed and non-managed).
//	 *  @return The list of urls.
//	 */
//	public IFuture<List<URL>> getAllURLs()
//	{
//		List<URL> ret = new ArrayList<URL>();
//		ret.addAll(libcl.getAllURLs());
//		ret.addAll(SUtil.getClasspathURLs(libcl));
//		ret.addAll(SUtil.getClasspathURLs(null));
//		return new Future<List<URL>>(ret);
//	}
//	
//	/** 
//	 *  Returns the current ClassLoader
//	 *  @return the current ClassLoader
//	 */
//	public ClassLoader getClassLoader()
//	{
//		return libcl;
//	}

//	/**
//	 *  Load a class given a class identifier.
//	 *  @param clid The class identifier.
//	 *  @return The class for the identifier.
//	 */
//	public IFuture<Class> loadClass(final IClassIdentifier clid)
//	{
//		final Future<Class> ret = new Future<Class>();
//		IResourceIdentifier rid = clid.getResourceIdentifier();
//		getClassLoader(rid).addResultListener(component.createResultListener(new ExceptionDelegationResultListener<ClassLoader, Class>(ret)
//		{
//			public void customResultAvailable(ClassLoader cl)
//			{
//				try
//				{
//					ret.setResult(cl.loadClass(clid.getClassname()));
//				}
//				catch(Exception e)
//				{
//					ret.setException(e);
//				}
//			}
//		}));
//		return ret;
//	}
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		final Future<Void>	ret	= new Future<Void>();
		if(initurls!=null)
		{
			CounterResultListener<IResourceIdentifier> lis = new CounterResultListener<IResourceIdentifier>(initurls.length,
				new DelegationResultListener<Void>(ret)
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
//								ret.setResult(getServiceIdentifier());
						}
					});
				}
			});
			for(int i=0; i<initurls.length; i++)
			{
				addURL(SUtil.toURL(initurls[i])).addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
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

	

	/** 
	 *  Helper method for validating jar-files
	 *  @param file the jar-file
	 * /
	private boolean checkJar(File file)
	{
		try
		{
			JarFile jarFile = new JarFile(file);
		}
		catch(IOException e)
		{
			return false;
		}

		return true;
	}*/

	/** 
	 *  Fires the class-path-added event
	 *  @param path the new class path
	 * /
	protected synchronized void fireURLAdded(URL url)
	{
//		System.out.println("listeners: "+listeners);
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.urlAdded(url);
		}
	}*/

	/** 
	 *  Fires the class-path-removed event
	 *  @param path the removed class path
	 * /
	protected synchronized void fireURLRemoved(URL url)
	{
		for(Iterator it = listeners.iterator(); it.hasNext();)
		{
			ILibraryServiceListener listener = (ILibraryServiceListener)it.next();
			listener.urlRemoved(url);
		}
	}*/
	
//	/**
//	 *  Convert a file/string/url.
//	 */
//	public static URL toURL(Object url)
//	{
//		URL	ret	= null;
//		boolean	jar	= false;
//		if(url instanceof String)
//		{
//			String	string	= (String) url;
//			if(string.startsWith("file:") || string.startsWith("jar:file:"))
//			{
//				try
//				{
//					string	= URLDecoder.decode(string, "UTF-8");
//				}
//				catch(UnsupportedEncodingException e)
//				{
//					e.printStackTrace();
//				}
//			}
//			
//			jar	= string.startsWith("jar:file:");
//			url	= jar ? new File(string.substring(9))
//				: string.startsWith("file:") ? new File(string.substring(5)) : null;
//			
//			
//			if(url==null)
//			{
//				File file	= new File(string);
//				if(file.exists())
//				{
//					url	= file;
//				}
//				else
//				{
//					file	= new File(System.getProperty("user.dir"), string);
//					if(file.exists())
//					{
//						url	= file;
//					}
//					else
//					{
//						try
//						{
//							url	= new URL(string);
//						}
//						catch (MalformedURLException e)
//						{
//							throw new RuntimeException(e);
//						}
//					}
//				}
//			}
//		}
//		
//		if(url instanceof URL)
//		{
//			ret	= (URL)url;
//		}
//		else if(url instanceof File)
//		{
//			try
//			{
//				String	abs	= ((File)url).getAbsolutePath();
//				String	rel	= SUtil.convertPathToRelative(abs);
//				ret	= abs.equals(rel) ? new File(abs).toURI().toURL()
//					: new File(System.getProperty("user.dir"), rel).toURI().toURL();
//				if(jar)
//				{
//					if(ret.toString().endsWith("!"))
//						ret	= new URL("jar:"+ret.toString()+"/");	// Add missing slash in jar url.
//					else
//						ret	= new URL("jar:"+ret.toString());						
//				}
//			}
//			catch (MalformedURLException e)
//			{
//				throw new RuntimeException(e);
//			}
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the non-managed classpath entries as strings.
//	 *  @return Classpath entries as a list of strings.
//	 */
//	public IFuture<List<String>> getURLStrings()
//	{
//		final Future<List<String>> ret = new Future<List<String>>();
//		
//		getURLs().addResultListener(new IResultListener<List<URL>>()
//		{
//			public void resultAvailable(List<URL> result)
//			{
////				List urls = (List)result;
//				List<String> tmp = new ArrayList<String>();
//				// todo: hack!!!
//				
//				for(Iterator<URL> it=result.iterator(); it.hasNext(); )
//				{
//					URL	url	= it.next();
//					tmp.add(url.toString());
//					
////					String file = url.getFile();
////					File f = new File(file);
////					
////					// Hack!!! Above code doesnt handle relative url paths. 
////					if(!f.exists())
////					{
////						File	newfile	= new File(new File("."), file);
////						if(newfile.exists())
////						{
////							f	= newfile;
////						}
////					}
////					ret.add(f.getAbsolutePath());
//				}
//				
//				ret.setResult(tmp);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				ret.setException(exception);
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the non-managed classpath entries.
//	 *  @return Classpath entries as a list of strings.
//	 */
//	public IFuture<List<String>> getNonManagedURLStrings()
//	{
//		final Future<List<String>> ret = new Future<List<String>>();
//		
//		getNonManagedURLs().addResultListener(new IResultListener<List<URL>>()
//		{
//			public void resultAvailable(List<URL> result)
//			{
////				List urls = (List)result;
//				List<String> tmp = new ArrayList<String>();
//				// todo: hack!!!
//				
//				for(Iterator<URL> it=result.iterator(); it.hasNext(); )
//				{
//					URL	url	= it.next();
//					tmp.add(url.toString());
//					
////					String file = url.getFile();
////					File f = new File(file);
////					
////					// Hack!!! Above code doesnt handle relative url paths. 
////					if(!f.exists())
////					{
////						File	newfile	= new File(new File("."), file);
////						if(newfile.exists())
////						{
////							f	= newfile;
////						}
////					}
////					ret.add(f.getAbsolutePath());
//				}
//				
//				ret.setResult(tmp);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				ret.setException(exception);
//			}
//		});
//		
//		return ret;
//		
////		java.util.List	ret	= new ArrayList();
////
//////		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
////		// todo: hack
//////		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class).get(new ThreadSuspendable());
////		ClassLoader	cl	= ls.getClassLoader();
////		
////		List cps = SUtil.getClasspathURLs(cl!=null ? cl.getParent() : null);	// todo: classpath?
////		for(int i=0; i<cps.size(); i++)
////		{
////			URL	url	= (URL)cps.get(i);
////			ret.add(url.toString());
////			
//////			String file = url.getFile();
//////			File f = new File(file);
//////			
//////			// Hack!!! Above code doesnt handle relative url paths. 
//////			if(!f.exists())
//////			{
//////				File	newfile	= new File(new File("."), file);
//////				if(newfile.exists())
//////				{
//////					f	= newfile;
//////				}
//////			}
//////			ret.add(f.getAbsolutePath());
////		}
////		
////		return ret;
//	}
	


	
//	/**
//	 *  Get a class definition.
//	 *  @param name The class name.
//	 *  @return The class definition as byte array.
//	 */
//	public IFuture<byte[]> getClassDefinition(String name)
//	{
//		Future<byte[]> ret = new Future<byte[]>();
//		
//		Class clazz = SReflect.findClass0(name, null, libcl);
//		if(clazz!=null)
//		{
//			try
//			{
//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
//				ObjectOutputStream oos = new ObjectOutputStream(bos);
//				oos.writeObject(clazz);
//				oos.close();
//				bos.close();
//				byte[] data = bos.toByteArray();
//				ret.setResult(data);
//			}
//			catch(Exception e)
//			{
//				ret.setResult(null);
//			}
//		}
//		else
//		{
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
	
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
	
//	/**
//	 *  Update the global class loader.
//	 *  
//	 *  hack: should be removed
//	 */
//	public void updateGlobalClassLoader()
//	{
//		DelegationURLClassLoader[] delegates = (DelegationURLClassLoader[])classloaders.values().toArray(new DelegationURLClassLoader[classloaders.size()]);
//		
//		/* $if !android $ */
//		this.libcl = new DelegationURLClassLoader(ClassLoader.getSystemClassLoader(), delegates);
//		/* $else $
//		this.libcl = new DelegationClassLoader(LibraryService.class.getClassLoader(), urls);
//		$endif $ */
//		
//		System.out.println("update global: "+delegates.length);
//	}
	
//	/**
//	 * 
//	 */
//	protected IFuture<IResourceIdentifier> createResourceIdentifier(URL url)
//	{
//		Tuple2<IComponentIdentifier, URL> lid = new Tuple2<IComponentIdentifier, URL>(cid, url);
//		String gid = mh.getArtifactDescription(url);
//		ResourceIdentifier rid = new ResourceIdentifier(lid, gid);
//		return new Future<IResourceIdentifier>(rid);
//	}

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

