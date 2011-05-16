package jadex.kernelbase;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMultiKernelNotifierService;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CallMultiplexer;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *  Kernel that delegates calls to sub-kernels it finds using on-demand searches.
 *
 */
public class MultiFactory extends BasicService implements IComponentFactory
{
	/** Kernel model property for extensions */
	protected static final String KERNEL_EXTENSIONS = "kernel.types";
	
	/** The internal access. */
	protected IInternalAccess ia;
	
	/** Kernel default locations */
	protected Map kerneldefaultlocations;
	
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
	
	/** Unloadable kernel locations that may become loadable later */
	protected Set potentialkernellocations;
	
	/** Call Multiplexer */
	protected CallMultiplexer multiplexer;

	/**
	 *  Creates a new MultiFactory.
	 *  @param ia Component internal access.
	 *  @param defaultLocations Known kernel location to be checked first.
	 *  @param kernelblacklist Kernels the factory should ignore.
	 *  @param extensionblacklist File extension the factory should not consider to be models 
	 *  	   (no extension and most files with .class extension are ignored by default)
	 */
	public MultiFactory(IInternalAccess ia, String[] defaultLocations, String[] kernelblacklist, String[] extensionblacklist)
	{
		super(ia.getServiceContainer().getId(), IComponentFactory.class, null);
		this.ia = ia;
		this.kernellocationcache = new MultiCollection();
		this.kernelurls = new MultiCollection();
		this.potentialurls = new HashSet();
		this.validurls = new HashSet();
		this.multiplexer = new CallMultiplexer();
		this.baseextensionblacklist = new HashSet();
		if (extensionblacklist != null)
			Arrays.asList(extensionblacklist);
		this.baseextensionblacklist.add(null);
		
		kerneldefaultlocations = new HashMap();
		if (defaultLocations != null)
			for (int i = 0; i < defaultLocations.length; ++i)
				kerneldefaultlocations.put(defaultLocations[i], null);
		
		activatedkernels = new HashSet();
		componenttypes = new HashSet();
		iconcache = new HashMap();
		this.kernelblacklist = new HashSet();
		if (kernelblacklist != null)
				this.kernelblacklist.addAll(Arrays.asList(kernelblacklist));
		this.extensionblacklist = new HashSet(baseextensionblacklist);
		this.potentialkernellocations = new HashSet();
	}
	
	/**
	 *  Starts the service.
	 */
	public IFuture startService()
	{
		final Future ret = new Future();
		super.startService().addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(final Object res)
			{
				SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService) result;
						final IExternalAccess exta = ia.getExternalAccess();
						ls.addLibraryServiceListener(new ILibraryServiceListener()
						{
							public IFuture urlRemoved(final URL url)
							{
								exta.scheduleStep(new IComponentStep()
								{
									public Object execute(IInternalAccess ia)
									{
										Collection affectedkernels = (Collection) kernelurls.remove(url);
										if (affectedkernels != null)
										{
											String[] keys = (String[]) kernellocationcache.keySet().toArray(new String[0]);
											for (int i = 0; i < keys.length; ++i)
												for (Iterator it = affectedkernels.iterator(); it.hasNext(); )
													kernellocationcache.remove(keys[i], it.next());
										}
										potentialurls.remove(url);
										validurls.remove(url);
										kernellocationcache = null;
										return null;
									}
								});
								return IFuture.DONE;
							}
							
							public IFuture urlAdded(final URL url)
							{
								exta.scheduleStep(new IComponentStep()
								{
									public Object execute(IInternalAccess ia)
									{
										extensionblacklist = new HashSet(baseextensionblacklist);
										validurls.add(url);
										potentialurls.add(url);
										return null;
									}
								});
								return IFuture.DONE;
							}
						});
						
						ls.getAllURLs().addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								potentialurls.addAll((Collection) result);
								validurls.addAll((Collection) result);
								
								if (kerneldefaultlocations.isEmpty())
									ret.setResult(res);
								else
								{
									// Initialize default locations
									String[] dl = (String[]) kerneldefaultlocations.keySet().toArray(new String[kerneldefaultlocations.size()]);
									kerneldefaultlocations.clear();
									IResultListener loccounter = ia.createResultListener(new CounterResultListener(dl.length, ia.createResultListener(new DelegationResultListener(ret)
									{
										public void customResultAvailable(Object result)
										{
											ret.setResult(res);
										}
									}))
									{
										public void intermediateResultAvailable(Object result)
										{
											IModelInfo kernel = (IModelInfo) result;
											String[] exts = (String[]) SJavaParser.evaluateExpression(((UnparsedExpression)kernel.getProperties().get(KERNEL_EXTENSIONS)).getValue(), null);
											if (exts != null)
												for (int i = 0; i < exts.length; ++i)
													kerneldefaultlocations.put(exts[i], kernel.getFilename());
										}
									});
									
									ClassLoader cl = ls.getClassLoader();
									for (int i = 0; i < dl.length; ++i)
										loadModel(dl[i], null, cl).addResultListener(loccounter);
								}
							}
						}));
					}
				}));
			}
		}));
		return ret;
	}
	
	/**
	 * Load a model.
	 * 
	 * @param model
	 *            The model (e.g. file name).
	 * @param The
	 *            imports (if any).
	 * @return The loaded model.
	 */
	public IFuture loadModel(final String model, final String[] imports, final ClassLoader classloader)
	{
		return loadModel(model, imports, classloader, false);
	}

	/**
	 * Load a model.
	 * 
	 * @param model
	 *            The model (e.g. file name).
	 * @param The
	 *            imports (if any).
	 * @return The loaded model.
	 */
	public IFuture loadModel(final String model, final String[] imports, final ClassLoader classloader, boolean isrecur)
	{
		final Future ret = new Future();
		findKernel(model, imports, classloader, isrecur).addResultListener(ia.createResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
					((IComponentFactory) result).loadModel(model, imports, classloader).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
				else
					ret.setException(new RuntimeException("Factory not found: " + model));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(exception);
			}
		})));
		return ret;
	}

	/**
	 * Test if a model can be loaded by the factory.
	 * 
	 * @param model
	 *            The model (e.g. file name).
	 * @param The
	 *            imports (if any).
	 * @return True, if model can be loaded.
	 */
	public IFuture isLoadable(String model, String[] imports, ClassLoader classloader)
	{
		final Future ret = new Future();
		findKernel(model, imports, classloader).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
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
	public IFuture isStartable(final String model, final String[] imports, final ClassLoader classloader)
	{
		final Future ret = new Future();
		findKernel(model, imports, classloader).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
					((IComponentFactory) result).isStartable(model, imports, classloader).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
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
	 * Get the component type of a model.
	 * 
	 * @param model
	 *            The model (e.g. file name).
	 * @param The
	 *            imports (if any).
	 */
	public IFuture getComponentType(final String model, final String[] imports, final ClassLoader classloader)
	{
		final Future ret = new Future();
		findKernel(model, imports, classloader).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
				{
					((IComponentFactory) result).getComponentType(model, imports, classloader).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
				}
				else
				{
					ret.setException(new RuntimeException("Factory not found: " + model));
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(exception);
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
		return (String[]) componenttypes.toArray(new String[componenttypes.size()]);
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
	public IFuture createComponentInstance(final IComponentDescription desc,
			final IComponentAdapterFactory factory, final IModelInfo model, final String config,
			final Map arguments, final IExternalAccess parent,
			final RequiredServiceBinding[] bindings, final Future ret)
	{
		final Future res = new Future();
		findKernel(model.getFilename(), null, model.getClassLoader()).addResultListener(ia.createResultListener(new DelegationResultListener(res)
		{
			public void customResultAvailable(Object result)
			{
				((IComponentFactory) result).createComponentInstance(desc, factory, model, config, arguments, parent, bindings, ret).addResultListener(ia.createResultListener(new DelegationResultListener(res)));
			}
		}));
		return res;
	}
	
	/**
	 *  Attempts to find an active kernel factory, searching, loading and instantiating as required.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @param classloader Model classloader.
	 *  @return Factory instance of the kernel or null if no matching kernel was found.
	 */
	protected IFuture findKernel(final String model, final String[] imports, final ClassLoader classloader)
	{
		return findKernel(model, imports, classloader, false);
	}
	
	/**
	 *  Attempts to find an active kernel factory, searching, loading and instantiating as required.
	 *  
	 *  @param model The model for which the kernel is needed.
	 *  @param imports Model imports.
	 *  @param classloader Model classloader.
	 *  @return Factory instance of the kernel or null if no matching kernel was found.
	 */
	protected IFuture findKernel(final String model, final String[] imports, final ClassLoader classloader, final boolean isrecur)
	{
		if (extensionblacklist.contains(getModelExtension(model)))
			return IFuture.DONE;
		
		final Future ret = new Future();
		
		findActiveKernel(model, imports, classloader).addResultListener(ia.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
					ret.setResult(result);
				else
					findLoadableKernel(model, imports, classloader, isrecur).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							if (result != null)
								ret.setResult(result);
							else
							{
								if (!isrecur)
									extensionblacklist.add(getModelExtension(model));
								ret.setResult(null);
							}
						}
					}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Give warning?
				if (!isrecur)
					extensionblacklist.add(getModelExtension(model));
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
	 *  @param classloader Model classloader.
	 *  @return Factory instance of the kernel or null if no matching kernel was found.
	 */
	protected IFuture findActiveKernel(final String model, final String[] imports, final ClassLoader classloader)
	{
		final Future ret = new Future();
		SServiceProvider.getServices(ia.getServiceContainer(), IComponentFactory.class, RequiredServiceInfo.SCOPE_APPLICATION).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
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
					final IComponentFactory factory = (IComponentFactory) it.next();
					factory.isLoadable(model, imports, classloader).addResultListener(ia.createResultListener(new IResultListener()
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
	protected IFuture findLoadableKernel(final String model, final String[] imports, final ClassLoader classloader, boolean isrecur)
	{
		final Future ret = new Future();
		String dl = (String) kerneldefaultlocations.get(getModelExtension(model));
		if (dl != null)
			startLoadableKernel(model, imports, classloader, dl).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
		else
			return findKernelInCache(model, imports, classloader, isrecur);
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
	protected IFuture findKernelInCache(final String model, final String[] imports, final ClassLoader classloader, final boolean isrecur)
	{
		final Future ret = new Future();
		
		Collection kernels = kernellocationcache.getCollection(getModelExtension(model));
		String cachedresult = null;
		if (!kernels.isEmpty())
			cachedresult = (String) kernels.iterator().next();
		
		if (cachedresult != null)
		{
			startLoadableKernel(model, imports, classloader, cachedresult).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
		}
		else
		{
			if (potentialurls.isEmpty() || isrecur)
				ret.setResult(null);
			else
			{
				multiplexer.doCall(new IResultCommand()
				{
					public Object execute(Object args)
					{
						return searchPotentialUrls(getModelExtension(model));
					}
				}).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						findKernelInCache(model, imports, classloader, isrecur).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
					}
				}));
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
	protected IFuture startLoadableKernel(final String model, final String[] imports, final ClassLoader classloader, final String kernelmodel)
	{
		return multiplexer.doCall(kernelmodel, new IResultCommand()
		{
			public Object execute(Object args)
			{
//				System.out.println("Starting: " + kernelmodel);
				final Future ret = new Future();
				SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService) result;
						CreationInfo ci = new CreationInfo(ia.getComponentIdentifier());
						cms.createComponent(null, kernelmodel, ci, null).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								findActiveKernel(model, imports, classloader).addResultListener(ia.createResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										final IComponentFactory kernel = (IComponentFactory) result;
										if (kernel == null)
										{
											ret.setResult(null);
											return;
										}
										// If this is a new kernel, gather types and icons
										if (!activatedkernels.contains(kernelmodel))
										{
											final String[] types = kernel.getComponentTypes();
											componenttypes.addAll(Arrays.asList(types));
											
											IResultListener typecounter = ia.createResultListener(new CounterResultListener(types.length, true, ia.createResultListener(new DelegationResultListener(ret)
											{
												public void customResultAvailable(Object result)
												{
													SServiceProvider.getService(ia.getServiceContainer(), IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_APPLICATION).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
													{
														public void customResultAvailable(Object result)
														{
															((IMultiKernelNotifierService) result).fireTypesAdded(types).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
															{
																public void customResultAvailable(Object result)
																{
																	ret.setResult(kernel);
																};
															}));
														};
													}));
												};
											}))
											{
												public void intermediateResultAvailable(Object result)
												{
													iconcache.put(types[getCnt() - 1], result);
												};
											});
											for (int i = 0; i < types.length; ++i)
												kernel.getComponentTypeIcon(types[i]).addResultListener(typecounter);
											activatedkernels.add(kernelmodel);
										}
										else
											ret.setResult(kernel);
									}
								}));
							};
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
	 *  
	 *  @param extension Extension the kernel must support.
	 */
	protected IFuture searchPotentialUrls(final String extension)
	{
		final Future ret = new Future();
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService libservice = (ILibraryService) result;
				examineKernelModels(new ArrayList(potentialkernellocations), libservice).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						Map kernellocs = (Map) result;
						if (kernellocs != null && !kernellocs.isEmpty())
						{
							kernellocationcache.putAll(kernellocs);
							ret.setResult(null);
						}
						else
						{
							final URL url = (URL) potentialurls.iterator().next();
							potentialurls.remove(url);
							quickKernelSearch(url).addResultListener(ia.createResultListener(new IResultListener()
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
									
									ret.setResult(null);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									resultAvailable(null);
								}
							}));
						}
					}
				}));
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
	protected IFuture quickKernelSearch(URL url)
	{
		final Future ret = new Future();
		kernelSearch(url, new IFilter()
		{
			public boolean filter(Object obj)
			{
				String loc = (String) obj;
				return loc.substring(loc.lastIndexOf(File.separatorChar) + 1).startsWith("Kernel");
			}
		}).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
		return ret;
	}
	
	/**
	 *  Searches supplied URL for a potential kernel matching the filter and containing
	 *  a description of loadable file extensions.
	 *  
	 *  @param url The URL to search
	 *  @param prefilter Prefilter applied before further restrictions are applied.
	 */
	protected IFuture kernelSearch(final URL url, final IFilter prefilter)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService libservice = (ILibraryService) result;
				List modellocs = searchUrl(url, new IFilter()
				{
					public boolean filter(Object obj)
					{
						if (obj instanceof String)
						{
							String loc = (String) obj;
							if (prefilter.filter(obj) &&
							    !baseextensionblacklist.contains(getModelExtension(loc)) &&
							    //((String) obj).endsWith("component.xml") &&
							    !kernelblacklist.contains(loc.substring(loc.lastIndexOf(File.separatorChar) + 1)))
								return true;
						}
						return false;
					}
				});
				
				examineKernelModels(modellocs, libservice).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
			}
		}));
		return ret;
	}
	
	/**
	 *  Examines potential kernels whether their model can be loaded or loaded
	 *  with the help of another kernel that can be found.
	 *  
	 *  @param modellocs List of locations of potential kernels.
	 *  @param libservice Library service.
	 *  @return Map of viable kernels with extension they support.
	 */
	protected IFuture examineKernelModels(List modellocs, final ILibraryService libservice)
	{
		final Map kernellocs = new HashMap();
		if (modellocs.isEmpty())
			return new Future(kernellocs);
		final Future ret = new Future();
		final IResultListener kernelCounter = ia.createResultListener(new CounterResultListener(modellocs.size(), true, new DefaultResultListener()
		{
			public void resultAvailable(
					Object result)
			{
				ret.setResult(kernellocs);
			}
		})
		{
			public void intermediateResultAvailable(Object result)
			{
				IModelInfo kernelmodel = (IModelInfo) result;
				try
				{
					String[] exts = null;
					Object extprop = kernelmodel.getProperties().get(KERNEL_EXTENSIONS);
					if (extprop instanceof String)
					{
						exts = (String[]) SJavaParser.evaluateExpression((String) kernelmodel.getProperties().get(KERNEL_EXTENSIONS), null);
					}
					else if (extprop instanceof UnparsedExpression)
					 	exts = (String[]) SJavaParser.evaluateExpression(((UnparsedExpression)kernelmodel.getProperties().get(KERNEL_EXTENSIONS)).getValue(), null);
					//String[] exts = (String[]) kernelmodel.getProperties().get(KERNEL_EXTENSIONS);
					if (exts != null)
					{
						for (int i = 0; i < exts.length; ++i)
							kernellocs.put(exts[i], kernelmodel.getFilename());
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		for (Iterator it2 = modellocs.iterator(); it2.hasNext();)
		{
			final String kernelloc = (String) it2.next();
			loadModel(kernelloc, null, libservice.getClassLoader(), true).addResultListener(ia.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					if (result != null)
					{
						potentialkernellocations.remove(kernelloc);
						kernelCounter.resultAvailable(result);
					}
					else
					{
						potentialkernellocations.add(kernelloc);
						kernelCounter.exceptionOccurred(new RuntimeException());
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
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
		File file = new File(url.getFile());
		if (file.isDirectory())
			return searchDirectory(file, filter, false);
		else if (file.getName().endsWith(".jar"))
			return searchJar(file, filter);

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
				if (prependDir)
					ret.add(dir.getName().concat(File.separator).concat(
							content[i].getName()));
				else
					ret.add(content[i].getName());
			}
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
		List ret = new ArrayList();
		try
		{
			JarFile jarFile = new JarFile(jar);
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
	protected static String getModelExtension(String model)
	{
		int firstpoint = Math.max(0, model.lastIndexOf(File.separatorChar));
		firstpoint = model.indexOf('.', firstpoint);
		
		if (firstpoint < 0 || firstpoint == (model.length() - 1))
			return null;
		
		String ext = model.substring(firstpoint + 1);
		if (ext.equals("class"))
		{
			if (model.endsWith("Agent.class"))
				ext = "Agent.class";
			else
				return null;
		}
		
		return ext;
	}
}
