package jadex.kernelbase;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IMultiKernelNotifierService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.FileFilter;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.SClassReader.AnnotationInfos;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CallSequentializer;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;

/**
 *  Multi factory for dynamically loading kernels.
 */
@Service
public class MultiFactory implements IComponentFactory, IMultiKernelNotifierService
{
	/** Kernel model property for extensions */
	protected static final String KERNEL_EXTENSIONS = "kernel.types";
	
	/** The internal access. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The listeners. */
	protected List<IMultiKernelListener> listeners = new ArrayList<>();
	
	/** Cache of component icons */
	protected Map<String, byte[]> iconcache = new HashMap<>();
	
	/** The service identifier. */
	@ServiceIdentifier(IComponentFactory.class)
	protected IServiceIdentifier sid;
	
	/** Currently supported types (loadable suffixes) */
	protected Set<String> componenttypes = new HashSet<>();
	
	/** The scanned kernel files (suffix -> {(classname, suffixes)} ). */
	protected Map<String, Collection<Tuple2<String, Set<String>>>> kernelfiles;

	/** The started factories (kernel classname -> kernel component). */
	protected Map<String, IComponentIdentifier> kernels = new HashMap<>();
	
	/** The started flag (because init is invoked twice, service impl for 2 services. */
	protected boolean inited;
	
	/** The dirty flag (when classpath changes). */
	protected boolean dirty;
	
	public static final String MULTIFACTORY = "multifactory";

	/** Used in SComponentFactory to reorder checks (multi last). Still necessary?!. */
	public static final Map<String, Object> props = SUtil.createHashMap(new String[]{MULTIFACTORY}, new Object[]{Boolean.TRUE});

	/** The sequentializer to execute getNewFactory() one by one and not interleaved. */
	protected CallSequentializer<IComponentFactory> getnewfac = new CallSequentializer<IComponentFactory>(new IResultCommand<IFuture<IComponentFactory>, Object[]>()
	{
		public IFuture<IComponentFactory> execute(Object[] args)
		{
			return getNewFactory((String)args[0], (String[])args[1], (IResourceIdentifier)args[2]);
		}
	});
	
	/**
	 *  Starts the service.
	 */
	@ServiceStart
	public IFuture<Void> startService()
	{
		if(inited)
			return IFuture.DONE;
		inited = true;
		
		// add data for implicitly started micro factory
		componenttypes.add(".class");
		kernels.put("jadex.micro.KernelMicroAgent", null);
		
		// Rescan on any changes in the library service
		
		ILibraryService libservice= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
		final IExternalAccess exta = agent.getExternalAccess();
		ILibraryServiceListener liblistener = new ILibraryServiceListener()
		{
			public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, final IResourceIdentifier rid)
			{
				exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						dirty = true;
						return IFuture.DONE;
					}
				});
				return IFuture.DONE;
			}
			
			public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, final IResourceIdentifier rid, boolean rem)
			{
				exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						dirty = true;
						return IFuture.DONE;
					}
				});
				return IFuture.DONE;
			}
		};
		libservice.addLibraryServiceListener(liblistener);
		
		return IFuture.DONE;
	}
	
	/**
	 * Get a default icon for a component type.
	 */
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
//		System.out.println("multi factory icon: "+type+" "+iconcache.containsKey(type));
		
		byte[] icon = iconcache.get(type);
		
		if(icon == null)
		{
			for(IComponentFactory fac : getFactories())
			{
				if(!((IService)fac).getId().equals(sid))
				{
					icon = fac.getComponentTypeIcon(type).get();
				
					if(icon != null)
					{
						iconcache.put(type, icon);
						break;
					}
				}
			}
		}
		
		return new Future<byte[]>(icon);
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(String model, String[] imports, IResourceIdentifier rid)
	{
		if(!isLoadable(model))
			return new Future<IModelInfo>(new RuntimeException("Cannot be loaded: "+model));
		
		Future<IModelInfo> ret = new Future<>();
		
		getFactoryForModel(model, imports, rid, null).addResultListener(new ExceptionDelegationResultListener<IComponentFactory, IModelInfo>(ret)
		{
			public void customResultAvailable(IComponentFactory fac) throws Exception
			{
				fac.loadModel(model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get a factory for a model.
	 *  
	 *  1) will check running factories
	 *  2) if none was found will check if a new can be started
	 *  
	 *  @return A factory that can load the model (or null if none was found).
	 */
	protected IFuture<IComponentFactory> getFactoryForModel(String model, String[] imports, IResourceIdentifier rid, Iterator<IComponentFactory> it)
	{
//		System.out.println("getFactory: "+model);
		
		Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
		getRunningFactory(model, imports, rid, null).addResultListener(new IResultListener<IComponentFactory>()
		{
			public void resultAvailable(IComponentFactory fac)
			{
				ret.setResult(fac);
			}

			public void exceptionOccurred(Exception exception)
			{
				getnewfac.call(new Object[]{model, imports, rid}).addResultListener(new DelegationResultListener<>(ret));
//				getNewFactory(model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  If there are other kernel specs for the current filename suffix start and test them.
	 *  Returns a new factory that can load the model (if any).
	 */
	protected IFuture<IComponentFactory> getNewFactory(String model, String[] imports, IResourceIdentifier rid)
	{
//		System.out.println("getNewFactory: "+model);
		
		Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
		Map<String, Collection<Tuple2<String, Set<String>>>> kernelfiles = getKernelFiles();
		
		Set<Tuple2<String, Set<String>>> found = new HashSet<>();
		for(Map.Entry<String, Collection<Tuple2<String, Set<String>>>> entry: kernelfiles.entrySet())
		{
			if(model.endsWith(entry.getKey()))
			{
				found.addAll(entry.getValue());
			}
		}
		
//		IComponentManagementService cms	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));

		final Iterator<Tuple2<String, Set<String>>> it = found.iterator();
		
		Runnable start = new Runnable()
		{
			public void run() 
			{
				if(it.hasNext())
				{
					Tuple2<String, Set<String>> f = it.next();
					
					if(kernels.containsKey(f.getFirstEntity()))
					{
						run();
					}
					else
					{	
						kernels.put(f.getFirstEntity(), null);
						
						CreationInfo ci = new CreationInfo(agent.getId());
						ci.setFilename(f.getFirstEntity()+".class");
						
						agent.createComponent(null, ci, new IResultListener<Collection<Tuple2<String, Object>>>()
						{
							public void resultAvailable(Collection<Tuple2<String, Object>> result)
							{
								System.out.println("Killed kernel: " + f);
								kernels.remove(f.getFirstEntity());
							}
							
							public void exceptionOccurred(Exception exception)
							{
//								System.out.println("Killed kernel: " + f+", "+exception);
								kernels.remove(f.getFirstEntity());
							}
//						cms.createComponent(null, f.getFirstEntity()+".class", ci, new IResultListener<Collection<Tuple2<String, Object>>>()
//						{
//							public void resultAvailable(Collection<Tuple2<String, Object>> result)
//							{
//								System.out.println("Killed kernel: " + f);
//								kernels.remove(f.getFirstEntity());
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
////								System.out.println("Killed kernel: " + f+", "+exception);
//								kernels.remove(f.getFirstEntity());
//							}
						}).addResultListener(new IResultListener<IExternalAccess>()
						{
							public void resultAvailable(IExternalAccess exta)
							{
//								System.out.println("started factory: "+cid);
								kernels.put(f.getFirstEntity(), exta.getId());
								
								ServiceQuery<IComponentFactory> q = new ServiceQuery<IComponentFactory>(IComponentFactory.class);
								q.setProvider(exta.getId());
								IComponentFactory fac = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(q);
								
								// If this is a new kernel, gather types and icons
								final String[] types = fac.getComponentTypes();
								componenttypes.addAll(Arrays.asList(types));
									
								if(SReflect.HAS_GUI)
								{
									fireTypesAdded(types);
									
									for(int i = 0; i < types.length; ++i)
									{
										final int fi = i;
										fac.getComponentTypeIcon(types[i]).addResultListener(new IResultListener<byte[]>()
										{
											public void resultAvailable(byte[] result)
											{
//												System.out.println("adding icon: "+types[fi]);
												iconcache.put(types[fi], result);
											}
											
											public void exceptionOccurred(Exception exception)
											{
											}
										});
									}
								}
								
								fac.isLoadable(model, imports, rid).addResultListener(new IResultListener<Boolean>()
								{
									public void resultAvailable(Boolean loadable) 
									{
										if(loadable.booleanValue())
											ret.setResult(fac);
										else 
											run();
									}
				
									public void exceptionOccurred(Exception exception)
									{
										System.out.println("Kernel cannot load: "+exta.getId()+" "+model);
										run();
									}
								});
							}
							
							public void exceptionOccurred(Exception exception) 
							{
								System.out.println("error starting factory: "+exception);
							}
						}) ;
					}
				}
				else
				{
					ret.setException(new RuntimeException("No factory found"));
				}
			}
		};
		
		start.run();
				
		return ret;
	}
	
	/**
	 *  Scan files for kernel components.
	 *  @return (suffix -> classname)
	 */
	protected Map<String, Collection<Tuple2<String, Set<String>>>> scanForKernels()
	{
		MultiCollection<String, Tuple2<String, Set<String>>> ret = new MultiCollection<>();
		
//		System.out.println("Scanning");
		
//		List<URL> urls = new ArrayList<URL>();
//		ClassLoader basecl = MultiFactory.class.getClassLoader();
//		for(URL url: SUtil.getClasspathURLs(basecl, true))
//		{
//			// Hack to avoid at least some Java junk.
//			if(!url.toString().contains("jre/lib/ext"))
//			{
//				urls.add(url);
//			}
//		}
//		System.out.println(urls.size());
		
		ILibraryService ls = agent.getFeature(IRequiredServicesFeature.class).getLocalService(ILibraryService.class);
		List<URL> urls2 = ls.getAllURLs().get();

//		System.out.println("urls: "+urls2.size());
//		for(URL u: urls2)
//			System.out.println(u);
		for(Iterator<URL> it=urls2.iterator(); it.hasNext(); )
		{
			String u = it.next().toString();
			if(u.indexOf("jre/lib/ext")!=-1
			//	|| u.indexOf("jadex")==-1
				|| u.indexOf("SYSTEMCPRID")!=-1)
			{
				it.remove();
			}
		}
		
		//System.out.println("scan: "+urls2.size());
		
		FileFilter ff = new FileFilter("$", false, ".class");
		ff.addFilenameFilter(new IFilter<String>()
		{
			public boolean filter(String fn)
			{
				return fn.startsWith("Kernel");
			}
		});
		
//		System.out.println("urls: "+urls);
		Set<ClassInfo> cis = SReflect.scanForClassInfos(urls2.toArray(new URL[urls2.size()]), ff, new IFilter<ClassInfo>()
		{
			public boolean filter(ClassInfo ci) 
			{
				return ci.hasAnnotation("jadex.micro.annotation.Agent");
			}
		});

		for(ClassInfo ci: cis)
		{
			AnnotationInfos ai = ci.getAnnotation("jadex.micro.annotation.Properties");
			if(ai!=null)
			{
				Object[] vals = (Object[])ai.getValue("value");
				if(vals!=null)
				{
					for(Object val: vals)
					{
						AnnotationInfos a = (AnnotationInfos)val;
						String name = (String)a.getValue("name");
						if("kernel.types".equals(name))
						{
							String value = (String)a.getValue("value");
							String[] types = (String[])SJavaParser.evaluateExpression(value, null);
//							System.out.println("foound: "+ci.getClassname()+" "+Arrays.toString(types));
							for(String type: types)
							{
								ret.add(type, new Tuple2<String, Set<String>>(ci.getClassname(), SUtil.arrayToSet(types)));
							}
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get all kernel files, i.e. specs to start a kernel.
	 */
	protected Map<String, Collection<Tuple2<String, Set<String>>>> getKernelFiles()
	{
		if(kernelfiles==null || dirty)
			kernelfiles = scanForKernels();
		dirty = false;
		return kernelfiles;
	}
	
	/**
	 *  Get a running subfactory.
	 */
	protected IFuture<IComponentFactory> getRunningFactory(String model, String[] imports, IResourceIdentifier rid, Iterator<IComponentFactory> it)
	{		
		if(!isLoadable(model))
			return new Future<IComponentFactory>(new RuntimeException());
		
//		System.out.println("getRunningFactory: "+model);

		Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
		Iterator<IComponentFactory> facs = it!=null? it: getFactories().iterator();
		
		if(facs.hasNext())
		{
			IComponentFactory fac = facs.next();
			fac.isLoadable(model, imports, rid).addResultListener(new IResultListener<Boolean>()
			{
				public void resultAvailable(Boolean result)
				{
					if(result.booleanValue())
						ret.setResult(fac);
					else
						getRunningFactory(model, imports, rid, facs).addResultListener(new DelegationResultListener<>(ret));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					getRunningFactory(model, imports, rid, facs).addResultListener(new DelegationResultListener<>(ret));
				}
			});
		}
		else
		{
			ret.setException(new RuntimeException("No factory not found for: "+model));
		}
		
		return ret;
	}	

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid)
	{		
		if(!isLoadable(model))
			return IFuture.FALSE;
		
//		System.out.println("isLoadable: "+model);

		Future<Boolean> ret = new Future<>();
		
		getFactoryForModel(model, imports, rid, null).addResultListener(new IResultListener<IComponentFactory>()
		{
			public void resultAvailable(IComponentFactory fac)
			{
				fac.isLoadable(model, imports, rid).addResultListener(new DelegationResultListener<Boolean>(ret));
//				{
//					public void customResultAvailable(Boolean result)
//					{
//						super.customResultAvailable(result);
//						if(model.indexOf("Block")!=-1)
//							System.out.println("model: "+result);
//					}
//				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(false);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{		
		if(!isLoadable(model))
			return IFuture.FALSE;
		
//		System.out.println("isStartable: "+model);

		Future<Boolean> ret = new Future<>();
		
		getFactoryForModel(model, imports, rid, null).addResultListener(new IResultListener<IComponentFactory>()
		{
			public void resultAvailable(IComponentFactory fac)
			{
				fac.isStartable(model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(false);
			}
		});
		
		return ret;
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name or resource name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, IResourceIdentifier rid)
	{
		if(!isLoadable(model))
			return new Future<String>(new RuntimeException("Model cannot be loaded: "+model));
		
//		System.out.println("getComponentType: "+model);
		
		Future<String> ret = new Future<>();
		
		getFactoryForModel(model, imports, rid, null).addResultListener(new ExceptionDelegationResultListener<IComponentFactory, String>(ret)
		{
			public void customResultAvailable(IComponentFactory fac) throws Exception
			{
				fac.getComponentType(model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
			}
		});
		
		return ret;
	}

	//-------- cached --------
	
	/**
	 * Get the names of component types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return (String[])componenttypes.toArray(new String[componenttypes.size()]);
	}

	//-------- excluded --------
	
	/**
	 *  Get the properties (name/value pairs).
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map<String, Object> getProperties(String type)
	{
//		return Collections.EMPTY_MAP;
		return props;
	}
	
	/**
	 *  Get the component features for a model.
	 *  @param model The component model.
	 *  @return The component features.
	 */
	public IFuture<Collection<IComponentFeatureFactory>> getComponentFeatures(final IModelInfo model)
	{
//		System.out.println("getComponentFeatures");
		
		Future<Collection<IComponentFeatureFactory>> ret = new Future<>();
		
		getFactoryForModel(model.getFilename(), model.getAllImports(), model.getResourceIdentifier(), null).addResultListener(new ExceptionDelegationResultListener<IComponentFactory, Collection<IComponentFeatureFactory>>(ret)
		{
			public void customResultAvailable(IComponentFactory fac) throws Exception
			{
				fac.getComponentFeatures(model).addResultListener(new DelegationResultListener<>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the filename suffixes that can be loaded with any of the found factories.
	 */
	protected Set<String> getSuffixes()
	{
		Map<String, Collection<Tuple2<String, Set<String>>>> types = getKernelFiles();
//		System.out.println("types: "+types.keySet());
		Set<String> ret = new HashSet<String>(types.keySet());
		//ret.add(".class"); // Hack :-( add manually for micro (add type in kernel desc?!)
		return ret;
	}
	
	/**
	 *  Check if a filename is loadable with respect to the suffixes.
	 */
	protected boolean isLoadable(String filename)
	{
		boolean ret = false;
		for(String suffix: getSuffixes())
		{
			if(filename.endsWith(suffix))
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	/**
	 *  Adds a kernel listener.
	 *  @param listener The listener.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addKernelListener(IMultiKernelListener listener)
	{
		listeners.add(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Removes a kernel listener.
	 *  @param listener The listener.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeKernelListener(IMultiKernelListener listener)
	{
		listeners.remove(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Fires a types added event.
	 *  @param types The types added.
	 *  @return Null, when done.
	 */
	public void fireTypesAdded(String[] types)
	{
		IMultiKernelListener[] ls = (IMultiKernelListener[]) listeners.toArray(new IMultiKernelListener[listeners.size()]);
		for(int i = 0; i < ls.length; ++i)
			ls[i].componentTypesAdded(types);
	}
	
	/**
	 *  Fires a types removed event.
	 *  @param types The types removed.
	 *  @return Null, when done.
	 */
	public void fireTypesRemoved(String[] types)
	{
		IMultiKernelListener[] ls = (IMultiKernelListener[]) listeners.toArray(new IMultiKernelListener[listeners.size()]);
		for(int i = 0; i < ls.length; ++i)
			ls[i].componentTypesRemoved(types);
	}
	
	/**
	 *  Get all subfactories except the multi itself.
	 */
	protected Collection<IComponentFactory> getFactories()
	{
		ServiceQuery<IComponentFactory> q = new ServiceQuery<IComponentFactory>(IComponentFactory.class);
		q.setExcludeOwner(true);
		return SUtil.notNull(agent.getFeature(IRequiredServicesFeature.class).searchLocalServices(q));
	}
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
//		FileFilter ff = new FileFilter("$", false, ".class");
//		ff.addFilenameFilter(new IFilter<String>()
//		{
//			public boolean filter(String fn)
//			{
//				int idx = fn.lastIndexOf("/");
//				if(idx!=-1)
//					fn = fn.substring(idx+1);
//				return fn.startsWith("Kernel");
//			}
//		});
//		
//		ff.filter("hall/das/ist/Klasse.class");
//		
//		scanForKernels();
//	}

}
