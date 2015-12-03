package jadex.kernelbase;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
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
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;


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
	protected MultiCollection<String, String> kerneldefaultlocations;
	
	/** Cache of known factories */
	protected Map<String, IComponentFactory> factorycache;
	
	/** Cache of kernel locations */
	protected MultiCollection<String, String> kernellocationcache;
	
	/** URLs of the kernels */
	protected MultiCollection<URI, String> kerneluris;
	
	/** Set of potential URLs for kernel searches */
	protected Set<URI> potentialuris;
	
	/** Currently valid URLs */
	protected Set<URI> validuris;
	
	/** Set of kernels that have been active at one point */
	protected Set<String> activatedkernels;
	
	/** Flag if active kernels has changed. */
	protected boolean activekernelsdirty = true;
	
	/** Currently supported types */
	protected Set<String> componenttypes;
	
	/** Cache of component icons */
	protected Map<String, byte[]> iconcache;
	
	/** Base Blacklist of extension for which there is no factory */
	protected Set<String> baseextensionblacklist;

	/** Blacklist of extension for which there is no factory */
//	protected Set extensionblacklist;
	
	/** Kernel blacklist */
	protected Set<String> kernelblacklist;
	
	/** Unloadable kernel locations that may become loadable later. */
	protected Set<String> potentialkernellocations;
	
	/** Call Multiplexer */
	protected CallMultiplexer multiplexer;
	
	/** The listeners. */
	protected List<IMultiKernelListener> listeners;
	
	/** The service identifier. */
	@ServiceIdentifier(IComponentFactory.class)
	protected IServiceIdentifier sid;
	
	/** Flag whether the service has started */
	protected boolean started;
	
	/** Library service listener */
	protected ILibraryServiceListener liblistener;
	
	/** The library service. */
	protected ILibraryService libservice;

	public static final String MULTIFACTORY = "multifactory";
	
	public static final Map<String, Object> props = SUtil.createHashMap(new String[]{MULTIFACTORY}, new Object[]{Boolean.TRUE});
	
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
		this.factorycache = new HashMap<String, IComponentFactory>();
		this.kernellocationcache = new MultiCollection<String, String>();
		this.kerneluris = new MultiCollection();
		this.potentialuris = new LinkedHashSet<URI>();
		this.validuris = new HashSet<URI>();
		this.multiplexer = new CallMultiplexer();
		this.baseextensionblacklist = new HashSet<String>();
//		if (extensionblacklist != null)
//			Arrays.asList(extensionblacklist);
//		this.baseextensionblacklist.add(null);
		
		kerneldefaultlocations = new MultiCollection<String, String>();
		if(defaultLocations != null)
		{
			for (int i = 0; i < defaultLocations.length; ++i)
			{
				kerneldefaultlocations.add(null, defaultLocations[i]);
			}
		}
		
		activatedkernels = new HashSet<String>();
		componenttypes = new HashSet<String>();
		iconcache = new HashMap<String, byte[]>();
		this.kernelblacklist = new HashSet<String>();
		if(kernelblacklist != null)
		{
			this.kernelblacklist.addAll(Arrays.asList(kernelblacklist));
		}
//		this.extensionblacklist = new HashSet(baseextensionblacklist);
		this.potentialkernellocations = new HashSet<String>();
		this.listeners = new ArrayList<IMultiKernelListener>();
		started = false;
	}
	
	/**
	 *  Starts the service.
	 */
	@ServiceStart
	public IFuture<Void> startService()
	{
		if (started)
			return IFuture.DONE;
		
		String[] blarray = (String[]) ia.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("ignoreextensions");
//		System.out.println(Arrays.toString(blarray));
		if (blarray != null)
			baseextensionblacklist.addAll(Arrays.asList(blarray));
		
		final Future ret = new Future()
		{
			public void setResult(Object result)
			{
				started = true;
				super.setResult(result);
			}
		};
		
		libservice	= SServiceProvider.getLocalService(ia, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		
		final IExternalAccess exta = ia.getExternalAccess();
		liblistener = new ILibraryServiceListener()
		{
			public IFuture resourceIdentifierRemoved(IResourceIdentifier parid, final IResourceIdentifier rid)
			{
				exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						URI uri = rid.getLocalIdentifier().getUri();
						Collection<String> affectedkernels = (Collection<String>)kerneluris.remove(uri);
						if (affectedkernels != null)
						{
							String[] keys = (String[]) kernellocationcache.keySet().toArray(new String[0]);
							for(int i = 0; i < keys.length; ++i)
							{
								for(Iterator<String> it = affectedkernels.iterator(); it.hasNext(); )
								{
//									System.out.println("rid removed: "+uri+", "+keys[i]);
									kernellocationcache.removeObject(keys[i], it.next());
								}
							}
						}
						potentialuris.remove(uri);
						validuris.remove(uri);
						return IFuture.DONE;
					}
				});
				return IFuture.DONE;
			}
			
			public IFuture resourceIdentifierAdded(IResourceIdentifier parid, final IResourceIdentifier rid, boolean rem)
			{
				final URI uri = rid.getLocalIdentifier().getUri();
				String regex = (String) ia.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("kerneluriregex");
				if (Pattern.matches(regex!=null ? regex : "", uri.toString()))
				{
					exta.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
//							extensionblacklist = new HashSet(baseextensionblacklist);
							validuris.add(uri);
							potentialuris.add(uri);
							return IFuture.DONE;
						}
					});
				}
				return IFuture.DONE;
			}
		};
		
		libservice.addLibraryServiceListener(liblistener);
		
		libservice.getAllURLs().addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				String regexstr = (String) ia.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("kerneluriregex");
				Pattern regex = Pattern.compile(regexstr!=null ? regexstr : "");
				
//				potentialurls.addAll();
//				validurls.addAll((Collection) result);
				
				if (result != null)
				{
					for (URL url : ((Collection<URL>) result))
					{
						try
						{
							URI uri = url.toURI();
							if (regex.matcher(uri.toString()).matches())
							{
								potentialuris.add(uri);
								validuris.add(uri);
							}
						}
						catch (URISyntaxException e)
						{
						}
						
					}
				}
				
				// Sort uris (for repeatability during debugging)
//				List	tmp	= new ArrayList(potentialuris);
//				Collections.sort(tmp);
//				potentialuris.clear();
//				potentialuris.addAll(tmp);
				
				if(kerneldefaultlocations.isEmpty())
					ret.setResult(null);
				else
				{
					// Initialize default locations
//							String[] dl = (String[])kerneldefaultlocations.keySet().toArray(new String[kerneldefaultlocations.size()]);
					String[] dl = kerneldefaultlocations.get(null) == null? new String[0] : (String[]) ((Collection) kerneldefaultlocations.get(null)).toArray(new String[kerneldefaultlocations.size()]);
					kerneldefaultlocations.clear();
					IResultListener loccounter = ia.getComponentFeature(IExecutionFeature.class).createResultListener(
						new CounterResultListener(dl.length, ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
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
											kerneldefaultlocations.add(exts[i], kernel.getFilename());
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
				
		
		return ret;
	}
	
	/**
	 *  Stops the service.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdownService()
	{
		final Future<Void> ret = new Future<Void>();
		
		ia.getComponentFeature(IRequiredServicesFeature.class).searchService(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
//		if(model.indexOf("ich")!=-1)
//			System.out.println("loadModel: "+model);
		
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
//		if(model.indexOf("ich")!=-1)
//			System.out.println("loadModel2: "+model);
		
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		
		findKernel(model, imports, rid, isrecur).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if (result != null)
					((IComponentFactory)result).loadModel(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
				else
					ret.setException(new RuntimeException("Factory not found: " + model));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
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
//		Collection tfactories = SServiceProvider.getServices((IServiceProvider) ia.getServiceContainer(), IComponentFactory.class, RequiredServiceInfo.SCOPE_APPLICATION).get();
//		if (tfactories.size() <= 1)
//		{
//			System.out.println(ServiceCall.getCurrentInvocation().getCaller());
//			System.out.println("FALSEs");
//			return new Future<Boolean>(false);
//		}
//		if(model.endsWith("BDI.class"))
//			System.out.println("isLoadable: "+model);

		final Future<Boolean> ret = new Future<Boolean>();
		findKernel(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
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
		findKernel(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result != null)
					((IComponentFactory) result).isStartable(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
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
		findKernel(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result != null)
				{
					((IComponentFactory)result).getComponentType(model, imports, rid)
						.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
				}
				else
				{
					ret.setException(new ServiceNotFoundException("Factory not found: " + model));
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
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
//		System.out.println("multi factory icon: "+type+" "+iconcache.containsKey(type));
		
		byte[] icon = iconcache.get(type);
		
		if (icon == null)
		{
			ITerminableIntermediateFuture<IComponentFactory> ffut = SServiceProvider.getServices(ia, IComponentFactory.class, RequiredServiceInfo.SCOPE_APPLICATION);
			Collection<IComponentFactory> facs = ffut.get();
			if (facs != null)
			{
				for (IComponentFactory fac : facs)
				{
					if(!((IService)fac).getServiceIdentifier().equals(sid))
					{
						icon = fac.getComponentTypeIcon(type).get();
					
						if (icon != null)
						{
							iconcache.put(type, icon);
							break;
						}
					}
				}
			}
		}
		
		return new Future<byte[]>(icon);
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
//		return Collections.EMPTY_MAP;
		return props;
	}

//	/**
//	 * Create a component instance.
//	 * 
//	 * @param factory	The component adapter factory.
//	 * @param model	The component model.
//	 * @param config	The name of the configuration (or null for default configuration)
//	 * @param arguments	The arguments for the component as name/value pairs.
//	 * @param parent	The parent component (if any).
//	 * @return An instance of a component and the corresponding adapter.
//	 */
//	@Excluded
//	public IFuture<Tuple2<IComponentInterpreter, IComponentAdapter>> createComponentInstance(final IComponentDescription desc,
//			final IPlatformComponentFactory factory, final IModelInfo model, final String config,
//			final Map<String, Object> arguments, final IExternalAccess parent,
//			final RequiredServiceBinding[] bindings, final boolean copy, final boolean realtime, final boolean persist,
//			final IPersistInfo persistinfo,
//			final IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> ret)
//	{
	/**
	 *  Get the component features for a model.
	 *  @param model The component model.
	 *  @return The component features.
	 */
	@Excluded
	public IFuture<Collection<IComponentFeatureFactory>> getComponentFeatures(final IModelInfo model)
	{
//		if(model.getName().indexOf("ich")!=-1)
//			System.out.println("createComponentInstance: "+model.getName());
		
//		IComponentFactory fac = (IComponentFactory)factorycache.get(getModelExtension(model.getFilename()));
		IComponentFactory fac = (IComponentFactory) getCacheResultForModel(model.getFilename(), factorycache);
		if(fac != null)
			return fac.getComponentFeatures(model);
		
		final Future<Collection<IComponentFeatureFactory>> res = new Future<Collection<IComponentFeatureFactory>>();
		
		findKernel(model.getFilename(), null, model.getResourceIdentifier()).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(res)
		{
			public void customResultAvailable(IComponentFactory result)
			{
				((IComponentFactory)result).getComponentFeatures(model).addResultListener(new DelegationResultListener(res));
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
		IResultListener counter = ia.getComponentFeature(IExecutionFeature.class).createResultListener(
			new CounterResultListener(ls.length, true, ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret))));
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
		IResultListener counter = ia.getComponentFeature(IExecutionFeature.class).createResultListener(
			new CounterResultListener(ls.length, true, ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret))));
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
	protected IFuture<IComponentFactory> findKernel(final String model, final String[] imports, final IResourceIdentifier rid)
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
		if (isInExtensionBlacklist(model, baseextensionblacklist))
			return IFuture.DONE;
		
//		if(model.toString().indexOf("ich")!=-1)
//			System.out.println("findKernel: "+model);
		
//		IComponentFactory fac = (IComponentFactory)factorycache.get(ext);
		IComponentFactory fac = (IComponentFactory) getCacheResultForModel(model, factorycache);
		if(fac != null)
			return new Future(fac);
		
		final Future ret = new Future();
		
//		final ClassLoader classloader =  libservice.getClassLoader(rid);
		
		findActiveKernel(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
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
						.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
//							System.out.println("model: "+model+" "+result);
							if(result != null)
							{
								ret.setResult(result);
							}
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
		SServiceProvider.getServices(ia, IComponentFactory.class, RequiredServiceInfo.SCOPE_APPLICATION)
			.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
		{
			public void exceptionOccurred(Exception exception)
			{
				super.exceptionOccurred(exception);
			}
			
			public void customResultAvailable(Object result)
			{
				final Collection factories = (Collection) result;
//				if(model.indexOf("ich")!=-1)
//					System.out.println("found factories: "+result);
				
				final IResultListener factorypicker = ia.getComponentFeature(IExecutionFeature.class).createResultListener(
					new CollectionResultListener(factories.size(), true, ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
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
					if(((IService)factory).getServiceIdentifier().equals(sid))
					{
//						if(model.indexOf("ich")!=-1)
//							System.out.println("removed: "+factory);
						factorypicker.exceptionOccurred(new RuntimeException());
						continue;
					}
					
//					System.out.println("Trying isloadable :" + factory + " for " + model);
					factory.isLoadable(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
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
							exception.printStackTrace();
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
//		if(model.toString().indexOf("ich")!=-1)
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
//		if(model.toString().indexOf("ich")!=-1)
//			System.out.println("findKernelInCache0: "+model);
		final Future ret = new Future();
		
//		Collection kernels = kernellocationcache.getCollection(getModelExtension(model));
		Tuple2<String, Object> cachedkernels = getCacheKeyValueForModel(model, (Map) kernellocationcache);
		final Object kernelsext = cachedkernels != null? cachedkernels.getFirstEntity(): null;
		Collection kernels = cachedkernels != null? (Collection) cachedkernels.getSecondEntity() : null;
		String cachedresult = null;
		if(kernels != null && !kernels.isEmpty())
			cachedresult = (String) kernels.iterator().next();
		
		if(cachedresult != null)
		{
//			if(model.toString().indexOf("ich")!=-1)
//				System.out.println("findKernelInCache1: "+model+", "+cachedresult);
			final String	kernelmodel	= cachedresult;	
			startLoadableKernel(model, imports, rid, kernelmodel)
				.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
			{
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("remove: "+kernelsext+", "+kernelmodel);
					kernellocationcache.removeObject(kernelsext, kernelmodel);
					findKernelInCache(model, imports, rid, isrecur)
						.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
				}
			}));
		}
		else
		{
//			if(model.toString().indexOf("ich")!=-1)
//				System.out.println("findKernelInCache2a: "+model+", "+potentialuris+", "+isrecur);
			if(potentialuris.isEmpty() && !hasLoadablePotentialKernels() || isrecur)
//			if(!hasLoadablePotentialKernels() || isrecur)
			{
//				if(model.toString().indexOf("ich")!=-1)
//					System.out.println("findKernelInCache2: "+model+", "+potentialuris+", "+isrecur);
				ret.setResult(null);
			}
			else
			{
				multiplexer.doCall(new IResultCommand()
				{
					public Object execute(Object args)
					{
//						if(model.toString().indexOf("ich")!=-1)
//							System.out.println("findKernelInCache3: "+model+", "+potentialuris+", "+isrecur);
						return searchPotentialUrls(rid);
					}
				}).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						findKernelInCache(model, imports, rid, isrecur)
							.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
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
		for(String loc: potentialkernellocations)
		{
//			if(loc.toLowerCase().indexOf("multi")==-1)
//				System.out.println("loc: "+loc+", "+getCacheKeyValueForModel((String)loc, kernellocationcache)+", "+kernellocationcache);

			ret	= getCacheKeyValueForModel((String)loc, (Map) kernellocationcache)!=null;
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
//		System.out.println("startLoadableKernel: "+model+" "+kernelmodel+" "+kernelmodel.length());

		return multiplexer.doCall(kernelmodel, new IResultCommand()
		{
			public Object execute(Object args)
			{
//				IComponentFactory fac = (IComponentFactory) getCacheResultForModel(model, factorycache);
//				if (fac != null)
//				{
//					return new Future(fac);
//				}
//				else
//				{
//					System.out.println("no fac found for: "+model);
//				}
				
//				System.out.println("Starting kernel: " + kernelmodel);
				final Future ret = new Future();
//				ret.addResultListener(new IResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						System.out.println("call fini: "+model);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						System.out.println("call fini ex: "+model+" "+exception);
//					}
//				});
				
				findActiveKernel(kernelmodel, null, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
//						System.out.println("Starting kernel1: " + kernelmodel);
						IComponentFactory	fac	= (IComponentFactory)result;
						fac.loadModel(kernelmodel, null, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
//								System.out.println("Starting kernel2: " + kernelmodel);
								final IModelInfo	info	= (IModelInfo)result;
								SServiceProvider.getService(ia, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
								{
									public void exceptionOccurred(Exception exception)
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
//												System.out.println("multi creates factory: "+kernelmodel);
												cms.createComponent(fname, kernelmodel, ci, ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
												{
													public void resultAvailable(Object result)
													{
//														System.out.println("Killed kernel4: " + kernelmodel);
														activekernelsdirty = true;
														for(int i = 0; i < kexts.length; ++i)
															factorycache.remove(kexts[i]);
													}
													
													public void exceptionOccurred(Exception exception)
													{
//														System.out.println("Killed kernel5: " + kernelmodel+", "+exception);
//														exception.printStackTrace();
														resultAvailable(null);
													}
												})).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
												{
													public void resultAvailable(Object result)
													{
//														System.out.println("Starting kernel6: " + kernelmodel);
														findActiveKernel(model, imports, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
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
//																	System.out.println("putting in cache: "+kexts[i]+" "+kernel);
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
																		final IResultListener typecounter = ia.getComponentFeature(IExecutionFeature.class).createResultListener(new CounterResultListener(types.length, true, ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
																		{
																			public void customResultAvailable(Object result)
																			{
																				SServiceProvider.getService(ia, IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_APPLICATION)
																					.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
																				{
																					public void customResultAvailable(Object result)
																					{
																						MultiFactory.this.fireTypesAdded(types).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
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
//																					System.out.println("adding icon: "+types[fi]);
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
//														System.out.println("Starting kernel7: " + kernelmodel+", "+exception);
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
		final IResultListener reslis = ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Map kernellocs = (Map) result;
				if (kernellocs != null && !kernellocs.isEmpty())
				{
//					System.out.println("searchPotentialURLs1: "+kernellocs+", "+kernellocationcache);
//					kernellocationcache.putAll(kernellocs);
					kernellocationcache.addAll(kernellocs);
					activekernelsdirty = true;
//					System.out.println("searchPotentialURLs1b: "+kernellocs+", "+kernellocationcache);
					ret.setResult(null);
				}
				else
				{
					final URI uri = potentialuris.iterator().next();
//					if(url.toString().indexOf("bdi")!=-1)
//						System.out.println("searchPotentialURLs2: "+url);
					quickKernelSearch(uri, rid).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Map<String, String>>()
					{
						public void resultAvailable(Map<String, String> kernelmap)
						{
							if(kernelmap != null && validuris.contains(uri))
							{
//								System.out.println("searchPotentialURLs3: "+uri+", "+result+", "+kernellocationcache);
//								Map kernelmap = (Map) result;
//								kernellocationcache.putAll(kernelmap);
								kernellocationcache.addAll(kernelmap);
								activekernelsdirty = true;
//								System.out.println("searchPotentialURLs3b: "+uri+", "+result+", "+kernellocationcache);
								for (Iterator<String> it = kernelmap.values().iterator(); it.hasNext(); )
									kerneluris.add(uri, it.next());
							}
							potentialuris.remove(uri);
//							System.out.println("Remove: " + url + " size " + potentialurls.size());
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							potentialuris.remove(uri);
							resultAvailable(null);
						}
					}));
				}
			}
		});
		
		ia.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if (activekernelsdirty)
				{
//					System.out.println("searchPotentialURLs4: ");
					activekernelsdirty = false;
					examineKernelModels(new ArrayList(potentialkernellocations), rid).addResultListener(reslis);
				}
				else
				{
//					System.out.println("searchPotentialURLs5: ");
					reslis.resultAvailable(null);
				}
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Searches supplied URL for a potential kernel. This method calls kernelSearch()
	 *  with a prefilter that excludes all files except the ones starting with "Kernel".
	 *  
	 *  @param url The URL to search
	 */
	protected IFuture<Map<String, String>> quickKernelSearch(URI uri, IResourceIdentifier rid)
	{
		return kernelSearch(uri, new IFilter()
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
	protected IFuture<Map<String, String>> kernelSearch(final URI uri, final IFilter prefilter, IResourceIdentifier rid)
	{
//		System.out.println("URLSearhc: " + uri.toString());
		List<String> modellocs = searchUri(uri, new IFilter()
		{
			public boolean filter(Object obj)
			{
				if (obj instanceof String)
				{
					String loc = (String) obj;
					
					for (Object oblstr : baseextensionblacklist)
					{
						String blstr = (String) oblstr;
						
						if (loc.toLowerCase().endsWith(blstr))
						{
//							System.out.println(loc + " false for " + blstr);
							return false;
						}
					}
					
					for (Object oblstr : kernelblacklist)
					{
						//!kernelblacklist.contains(loc.substring(loc.lastIndexOf(File.separatorChar) + 1))
						String blstr = (String) oblstr;
						
						if (loc.endsWith(blstr))
						{
//							System.out.println(loc + " false2 for " + blstr);
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
	protected IFuture<Map<String, String>> examineKernelModels(final List<String> modellocs, IResourceIdentifier rid)
	{
//		if(modellocs.toString().indexOf("ich")!=-1)
//			System.out.println("examineKernelModels0: "+modellocs);
		final Map<String, String> kernellocs = new HashMap();
		if (modellocs.isEmpty())
			return new Future(kernellocs);
		
		final Future<Map<String, String>> ret = new Future<Map<String, String>>();
		final IResultListener kernelCounter = ia.getComponentFeature(IExecutionFeature.class).createResultListener(new CounterResultListener(modellocs.size(), true,
			new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
//				if(modellocs.toString().indexOf("ich")!=-1)
//					System.out.println("examineKernelModels1: "+modellocs+", "+kernellocs);
				super.customResultAvailable(kernellocs);
			}
			public void exceptionOccurred(Exception e)
			{
//				e.printStackTrace();
				super.exceptionOccurred(e);
			}
		}));

		for(Iterator<String> it2 = modellocs.iterator(); it2.hasNext();)
		{
			final String kernelloc = it2.next();
			loadModel(kernelloc, null, rid, true)
				.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IModelInfo>()
			{
				public void resultAvailable(final IModelInfo modelinfo)
				{
//					if(modellocs.toString().indexOf("ich")!=-1)
//						System.out.println("Tried to load model for kernel: " + kernelloc + " model " + modelinfo);
					if(modelinfo!=null)
					{
						potentialkernellocations.remove(kernelloc);
						libservice.getClassLoader(modelinfo.getResourceIdentifier())
							.addResultListener(new IResultListener<ClassLoader>()
						{
							public void resultAvailable(ClassLoader result)
							{
								String[] exts = (String[])modelinfo.getProperty(KERNEL_EXTENSIONS, result);
//								if(modellocs.toString().indexOf("ich")!=-1)
//									System.out.println("Kernel extensions for kernel " + kernelloc + " " + SUtil.arrayToString(exts));
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
//						if(modellocs.toString().indexOf("bdi")!=-1)
//						{
//							System.out.println("potential: "+hasLoadablePotentialKernels());
//						}
						kernelCounter.exceptionOccurred(new RuntimeException());
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("Tried to load model for kernel: " + kernelloc + " but failed. "+exception);
					resultAvailable(null);
				}
			}));
		}
		
		return ret;
	}
	
	/**
	 *  Searches an URI, accepts both directory and .jar-based URLs.
	 *  @param uri The URI.
	 *  @param filter The search filter.
	 *  @return List of file locations matching the filter.
	 */
	protected List<String> searchUri(URI uri, IFilter filter)
	{
		try
		{
			File file = new File(uri);
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
	protected List<String> searchDirectory(File dir, IFilter filter, boolean prependDir)
	{
//		System.out.println("Searching dir: " + dir.getAbsolutePath());
		List<String> ret = new ArrayList<String>();
		File[] content = dir.listFiles();
		for (int i = 0; i < content.length; ++i)
		{
			if (content[i].isDirectory())
			{
				List<String> subList = searchDirectory(content[i], filter, true);
				for (Iterator<String> it = subList.iterator(); it.hasNext();)
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
	protected List<String> searchJar(File jar, IFilter filter)
	{
//		System.out.println("Searching jar: " + jar.getAbsolutePath());
		List<String> ret = new ArrayList<String>();
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
	protected List<String> searchApk(File apk, IFilter filter)
	{
//		System.out.println("Searching apk: " + apk.getAbsolutePath());
		List<String> ret = new ArrayList<String>();
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
//			System.out.println("Warning: File not found: " + apk.getAbsolutePath());
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
		Tuple2<String, Object> ret = getCacheKeyValueForModel(model, map);
		return ret != null? ret.getSecondEntity() : null;
	}
	
	/**
	 *  Gets key/value for a cached object based on model name.
	 *  
	 *  @param model The model.
	 *  @param map The cache.
	 *  @return A cache hit or null.
	 */
	protected Tuple2<String, Object> getCacheKeyValueForModel(String model, Map<String, Object> map)
	{
		Tuple2<String, Object> ret = null;

		if (model != null && map != null)
		{
			for (Map.Entry<String, Object> entry : map.entrySet())
			{
				String ext = entry.getKey();
				if (model.endsWith(ext))
				{
					ret = new Tuple2<String, Object>(entry.getKey(), entry.getValue());
					
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
