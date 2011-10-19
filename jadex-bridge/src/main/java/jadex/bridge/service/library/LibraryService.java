package jadex.bridge.service.library;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.Excluded;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
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

/**
 *  Library service for loading classpath elements.
 */
public class LibraryService extends BasicService implements ILibraryService, IPropertiesProvider
{
	//-------- attributes --------
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** LibraryService listeners. */
	protected Set<ILibraryServiceListener> listeners;

	/** The init urls. */
	protected Object[] initurls;

	/** The map of managed resources (url (for local case) -> delegate loader). */
	protected Map<IResourceIdentifier, DelegationURLClassLoader> classloaders;
	
	/** Rid support, for a rid all rids are saved that support it. */
	protected Map<IResourceIdentifier, Set<IResourceIdentifier>> ridsupport;
	
	/** The primary managed rids and the number of their support. */
	protected Map<IResourceIdentifier, Integer> managedrids;
	
	//-------- constructors --------
	
	/** 
	 *  Creates a new LibraryService.
	 */ 
	public LibraryService(IServiceProvider provider)
	{
		this(null, provider);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls, IServiceProvider provider)
	{
		this(urls, provider, null);
	}
	
	/** 
	 *  Creates a new LibraryService.
	 *  @param urls	Urls may be specified as java.net.URLs, java.io.Files or java.lang.Strings.
	 *  	Strings are interpreted as relative files (relative to current directory),
	 *  	absolute files or URLs (whatever can be found). 
	 */ 
	public LibraryService(Object[] urls, IServiceProvider provider, Map properties)
	{
		super(provider.getId(), ILibraryService.class, properties);
		
		this.classloaders = new HashMap<IResourceIdentifier, DelegationURLClassLoader>();
		this.listeners	= new LinkedHashSet<ILibraryServiceListener>();
		this.ridsupport = new LinkedHashMap<IResourceIdentifier, Set<IResourceIdentifier>>();
		this.managedrids = new LinkedHashMap<IResourceIdentifier, Integer>();
		this.initurls = urls;
		this.provider = provider;

		if(urls!=null)
		{
			for(int i=0; i<urls.length; i++)
			{
				addURL(SUtil.toURL(urls[i]));
			}
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new resource identifier.
	 *  @param rid The resource identifier.
	 */
	public IFuture<Void> addResourceIdentifier(final IResourceIdentifier rid)
	{
		System.out.println("add "+rid);
		final Future<Void> ret = new Future<Void>();
		
		getClassLoader(rid, null, rid).addResultListener(
			new ExceptionDelegationResultListener<DelegationURLClassLoader, Void>(ret)
		{
			public void customResultAvailable(DelegationURLClassLoader result)
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
	 *  Remove a resource identifier.
	 *  @param url The resource identifier.
	 */
	public IFuture<Void> removeResourceIdentifier(final IResourceIdentifier rid)
	{
		System.out.println("remove "+rid);
		
		final Future<Void> ret = new Future<Void>();
		
		getClassLoader(rid, null, null).addResultListener(
			new ExceptionDelegationResultListener<DelegationURLClassLoader, Void>(ret)
		{
			public void customResultAvailable(DelegationURLClassLoader result)
			{
				boolean removed = removeManaged(rid);
				if(removed)
				{
					for(Iterator<IResourceIdentifier> it=result.getAllResourceIdentifiers().iterator(); it.hasNext(); )
					{
						IResourceIdentifier dep = it.next();
						removeSupport(dep, rid);
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
		System.out.println("remove "+rid);
		
		final Future<Void> ret = new Future<Void>();
		
		getClassLoader(rid, null, null).addResultListener(
			new ExceptionDelegationResultListener<DelegationURLClassLoader, Void>(ret)
		{
			public void customResultAvailable(DelegationURLClassLoader result)
			{
				removeManagedCompletely(rid);
				for(Iterator<IResourceIdentifier> it=result.getAllResourceIdentifiers().iterator(); it.hasNext(); )
				{
					IResourceIdentifier dep = it.next();
					removeSupport(dep, rid);
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
	public IFuture<Void> addURL(final URL url)
	{
		final Future<Void> ret = new Future<Void>();
		SServiceProvider.getService(provider, IDependencyResolverService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyResolverService, Void>(ret)
		{
			public void customResultAvailable(IDependencyResolverService drs)
			{
				drs.getResourceIdentifier(url).addResultListener(
					new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
				{
					public void customResultAvailable(IResourceIdentifier result)
					{
						addResourceIdentifier(result).addResultListener(new DelegationResultListener<Void>(ret));
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
		SServiceProvider.getService(provider, IDependencyResolverService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyResolverService, Void>(ret)
		{
			public void customResultAvailable(IDependencyResolverService drs)
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
		SServiceProvider.getService(provider, IDependencyResolverService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyResolverService, Void>(ret)
		{
			public void customResultAvailable(IDependencyResolverService drs)
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
//		return new Future<List<URL>>(SUtil.getClasspathURLs(libcl));
		List<URL> nonurls = new ArrayList<URL>(SUtil.getClasspathURLs(getClass().getClassLoader()));
		return new Future<List<URL>>(nonurls);
	}
	
	/**
	 *  Get all urls (managed , indirect and non-managed from parent loader).
	 *  @return The list of urls.
	 */
	public IFuture<List<URL>> getAllURLs()
	{
		final Future<List<URL>> ret = new Future<List<URL>>();
		getAllResourceIdentifiers().addResultListener(new ExceptionDelegationResultListener<List<IResourceIdentifier>, List<URL>>(ret)
		{
			public void customResultAvailable(List<IResourceIdentifier> result)
			{
				List<URL> res = new ArrayList<URL>();
				for(int i=0; i<result.size(); i++)
				{
					res.add(result.get(i).getLocalIdentifier().getSecondEntity());
				}
				res.addAll(SUtil.getClasspathURLs(getClass().getClassLoader()));
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
			DelegationURLClassLoader[] delegates = (DelegationURLClassLoader[])classloaders.values().toArray(new DelegationURLClassLoader[classloaders.size()]);
			ret.setResult(new DelegationURLClassLoader(ClassLoader.getSystemClassLoader(), delegates));
		}
		else
		{
			getClassLoader(rid, null, null).addResultListener(new ExceptionDelegationResultListener<DelegationURLClassLoader, ClassLoader>(ret)
			{
				public void customResultAvailable(DelegationURLClassLoader result)
				{
					ret.setResult(result);
				}
			});
		}
		
		return ret;
	}
	
	/** 
	 *  Get the resource identifier for an url.
	 *  @return The resource identifier.
	 */
	@Excluded()
	public IFuture<IResourceIdentifier> getResourceIdentifier(final URL url)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		
		SServiceProvider.getService(provider, IDependencyResolverService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyResolverService, IResourceIdentifier>(ret)
		{
			public void customResultAvailable(IDependencyResolverService drs)
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
		System.out.println("getClassLoader(): "+rid);
//		final URL url = rid.getLocalIdentifier().getSecondEntity();
		
		final Future<DelegationURLClassLoader> ret = new Future<DelegationURLClassLoader>();
		DelegationURLClassLoader cl = (DelegationURLClassLoader)classloaders.get(rid);
		
		if(cl!=null)
		{
			addSupport(rid, support);
			ret.setResult(cl);
		}
		else
		{
			if(alldeps==null)
			{
				getDependencies(rid).addResultListener(
					new ExceptionDelegationResultListener<Map<IResourceIdentifier, List<IResourceIdentifier>>, DelegationURLClassLoader>(ret)
				{
					public void customResultAvailable(Map<IResourceIdentifier, List<IResourceIdentifier>> deps)
					{
						createClassLoader(rid, deps, support).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret));
					}
				});
			}
			else
			{
				createClassLoader(rid, alldeps, support).addResultListener(new DelegationResultListener<DelegationURLClassLoader>(ret));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a new classloader.
	 */
	protected IFuture<DelegationURLClassLoader> createClassLoader(final IResourceIdentifier rid, Map<IResourceIdentifier, List<IResourceIdentifier>> alldeps, final IResourceIdentifier support)
	{
		final Future<DelegationURLClassLoader> ret = new Future<DelegationURLClassLoader>();
//		final URL url = rid.getLocalIdentifier().getSecondEntity();
		final List<IResourceIdentifier> deps = alldeps.get(rid);
		
		CollectionResultListener<DelegationURLClassLoader> lis = new CollectionResultListener<DelegationURLClassLoader>
			(deps.size(), true, new ExceptionDelegationResultListener<Collection<DelegationURLClassLoader>, DelegationURLClassLoader>(ret)
		{
			public void customResultAvailable(Collection<DelegationURLClassLoader> result)
			{
				DelegationURLClassLoader[] delegates = (DelegationURLClassLoader[])result.toArray(new DelegationURLClassLoader[result.size()]);
				DelegationURLClassLoader cl = new DelegationURLClassLoader(rid, ClassLoader.getSystemClassLoader(), delegates);
				classloaders.put(rid, cl);
				addSupport(rid, support);
				ret.setResult(cl);
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
		
		SServiceProvider.getService(provider, IDependencyResolverService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDependencyResolverService, Map<IResourceIdentifier, List<IResourceIdentifier>>>(ret)
		{
			public void customResultAvailable(IDependencyResolverService drs)
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
				ridsupport.put(rid, new HashSet<IResourceIdentifier>());
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
			mysup.add(support);
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

	/**
	 *  Start the service.
	 */
	public IFuture<Void>	startService()
	{
		final Future<Void>	ret	= new Future<Void>();
		super.startService().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				SServiceProvider.getService(provider,ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ISettingsService	settings	= (ISettingsService)result;
						settings.registerPropertiesProvider(LIBRARY_SERVICE, LibraryService.this)
							.addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								super.customResultAvailable(getServiceIdentifier());
							}
						});
					}
					public void exceptionOccurred(Exception exception)
					{
						// No settings service: ignore
						ret.setResult(null);
//						ret.setResult(getServiceIdentifier());
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
	public IFuture<Void>	shutdownService()
	{
//		System.out.println("shut");
		final Future	saved	= new Future();
		SServiceProvider.getService(provider,ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(saved)
		{
			public void customResultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.deregisterPropertiesProvider(LIBRARY_SERVICE)
					.addResultListener(new DelegationResultListener(saved));
			}
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore
				saved.setResult(null);
			}
		});
		
		final Future<Void>	ret	= new Future<Void>();
		saved.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				synchronized(this)
				{
//					libcl = null;
					listeners.clear();
					LibraryService.super.shutdownService().addResultListener(new DelegationResultListener(ret));
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


	/**
	 *  Test if a file name is contained.
	 */
	public static int indexOfFilename(String url, List urlstrings)
	{
		int ret = -1;
		try
		{
			File file = urlToFile(url);
			if(file==null)
				file	= new File(url);
			for(int i=0; file!=null && i<urlstrings.size() && ret==-1; i++)
			{
				String	totest	= (String)urlstrings.get(i);
				File test = urlToFile(totest);
				if(test==null)
					test	= new File(totest);
				if(test!=null && file.getCanonicalPath().equals(test.getCanonicalPath()))
					ret = i;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 *  Convert an URL to a file.
	 *  @return null, if the URL is neither 'file:' nor 'jar:file:' URL and no path point to an existing file.
	 */
	public static File urlToFile(String url)
	{
		File	file	= null;
		if(url.startsWith("file:"))
		{
			try
			{
				url	= URLDecoder.decode(url, "UTF-8");
			}
			catch(UnsupportedEncodingException uee)
			{
			}
			file	= new File(url.substring(5));
		}
		else if(url.startsWith("jar:file:"))
		{
			try
			{
				url	= URLDecoder.decode(url, "UTF-8");
			}
			catch(UnsupportedEncodingException uee)
			{
			}
			file	= new File(url.substring(9));
		}
		else
		{
			file	= new File(url);
			if(!file.exists())
			{
				file	= null;
			}
		}
		return file;
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
}

