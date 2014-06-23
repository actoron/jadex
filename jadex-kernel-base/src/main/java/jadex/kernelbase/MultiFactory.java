package jadex.kernelbase;

import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IMultiKernelNotifierService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CallMultiplexer;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 *  Kernel that delegates calls to sub-kernels it finds using on-demand searches.
 *  
 *  NOTE: This is extremely complex code. Do not touch unless you really, REALLY
 *        know what you are doing.
 */
@Service
public class MultiFactory implements IComponentFactory, IMultiKernelNotifierService
{
	/** Kernel model property for extensions */
	protected static final String KERNEL_EXTENSIONS = "kernel.types";
	
	/** The internal access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	/** Kernel default locations */
	protected Map kerneldefaultlocations;
	
	
	/** Cache of known factories */
	protected Map factorycache;
	
	/** Cache of kernel locations */
	protected MultiCollection kernellocationcache;
	
	/** URLs of the kernels */
	protected MultiCollection kernelurls;
	
	/** Set of potential URLs for kernel searches */
	protected Set potentialurls;
	
	/** Currently valid URLs */
	protected Set validurls;
	
	/** Set of kernels that have been active at one point */
	protected Set activatedkernels;
	
	/** Currently supported types */
	protected Set componenttypes;
	
	/** Cache of component icons */
	protected Map iconcache;
	
	/** Base Blacklist of extension for which there is no factory */
	protected Set baseextensionblacklist;

	/** Blacklist of extension for which there is no factory */
	protected Set extensionblacklist;
	
	/** Kernel blacklist */
	protected Set kernelblacklist;
	
	/** Unloadable kernel locations that may become loadable later. */
	protected Set potentialkernellocations;
	
	/** Call Multiplexer */
	protected CallMultiplexer multiplexer;
	
	/** The listeners. */
	protected List listeners;
	
	/** The service identifier. */
	@ServiceIdentifier(IComponentFactory.class)
	protected IServiceIdentifier sid;
	
	/** Flag whether the service has started */
	protected boolean started;
	
	/** Library service listener */
	protected ILibraryServiceListener liblistener;
	
	/** The library service. */
	protected ILibraryService libservice;

	/**
	 *  Creates a new MultiFactory.
	 *  @param ia Component internal access.
	 *  @param defaultLocations Known kernel location to be checked first.
	 *  @param kernelblacklist Kernels the factory should ignore.
	 *  @param extensionblacklist File extension the factory should not consider to be models 
	 *  	   (no extension and most files with .class extension are ignored by default)
	 */
	public MultiFactory(String[] defaultLocations, String[] kernelblacklist, String[] extensionblacklist)
	{
		//super(ia.getServiceContainer().getId(), IComponentFactory.class, null);
		//this.ia = ia;
		this.factorycache = new HashMap();
		this.kernellocationcache = new MultiCollection();
		this.kernelurls = new MultiCollection();
		this.potentialurls = new LinkedHashSet();
		this.validurls = new HashSet();
		this.multiplexer = new CallMultiplexer();
		this.baseextensionblacklist = new HashSet();
		if (extensionblacklist != null)
			Arrays.asList(extensionblacklist);
//		this.baseextensionblacklist.add(null);
		
		kerneldefaultlocations = new MultiCollection();
		if(defaultLocations != null)
		{
			for (int i = 0; i < defaultLocations.length; ++i)
			{
				kerneldefaultlocations.put(null, defaultLocations[i]);
			}
		}
		
		activatedkernels = new HashSet();
		componenttypes = new HashSet();
		iconcache = new HashMap();
		this.kernelblacklist = new HashSet();
		if(kernelblacklist != null)
		{
			this.kernelblacklist.addAll(Arrays.asList(kernelblacklist));
		}
		this.extensionblacklist = new HashSet(baseextensionblacklist);
		this.potentialkernellocations = new HashSet();
		this.listeners = new ArrayList();
		started = false;
	}
	
	/**
	 *  Starts the service.
	 */
	@ServiceStart
	public IFuture startService()
	{
		if (started)
			return IFuture.DONE;
		
		final Future ret = new Future()
		{
			public void setResult(Object result)
			{
				started = true;
				super.setResult(result);
			}
		};
		
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				libservice = (ILibraryService)result;
				final IExternalAccess exta = ia.getExternalAccess();
				liblistener = new ILibraryServiceListener()
				{
					public IFuture resourceIdentifierRemoved(IResourceIdentifier parid, final IResourceIdentifier rid)
					{
						exta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								URL url = SUtil.toURL(rid.getLocalIdentifier().getUri());
								Collection affectedkernels = (Collection)kernelurls.remove(url);
								if (affectedkernels != null)
								{
									String[] keys = (String[]) kernellocationcache.keySet().toArray(new String[0]);
									for(int i = 0; i < keys.length; ++i)
									{
										for(Iterator it = affectedkernels.iterator(); it.hasNext(); )
										{
											kernellocationcache.removeObject(keys[i], it.next());
										}
									}
								}
								potentialurls.remove(url);
								validurls.remove(url);
								return IFuture.DONE;
							}
						});
						return IFuture.DONE;
					}
					
					public IFuture resourceIdentifierAdded(IResourceIdentifier parid, final IResourceIdentifier rid, boolean rem)
					{
						final URL url = SUtil.toURL(rid.getLocalIdentifier().getUri());
						exta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								extensionblacklist = new HashSet(baseextensionblacklist);
								validurls.add(url);
								potentialurls.add(url);
								return IFuture.DONE;
							}
						});
						return IFuture.DONE;
					}
				};
				
				libservice.addLibraryServiceListener(liblistener);
				
				libservice.getAllURLs().addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						potentialurls.addAll((Collection) result);
						validurls.addAll((Collection) result);
						
						if(kerneldefaultlocations.isEmpty())
							ret.setResult(null);
						else
						{
							// Initialize default locations
//							String[] dl = (String[])kerneldefaultlocations.keySet().toArray(new String[kerneldefaultlocations.size()]);
							String[] dl = kerneldefaultlocations.get(null) == null? new String[0] : (String[]) ((Collection) kerneldefaultlocations.get(null)).toArray(new String[kerneldefaultlocations.size()]);
							kerneldefaultlocations.clear();
							IResultListener loccounter = ia.createResultListener(new CounterResultListener(dl.length, ia.createResultListener(new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object result)
								{
									ret.setResult(null);
								}
							}))
							{
								public void intermediateResultAvailable(Object result)
								{
									final IModelInfo kernel = (IModelInfo) result;
									libservice.getClassLoader(kernel.getResourceIdentifier())
										.addResultListener(new IResultListener<ClassLoader>()
									{
										public void resultAvailable(ClassLoader result)
										{
											String[] exts = (String[])kernel.getProperty(KERNEL_EXTENSIONS, result);
											if (exts != null)
												for (int i = 0; i < exts.length; ++i)
													kerneldefaultlocations.put(exts[i], kernel.getFilename());
										}
										public void exceptionOccurred(Exception exception)
										{
											// Todo: log warning!?
										}
									});
								}
							});
							
							for(int i = 0; i < dl.length; ++i)
								loadModel(dl[i], null, null).addResultListener(loccounter);
						}
					}
				}));
				
//				libservice.getAllResourceIdentifiers().addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						List col = new ArrayList();
//						for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
//						{
//							col.add(((IResourceIdentifier)it.next()).getLocalIdentifier().getSecondEntity());
//						}
//						potentialurls.addAll(col);
//						validurls.addAll(col);
//						
//						if(kerneldefaultlocations.isEmpty())
//							ret.setResult(null);
//						else
//						{
//							// Initialize default locations
//							String[] dl = (String[])kerneldefaultlocations.keySet().toArray(new String[kerneldefaultlocations.size()]);
//							kerneldefaultlocations.clear();
//							IResultListener loccounter = ia.createResultListener(new CounterResultListener(dl.length, ia.createResultListener(new DelegationResultListener(ret)
//							{
//								public void customResultAvailable(Object result)
//								{
//									ret.setResult(null);
//								}
//							}))
//							{
//								public void intermediateResultAvailable(Object result)
//								{
//									final IModelInfo kernel = (IModelInfo) result;
//									libservice.getClassLoader(kernel.getResourceIdentifier())
//										.addResultListener(new IResultListener<ClassLoader>()
//									{
//										public void resultAvailable(ClassLoader result)
//										{
//											String[] exts = (String[])kernel.getProperty(KERNEL_EXTENSIONS, result);
//											if (exts != null)
//												for (int i = 0; i < exts.length; ++i)
//													kerneldefaultlocations.put(exts[i], kernel.getFilename());
//										}
//										public void exceptionOccurred(Exception exception)
//										{
//											// Todo: log warning!?
//										}
//									});
//								}
//							});
//							
//							for(int i = 0; i < dl.length; ++i)
//								loadModel(dl[i], null, null).addResultListener(loccounter);
//						}
//					}
//				}));
			}
		}));
		return ret;
	}
	
	/**
	 *  Stops the service.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdownService()
	{
		final Future<Void> ret = new Future<Void>();
		
		ia.getServiceContainer().searchService(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
		{
			public void customResultAvailable(ILibraryService ls)
			{
				ls.removeLibraryServiceListener(liblistener)
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 * Load a model.
	 * 
	 * @param model The model (e.g. file name).
	 * @param The imports (if any).
	 * @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(final String model, final String[] imports, IResourceIdentifier rid)
	{
//		System.out.println("loadModel: "+model);
		
		return loadModel(model, imports, rid, false);
	}
	
	/**
	 * Load a model.
	 * 
	 * @param model The model (e.g. file name).
	 * @param The imports (if any).
	 * @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(final String model, final String[] imports, final IResourceIdentifier rid, boolean isrecur)
	{
//		System.out.println("loadModel2: "+model);
		
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		
		findKernel(model, imports, rid, isrecur).addResultListener(ia.createResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
					((IComponentFactory)result).loadModel(model, imports, rid).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
				else
					ret.setException(new RuntimeException("Factory not found: " + model));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		})));
		
		return ret;
	}

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid)
	{
//		if(model.endsWith("BDI.class"))
//			System.out.println("isLoadable: "+model);

		final Future<Boolean> ret = new Future<Boolean>();
		findKernel(model, imports, rid).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result != null)
					ret.setResult(true);
				else
					ret.setResult(false);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(false);
			}
		}));
		return ret;
	}

	/**
	 * Test if a model is startable (e.g. an component).
	 * 
	 * @param model
	 *            The model (e.g. file name).
	 * @param The
	 *            imports (if any).
	 * @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(final String model, final String[] imports, final IResourceIdentifier rid)
	{
//		System.out.println("isStartable: "+model);

		final Future<Boolean> ret = new Future<Boolean>();
		findKernel(model, imports, rid).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
					((IComponentFactory) result).isStartable(model, imports, rid).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
				else
					ret.setResult(false);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(false);
			}
		}));
		return ret;
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(final String model, final String[] imports, final IResourceIdentifier rid)
	{
//		if(model.endsWith("agent.xml"))
//			System.out.println("model: "+model);

		final Future<String> ret = new Future<String>();
		findKernel(model, imports, rid).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result != null)
				{
					((IComponentFactory)result).getComponentType(model, imports, rid)
						.addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
				}
				else
				{
					ret.setException(new RuntimeException("Factory not found: " + model));
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		return ret;
	}

	/**
	 * Get a default icon for a component type.
	 */
	public IFuture getComponentTypeIcon(String type)
	{
		return new Future(iconcache.get(type));
	}

	// -------- cached --------

	/**
	 * Get the names of component types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return (String[])componenttypes.toArray(new String[componenttypes.size()]);
		//return (String[]) iconcache.keySet().toArray(new String[iconcache.size()]);
	}

	/**
	 * Get the properties (name/value pairs). Arbitrary properties that can e.g.
	 * be used to define kernel-specific settings to configure tools.
	 * 
	 * @param type
	 *            The component type.
	 * @return The properties or null, if the component type is not supported by
	 *         this factory.
	 */
	public Map getProperties(String type)
	{
		return Collections.EMPTY_MAP;
	}

	/**
	 * Create a component instance.
	 * 
	 * @param factory
	 *            The component adapter factory.
	 * @param model
	 *            The component model.
	 * @param config
	 *            The name of the configuration (or null for default
	 *            configuration)
	 * @param arguments
	 *            The arguments for the component as name/value pairs.
	 * @param parent
	 *            The parent component (if any).
	 * @return An instance of a component and the corresponding adapter.
	 */
	@Excluded
	public IFuture<Tuple2<IComponentInstance, IComponentAdapter>> createComponentInstance(final IComponentDescription desc,
			final IComponentAdapterFactory factory, final IModelInfo model, final String config,
			final Map<String, Object> arguments, final IExternalAccess parent,
			final RequiredServiceBinding[] bindings, final boolean copy, final boolean realtime, final boolean persist,
			final IPersistInfo persistinfo,
			final IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> ret)
	{
//		System.out.println("createComponentInstance: "+model.getName());
		
//		IComponentFactory fac = (IComponentFactory)factorycache.get(getModelExtension(model.getFilename()));
		IComponentFactory fac = (IComponentFactory) getCacheResultForModel(model.getFilename(), factorycache);
		if(fac != null)
			return fac.createComponentInstance(desc, factory, model, config, arguments, parent, bindings, copy, realtime, persist, persistinfo, resultlistener, ret);
		
		final Future<Tuple2<IComponentInstance, IComponentAdapter>> res = new Future<Tuple2<IComponentInstance, IComponentAdapter>>();
		
		findKernel(model.getFilename(), null, model.getResourceIdentifier()).addResultListener(ia.createResultListener(new DelegationResultListener(res)
		{
			public void customResultAvailable(Object result)
			{
				((IComponentFactory)result).createComponentInstance(desc, factory, model, config, arguments, parent, bindings, copy, realtime, persist, persistinfo, resultlistener, ret).addResultListener(new DelegationResultListener(res));
			}
		}));
		return res;
	}
	
	/**
	 *  Adds a kernel listener.
	 *  @param listener The listener.
	 *  @return Null, when done.
	 */
	public IFuture addKernelListener(IMultiKernelListener listener)
	{
		listeners.add(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Removes a kernel listener.
	 *  @param listener The listener.
	 *  @return Null, when done.
	 */
	public IFuture removeKernelListener(IMultiKernelListener listener)
	{
		listeners.remove(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Fires a types added event.
	 *  @param types The types added.
	 *  @return Null, when done.
	 */
	public IFuture fireTypesAdded(String[] types)
	{
		final Future ret = new Future();
		IMultiKernelListener[] ls = (IMultiKernelListener[]) listeners.toArray(new IMultiKernelListener[listeners.size()]);
		IResultListener counter = ia.createResultListener(new CounterResultListener(ls.length, true, ia.createResultListener(new DelegationResultListener(ret))));
		for (int i = 0; i < ls.length; ++i)
			ls[i].componentTypesAdded(types).addResultListener(counter);
		return ret;
	}
	
	/**
	 *  Fires a types removed event.
	 *  @param types The types removed.
	 *  @return Null, when done.
	 */
	public IFuture fireTypesRemoved(String[] types)
	{
		final Future ret = new Future();
		IMultiKernelListener[] ls = (IMultiKernelListener[]) listeners.toArray(new IMultiKernelListener[listeners.size()]);
		IResultListener counter = ia.createResultListener(new CounterResultListener(ls.length, true, ia.createResultListener(new DelegationResultListener(ret))));
		for (int i = 0; i < ls.length; ++i)
			ls[i].componentTypesAdded(types).addResultListener(counter);
		return ret;
	}
	
	/**
	 *  Attempts to find an active kernel factory, searching, loading and instantiating as required.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @param classloader Model classloader.
	 *  @return Factory instance of the kernel or null if no matching kernel was found.
	 */
	protected IFuture findKernel(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		return findKernel(model, imports, rid, false);
	}
	
	/**
	 *  Attempts to find an active kernel factory, searching, loading and instantiating as required.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @param classloader Model classloader.
	 *  @return Factory instance of the kernel or null if no matching kernel was found.
	 */
	protected IFuture findKernel(final String model, final String[] imports, final IResourceIdentifier rid, final boolean isrecur)
	{
//		final String ext = getModelExtension(model);
//		System.out.println("EXT: " + ext);
//		if(extensionblacklist.contains(ext))
//			return IFuture.DONE;
		if (isInExtensionBlacklist(model, extensionblacklist))
			return IFuture.DONE;
		
//		if(model.toString().indexOf("agent")!=-1)
//			System.out.println("findKernel: "+model);
		
//		IComponentFactory fac = (IComponentFactory)factorycache.get(ext);
		IComponentFactory fac = (IComponentFactory) getCacheResultForModel(model, factorycache);
		if(fac != null)
			return new Future(fac);
		
		final Future ret = new Future();
		
//		final ClassLoader classloader =  libservice.getClassLoader(rid);
		
		findActiveKernel(model, imports, rid).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
				{
					ret.setResult(result);
				}
				else
				{
					findLoadableKernel(model, imports, rid, isrecur)
						.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							if (result != null)
								ret.setResult(result);
							else
							{
								// FIXME: Blacklist? What if a new factory model is added later?
//								if (!isrecur)
//									extensionblacklist.add(ext);
								ret.setResult(null);
							}
						}
					}));
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Give warning?
				// FIXME: Blacklist? What if a new factory model is added later?
//				if(!isrecur)
//					extensionblacklist.add(ext);
				resultAvailable(null);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Attempts to find a running kernel matching the model.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @return Factory instance of the kernel or null if no matching kernel was found.
	 */
	protected IFuture findActiveKernel(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		//SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(kernelmodel, null, classloader))
		final Future ret = new Future();
		SServiceProvider.getServices(ia.getServiceContainer(), IComponentFactory.class, RequiredServiceInfo.SCOPE_APPLICATION).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void exceptionOccurred(Exception exception)
			{
				super.exceptionOccurred(exception);
			}
			
			public void customResultAvailable(Object result)
			{
				final Collection factories = ((Collection)result);
				factories.remove(MultiFactory.this);
				final IResultListener factorypicker = ia.createResultListener(new CollectionResultListener(factories.size(), true, ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						Collection viablefactories = (Collection) result;
						if (!viablefactories.isEmpty())
							ret.setResult(viablefactories.iterator().next());
						else
						{
							ret.setResult(null);
						}
					}
				})));
				for (Iterator it = factories.iterator(); it.hasNext(); )
				{
					final IComponentFactory factory = (IComponentFactory)it.next();
//					System.out.println("Trying isloadable :" + factory + " for " + model);
					factory.isLoadable(model, imports, rid).addResultListener(ia.createResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							if (Boolean.TRUE.equals(result))
								factorypicker.resultAvailable(factory);
							else
								factorypicker.exceptionOccurred(new RuntimeException());
						}
						
						public void exceptionOccurred(Exception exception)
						{
							factorypicker.exceptionOccurred(exception);
						}
					}));
				}
			}
		}));
		return ret; 
	}
	
	/**
	 *  Attempts to find a kernel which is currently not loaded, matching the model.
	 *  This method will instantiate the required kernel if it is found.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @param classloader Model classloader.
	 *  @return Factory instance of the activated kernel or null if no matching kernel was found.
	 */
	protected IFuture findLoadableKernel(final String model, final String[] imports, final IResourceIdentifier rid, boolean isrecur)
	{
//		if(model.toString().indexOf("HelloWorld")!=-1)
//			System.out.println("findLoadableKernel: "+model);
		
		IFuture	ret;
//		String dl = (String) kerneldefaultlocations.get(getModelExtension(model));
		String dl = (String) getCacheResultForModel(model, kerneldefaultlocations);
		if (dl != null)
			ret	= startLoadableKernel(model, imports, rid, dl);
		else
			ret	= findKernelInCache(model, imports, rid, isrecur);
		return ret;
	}
	
	/**
	 *  Attempts to find a kernel which is currently not loaded in the
	 *  cache or through search, matching the model.
	 *  This method will instantiate the required kernel if it is found.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @param classloader Model classloader.
	 *  @return Factory instance of the activated kernel or null if no matching kernel was found.
	 */
	protected IFuture findKernelInCache(final String model, final String[] imports, final IResourceIdentifier rid, final boolean isrecur)
	{
//		if(model.toString().indexOf("BDI")!=-1)
//			System.out.println("findKernelInCache0: "+model);
		final Future ret = new Future();
		
//		Collection kernels = kernellocationcache.getCollection(getModelExtension(model));
		Tuple2<Object, Object> cachedkernels = getCacheKeyValueForModel(model, kernellocationcache);
		final Object kernelsext = cachedkernels != null? cachedkernels.getFirstEntity(): null;
		Collection kernels = cachedkernels != null? (Collection) cachedkernels.getSecondEntity() : null;
		String cachedresult = null;
		if(kernels != null && !kernels.isEmpty())
			cachedresult = (String) kernels.iterator().next();
		
		if(cachedresult != null)
		{
//			if(model.toString().indexOf("RemoteServiceManagementAgent")!=-1)
//				System.out.println("findKernelInCache1: "+model);
			final String	kernelmodel	= cachedresult;	
			startLoadableKernel(model, imports, rid, kernelmodel)
				.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
			{
				public void exceptionOccurred(Exception exception)
				{
					kernellocationcache.removeObject(kernelsext, kernelmodel);
					findKernelInCache(model, imports, rid, isrecur)
						.addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
				}
			}));
		}
		else
		{
			if(potentialurls.isEmpty() && !hasLoadablePotentialKernels() || isrecur)
			{
				ret.setResult(null);
			}
			else
			{
				multiplexer.doCall(new IResultCommand()
				{
					public Object execute(Object args)
					{
						return searchPotentialUrls(rid);
					}
				}).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						findKernelInCache(model, imports, rid, isrecur)
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
					}
				}));
			}
		}
		return ret;
	}
	
	/**
	 *  Check if there is a potential kernel that could be
	 *  loaded when another kernel from cache is started. 
	 */
	protected boolean hasLoadablePotentialKernels()
	{
		boolean	ret	= false;
		for(Object loc: potentialkernellocations)
		{
			ret	= getCacheKeyValueForModel((String)loc, kernellocationcache)!=null;
			if(ret)
			{
				break;
			}
		}
		return ret;
	}
	
	/**
	 *  Starts a kernel matching the model.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @param classloader Model classloader.
	 *  @param kernelmodel Model of the kernel.
	 *  @return Factory instance of the activated kernel.
	 */
	protected IFuture startLoadableKernel(final String model, final String[] imports, final IResourceIdentifier rid, final String kernelmodel)
	{
//		System.out.println("startLoadableKernel: "+model);
		return multiplexer.doCall(kernelmodel, new IResultCommand()
		{
			public Object execute(Object args)
			{
//				System.out.println("Starting kernel: " + kernelmodel);
				final Future ret = new Future();
				findActiveKernel(kernelmodel, null, rid).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
//						System.out.println("Starting kernel1: " + kernelmodel);
						IComponentFactory	fac	= (IComponentFactory)result;
						fac.loadModel(kernelmodel, null, rid).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
//								System.out.println("Starting kernel2: " + kernelmodel);
								final IModelInfo	info	= (IModelInfo)result;
								SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void exceptionOccurred(
											Exception exception)
									{
										super.exceptionOccurred(exception);
									}
									
									public void customResultAvailable(Object result)
									{
//										System.out.println("Starting kernel3: " + kernelmodel);
										final IComponentManagementService cms = (IComponentManagementService) result;										
										final CreationInfo ci = new CreationInfo(ia.getComponentIdentifier());
										String	name	= info.getName().toLowerCase();
										if(name.startsWith("kernel"))
											name	= name.substring(6);
										final String fname	= "kernel_"+name;
										
										libservice.getClassLoader(info.getResourceIdentifier())
											.addResultListener(new DelegationResultListener<ClassLoader>(ret)
										{
											public void customResultAvailable(ClassLoader result)
											{
												final String[] kexts = (String[]) info.getProperty(KERNEL_EXTENSIONS, result) == null? new String[0] : (String[]) info.getProperty(KERNEL_EXTENSIONS, result);
												cms.createComponent(fname, kernelmodel, ci, ia.createResultListener(new IResultListener()
												{
													public void resultAvailable(Object result)
													{
//														System.out.println("Killed kernel4: " + kernelmodel);
														for(int i = 0; i < kexts.length; ++i)
															factorycache.remove(kexts[i]);
													}
													
													public void exceptionOccurred(Exception exception)
													{
//														System.out.println("Killed kernel5: " + kernelmodel);
														exception.printStackTrace();
														resultAvailable(null);
													}
												})).addResultListener(ia.createResultListener(new IResultListener()
												{
													public void resultAvailable(Object result)
													{
//														System.out.println("Starting kernel6: " + kernelmodel);
														findActiveKernel(model, imports, rid).addResultListener(ia.createResultListener(new DefaultResultListener()
														{
															public void resultAvailable(Object result)
															{
																final IComponentFactory kernel = (IComponentFactory) result;
																if(kernel == null)
																{
																	ret.setResult(null);
																	return;
																}
																for(int i = 0; i < kexts.length; ++i)
																{
																	factorycache.put(kexts[i], kernel);
																}
																
																// If this is a new kernel, gather types and icons
																if(!activatedkernels.contains(kernelmodel))
																{
																	final String[] types = kernel.getComponentTypes();
																	componenttypes.addAll(Arrays.asList(types));
																	
																	activatedkernels.add(kernelmodel);
																	
																	if(SReflect.HAS_GUI)
																	{
																		final IResultListener typecounter = ia.createResultListener(new CounterResultListener(types.length, true, ia.createResultListener(new DelegationResultListener(ret)
																		{
																			public void customResultAvailable(Object result)
																			{
																				SServiceProvider.getService(ia.getServiceContainer(), IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_APPLICATION).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
																				{
																					public void customResultAvailable(Object result)
																					{
																						MultiFactory.this.fireTypesAdded(types).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
																						{
																							public void customResultAvailable(Object result)
																							{
																								ret.setResult(kernel);
																							};
																						}));
																					};
																				}));
																			};
																		})));
																		
																		for(int i = 0; i < types.length; ++i)
																		{
																			final int fi = i;
																			kernel.getComponentTypeIcon(types[i]).addResultListener(new IResultListener<byte[]>()
																			{
																				public void resultAvailable(byte[] result)
																				{
																					iconcache.put(types[fi], result);
																					typecounter.resultAvailable(null);
																				}
																				public void exceptionOccurred(Exception exception)
																				{
																					typecounter.exceptionOccurred(exception);
																				}
																			});
																		}
																	}
																	else
																	{
																		ret.setResult(kernel);
																	}
																}
																else
																{
																	ret.setResult(kernel);
																}
															}
														}));
													}
													
													public void exceptionOccurred(Exception exception)
													{
//														System.out.println("Starting kernel7: " + kernelmodel);
//														exception.printStackTrace();
														ret.setException(exception);
													}
												}));
											}
										});
									}
								}));
							}
						}));
					}
				}));
				return ret;
			}
		});
	}
	
	/**
	 *  Searches the set of potential URLs for a kernel supporting the extension,
	 *  putting it in the cache for use if found.
	 */
	protected IFuture searchPotentialUrls(final IResourceIdentifier rid)
	{
//		System.out.println("searchPotentialURLs: "+rid+", "+potentialurls);
		
		final Future ret = new Future();
		examineKernelModels(new ArrayList(potentialkernellocations), rid).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Map kernellocs = (Map) result;
				if (kernellocs != null && !kernellocs.isEmpty())
				{
//					System.out.println("searchPotentialURLs1: "+kernellocs);
					kernellocationcache.putAll(kernellocs);
					ret.setResult(null);
				}
				else
				{
					final URL url = (URL)potentialurls.iterator().next();
//					if(url.toString().indexOf("bdi")!=-1)
//						System.out.println("searchPotentialURLs2: "+url);
					quickKernelSearch(url, rid).addResultListener(ia.createResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							if (result != null && validurls.contains(url))
							{
								Map kernelmap = (Map) result;
								kernellocationcache.putAll(kernelmap);
								for (Iterator it = kernelmap.values().iterator(); it.hasNext(); )
									kernelurls.put(url, it.next());
							}
							
							potentialurls.remove(url);
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							potentialurls.remove(url);
							resultAvailable(null);
						}
					}));
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Searches supplied URL for a potential kernel. This method calls kernelSearch()
	 *  with a prefilter that excludes all files except the ones starting with "Kernel".
	 *  
	 *  @param url The URL to search
	 */
	protected IFuture quickKernelSearch(URL url, IResourceIdentifier rid)
	{
		return kernelSearch(url, new IFilter()
		{
			public boolean filter(Object obj)
			{
				String loc = (String) obj;
//				if (loc.toLowerCase().contains("kernel"))
//				{
//					System.out.println(loc);
//					System.out.println(loc.substring(loc.lastIndexOf('/') + 1).toLowerCase().startsWith("kernel"));
//				}
				// For jar entries, strip directory part.
				return loc.substring(loc.lastIndexOf("/") + 1).toLowerCase().startsWith("kernel");
			}
		}, rid);
	}
	
	/**
	 *  Searches supplied URL for a potential kernel matching the filter and containing
	 *  a description of loadable file extensions.
	 *  
	 *  @param url The URL to search
	 *  @param prefilter Prefilter applied before further restrictions are applied.
	 */
	protected IFuture kernelSearch(final URL url, final IFilter prefilter, IResourceIdentifier rid)
	{
		List modellocs = searchUrl(url, new IFilter()
		{
			public boolean filter(Object obj)
			{
				if (obj instanceof String)
				{
					String loc = (String) obj;
					
					for (Object oblstr : baseextensionblacklist)
					{
						String blstr = (String) oblstr;
						
						if (loc.endsWith(blstr))
						{
							return false;
						}
					}
					
					for (Object oblstr : kernelblacklist)
					{
						//!kernelblacklist.contains(loc.substring(loc.lastIndexOf(File.separatorChar) + 1))
						String blstr = (String) oblstr;
						
						if (loc.endsWith(blstr))
						{
							return false;
						}
					}
					
					if (!isInExtensionBlacklist(obj, baseextensionblacklist) &&
						!kernelblacklist.contains(loc.substring(loc.lastIndexOf(File.separatorChar) + 1)) &&
						prefilter.filter(obj)) 
					{
//							System.out.println("Found kernel: " + loc);
							return true;
					}
				}
//				System.out.println("Decided it's not a kernel: " + obj);
				return false;
			}
		});
		
		return examineKernelModels(modellocs, rid);
	}
	
	/**
	 *  Examines potential kernels whether their model can be loaded or loaded
	 *  with the help of another kernel that can be found.
	 *  
	 *  @param modellocs List of locations of potential kernels.
	 *  @param libservice Library service.
	 *  @return Map of viable kernels with extension they support.
	 */
	protected IFuture examineKernelModels(final List modellocs, IResourceIdentifier rid)
	{
//		if(modellocs.toString().indexOf("KernelApplication.component.xml")!=-1)
//			System.out.println("examineKernelModels0: "+modellocs);
		final Map kernellocs = new HashMap();
		if (modellocs.isEmpty())
			return new Future(kernellocs);
		final Future ret = new Future();
		final IResultListener kernelCounter = ia.createResultListener(new CounterResultListener(modellocs.size(), true,
			new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
//				if(modellocs.toString().indexOf("KernelApplication.component.xml")!=-1)
//					System.out.println("examineKernelModels1: "+modellocs+", "+kernellocs);
				super.customResultAvailable(kernellocs);
			}
			public void exceptionOccurred(Exception e)
			{
//				e.printStackTrace();
				super.exceptionOccurred(e);
			}
		}));

		for(Iterator it2 = modellocs.iterator(); it2.hasNext();)
		{
			final String kernelloc = (String)it2.next();
			loadModel(kernelloc, null, rid, true)
				.addResultListener(ia.createResultListener(new IResultListener<IModelInfo>()
			{
				public void resultAvailable(final IModelInfo modelinfo)
				{
//					System.out.println("Tried to load model for kernel: " + kernelloc + " model " + modelinfo);
					if(modelinfo!=null)
					{
						potentialkernellocations.remove(kernelloc);
						libservice.getClassLoader(modelinfo.getResourceIdentifier())
							.addResultListener(new IResultListener<ClassLoader>()
						{
							public void resultAvailable(ClassLoader result)
							{
//								if(modellocs.toString().indexOf("KernelApplication.component.xml")!=-1)
//									System.out.println("examineKernelModels2: "+modellocs);
								String[] exts = (String[])modelinfo.getProperty(KERNEL_EXTENSIONS, result);
//								System.out.println("Kernel extensions for kernel " + kernelloc + " " + SUtil.arrayToString(exts));
								if(exts!=null)
								{
									for (int i = 0; i < exts.length; ++i)
										kernellocs.put(exts[i], modelinfo.getFilename());
								}
								kernelCounter.resultAvailable(result);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// Todo: log warning!?
								potentialkernellocations.add(kernelloc);
								kernelCounter.exceptionOccurred(new RuntimeException());
							}
						});
					}
					else
					{
						potentialkernellocations.add(kernelloc);
						kernelCounter.exceptionOccurred(new RuntimeException());
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("Tried to load model for kernel: " + kernelloc + " but failed. ");
					resultAvailable(null);
				}
			}));
		}
		
		return ret;
	}

	/**
	 *  Searches an URL, accepts both directory and .jar-based URLs.
	 *  @param url The URL.
	 *  @param filter The search filter.
	 *  @return List of file locations matching the filter.
	 */
	protected List searchUrl(URL url, IFilter filter)
	{
		try
		{
			File file = new File(url.toURI());
			if (file.isDirectory())
				return searchDirectory(file, filter, false);
			else if (file.getName().endsWith(".jar"))
				return searchJar(file, filter);
			else if (file.getName().endsWith(".apk"))
				return searchApk(file, filter);
		}
		catch (Exception e)
		{
		}

		return Collections.EMPTY_LIST;
	}

	/**
	 *  Searches a directory for files matching a filter.
	 *  
	 *  @param dir The directory.
	 *  @param filter The filter.
	 *  @param prependDir Flag whether to prepend the directory name to files found,
	 *  				  used when unwinding recursions.
	 * @return List of files matching the filter.
	 */
	protected List searchDirectory(File dir, IFilter filter, boolean prependDir)
	{
//		System.out.println("Searching dir: " + dir.getAbsolutePath());
		List ret = new ArrayList();
		File[] content = dir.listFiles();
		for (int i = 0; i < content.length; ++i)
		{
			if (content[i].isDirectory())
			{
				List subList = searchDirectory(content[i], filter, true);
				for (Iterator it = subList.iterator(); it.hasNext();)
				{
					if (prependDir)
						ret.add(dir.getName().concat(File.separator).concat(
								(String) it.next()));
					else
						ret.add(it.next());
				}
			}
			else if (filter.filter(content[i].getName()))
			{
//				System.out.println("May be a kernel: " + content[i].getName());
				if (prependDir)
					ret.add(dir.getName().concat(File.separator).concat(
							content[i].getName()));
				else
					ret.add(content[i].getName());
			}
//			else
//			{
//				System.out.println("Not a kernel: " + content[i].getName());
//			}
		}

		return ret;
	}

	/**
	 *  Searches a .jar for files matching a filter.
	 *  
	 *  @param jar The .jar-file.
	 *  @param filter The filter.
	 * @return List of files matching the filter.
	 */
	protected List searchJar(File jar, IFilter filter)
	{
//		System.out.println("Searching jar: " + jar.getAbsolutePath());
		List ret = new ArrayList();
		JarFile jarFile	= null;
		try
		{
			jarFile = new JarFile(jar);
			for (Enumeration entries = jarFile.entries(); entries
					.hasMoreElements();)
			{
				JarEntry entry = (JarEntry) entries.nextElement();
				if (filter.filter(entry.getName()))
					ret.add(entry.getName());
			}
		}
		catch (IOException e)
		{
			// Happens for initial jar when starting from jar, because Java adds crappy classpath entry.
			
			// TODO: Print warning?
//			System.out.println("Warning: File not found: " + jar.getAbsolutePath());
//			e.printStackTrace();
		}
		if(jarFile!=null)
		{
			try
			{
				jarFile.close();
			}
			catch(IOException e)
			{
				// Ignore
			}
		}
		return ret;
	}
	
	/**
	 *  Searches a .apk for files matching a filter.
	 *  
	 *  @param jar The .jar-file.
	 *  @param filter The filter.
	 * @return List of files matching the filter.
	 */
	protected List searchApk(File apk, IFilter filter)
	{
//		System.out.println("Searching apk: " + apk.getAbsolutePath());
		List ret = new ArrayList();
		try
		{
			// Scan for resource files in .apk
			ZipFile	zip	= new ZipFile(apk);
			Enumeration< ? extends ZipEntry>	entries	= zip.entries();
			while(entries.hasMoreElements())
			{
				String	entry	= entries.nextElement().getName();
				if(filter.filter(entry))
				{
					ret.add(entry);
				}
			}
			
			// Scan for classes in .dex
			Enumeration<String> dexentries = SUtil.androidUtils().getDexEntries(apk);
			while(dexentries.hasMoreElements())
			{
				String entry = dexentries.nextElement();
				entry = entry.replace('.', '/') + ".class";
				if(filter.filter(entry))
				{
					ret.add(entry);
				}					
			}
		}
		catch (IOException e)
		{
			System.out.println("Warning: File not found: " + apk.getAbsolutePath());
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/**
	 *  Helper method for generating the file extension of a model.
	 *  
	 *  @param model The model.
	 *  @return The file extension, special case for .class files.
	 */
//	protected String getModelExtension(String model)
//	{
////		int lastpoint = Math.max(Math.max(0, model.lastIndexOf(File.separatorChar)), model.lastIndexOf(this.packageseparator));
////		lastpoint = model.indexOf('.', lastpoint);
//		int lastpoint = model.lastIndexOf('.');
//		
//		if (lastpoint < 0 || lastpoint == (model.length() - 1))
//			return null;
//		
//		String ext = model.substring(lastpoint + 1);
//		
//		// Hack! todo: fix me
//		if(ext.equals("class"))
//		{
//			if(model.endsWith("Agent.class"))
//			{
//				ext = "Agent.class";
//			}
//			else if(model.endsWith("BDI.class"))
//			{
//				ext = "BDI.class";
//			}
//			else
//			{
//				return null;
//			}
//		}
//		
//		if (ext.equals("xml")) {
//			if(model.endsWith("component.xml")) 
//			{
//				ext = "component.xml";
//			}
//			else if(model.endsWith("application.xml")) {
//				ext = "application.xml";
//			}
//			else
//			{
//				return null;
//			}
//		}
//		
//		return ext;
//	}
	
	/**
	 *  Checks if a model matches an extension blacklist.
	 */
	protected boolean isInExtensionBlacklist(Object model, Set blacklist)
	{
//		if (model instanceof String)
//		{
//			if (((String) model).indexOf('.') == -1)
//			{
//				return true;
//			}
//			
//			for (Object oblstr : blacklist)
//			{
//				String blstr = (String) oblstr;
//				
//				if (((String) model).endsWith(blstr))
//				{
//					return true;
//				}
//			}
//		}
		return false;
	}
	
	/**
	 *  Gets result for a cached object based on model name.
	 *  
	 *  @param model The model.
	 *  @param map The cache.
	 *  @return A cache hit or null.
	 */
	protected Object getCacheResultForModel(String model, Map map)
	{
		Tuple2<Object, Object> ret = getCacheKeyValueForModel(model, map);
		return ret != null? ret.getSecondEntity() : null;
	}
	
	/**
	 *  Gets key/value for a cached object based on model name.
	 *  
	 *  @param model The model.
	 *  @param map The cache.
	 *  @return A cache hit or null.
	 */
	protected Tuple2<Object, Object> getCacheKeyValueForModel(String model, Map map)
	{
		Tuple2<Object, Object> ret = null;

		if (model != null && map != null)
		{
			for (Object oentry : map.entrySet())
			{
				Map.Entry entry = (Map.Entry) oentry;
				String ext = (String) entry.getKey();
				if (model.endsWith(ext))
				{
					ret = new Tuple2<Object, Object>(entry.getKey(), entry.getValue());
					
					break;
				}
			}
		}
		
		return ret;
	}
	
	public int hashCode()
	{
		return sid.hashCode();
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof IService)
			return sid.equals(((IService) obj).getServiceIdentifier());
		return false;
	}
}
