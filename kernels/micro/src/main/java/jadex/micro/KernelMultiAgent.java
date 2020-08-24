package jadex.micro;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IMultiKernelNotifierService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.Boolean3;
import jadex.commons.FileFilter;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
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
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentServiceQuery;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Multi kernel.
 */
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, scope=ServiceScope.PLATFORM),
	@ProvidedService(type=IMultiKernelNotifierService.class, scope=ServiceScope.PLATFORM), // implementation=@Implementation(expression="$component.getFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(jadex.bridge.service.types.factory.IComponentFactory.class)"))
})
@ComponentTypes({
	@ComponentType(name="KernelMicro", filename="jadex/micro/KernelMicroAgent.class")
})
@Configurations({
	@Configuration(name="default", components={
		@Component(name="kernel_micro", type="KernelMicro")
	})
})
@Agent(name="kernel_multi",
	autostart=Boolean3.TRUE)
@Service
@Properties(@NameValue(name="system", value="true"))
public class KernelMultiAgent implements IComponentFactory, IMultiKernelNotifierService
{
	/** Kernel model property for extensions */
	protected static final String KERNEL_EXTENSIONS = "kernel.types";
	
	/** Filter for scanning for kernel agent class files. */
	protected static FileFilter ffilter = new FileFilter("$", false, ".class")
		.addFilenameFilter(new IFilter<String>()
	{
		public boolean filter(String fn)
		{
			return fn.startsWith("Kernel");
		}
	});
	
	/** Filter for scanning for kernel agent class infos. */
	protected static IFilter<ClassInfo>	cfilter	= new IFilter<ClassInfo>()
	{
		public boolean filter(ClassInfo ci) 
		{
			return ci.hasAnnotation("jadex.micro.annotation.Agent");
		}
	};
	
	/** The internal access. */
	@Agent
	protected IInternalAccess agent;
	
	@OnService
	protected ILibraryService libservice; 
	
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
	protected Map<String, Object> kernels = new HashMap<>();
	
	/** The started flag (because init is invoked twice, service impl for 2 services. */
	protected boolean inited;
	
	/** The dirty flag (when classpath changes). */
	protected boolean dirty;

	/** The known factories. */
	protected Set<String> known_kernels = SUtil.createHashSet(new String[]{
		"jadex/micro/KernelMicroAgent.class", 
		"jadex/bdiv3/KernelBDIV3Agent.class", 
		"jadex/bdiv3x/KernelBDIXAgent.class", 
		"jadex/bpmn/KernelBpmnAgent.class", 
		"jadex/application/KernelApplicationAgent.class", 
		"jadex/component/KernelComponentAgent.class",
		"jadex/microservice/KernelMicroserviceAgent.class"
	});
	protected MultiCollection<String, Tuple2<String, Set<String>>> known_kernels_cache;
	
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
	//@AgentCreated
	@OnInit
	public IFuture<Void> startService()
	{
		if(inited)
			return IFuture.DONE;
		inited = true;
		
		// add data for implicitly started micro factory
		componenttypes.add(".class");
		kernels.put("jadex.micro.KernelMicroAgent", null);
		
		// Rescan on any changes in the library service
		
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
				if(!((IService)fac).getServiceId().equals(sid))
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
		Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
		getRunningFactory(model, imports, rid, null).addResultListener(new IResultListener<IComponentFactory>()
		{
			public void resultAvailable(IComponentFactory fac)
			{
				ret.setResult(fac);
			}

			public void exceptionOccurred(Exception exception)
			{
//				getnewfac.call(new Object[]{model, imports, rid}).addResultListener(new DelegationResultListener<>(ret));
				getNewFactory(model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
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
		
		Map<String, Collection<Tuple2<String, Set<String>>>> kernelfiles = getKnownKernels();
		Set<Tuple2<String, Set<String>>> found = new HashSet<>();
		for(Map.Entry<String, Collection<Tuple2<String, Set<String>>>> entry: kernelfiles.entrySet())
		{
			if(model.endsWith(entry.getKey()))
			{
				found.addAll(entry.getValue());
			}
		}
		final Iterator<Tuple2<String, Set<String>>> it = found.iterator();
		
		check(it, model, imports, rid).addResultListener(new DelegationResultListener<IComponentFactory>(ret)
		{
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("getKernelFiles: "+model);
				Map<String, Collection<Tuple2<String, Set<String>>>> kernelfiles = getKernelFiles();
				Set<Tuple2<String, Set<String>>> found = new HashSet<>();
				for(Map.Entry<String, Collection<Tuple2<String, Set<String>>>> entry: kernelfiles.entrySet())
				{
					if(model.endsWith(entry.getKey()))
					{
						found.addAll(entry.getValue());
					}
				}
				final Iterator<Tuple2<String, Set<String>>> it = found.iterator();
				
				check(it, model, imports, rid).addResultListener(new DelegationResultListener<IComponentFactory>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Scan files for kernel components.
	 *  @return (suffix -> classname)
	 */
	protected Map<String, Collection<Tuple2<String, Set<String>>>> scanForKernels()
	{
		MultiCollection<String, Tuple2<String, Set<String>>> ret = new MultiCollection<>();
		
//		System.out.println("MultiFactory scanning...");
		
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

//		System.out.println("urls2: "+urls2.size());
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
		
//		System.out.println("urls: "+urls);
		Set<ClassInfo> cis = SReflect.scanForClassInfos(urls2.toArray(new URL[urls2.size()]), ffilter, cfilter);

		for(ClassInfo ci: cis)
		{
			String[] types = getKernelTypes(ci);
			
			if(types!=null)
			{
				for(String type: types)
				{
					ret.add(type, new Tuple2<String, Set<String>>(ci.getClassName(), SUtil.arrayToSet(types)));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Check a factory
	 *  @param it factory iterator.
	 */
	protected IFuture<IComponentFactory> check(Iterator<Tuple2<String, Set<String>>> it, String model, String[] imports, IResourceIdentifier rid)
	{
		Future<IComponentFactory> ret = new Future<>();
		
		if(it.hasNext())
		{
			Tuple2<String, Set<String>> f = it.next();
			
			Object k = kernels.get(f.getFirstEntity());
			
			if(kernels.containsKey(f.getFirstEntity()) && k==null)
			{				
				check(it, model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
			}
			else if(k instanceof IFuture)
			{
				((IFuture<IComponentFactory>)k).addResultListener(new IResultListener<IComponentFactory>() 
				{
					public void resultAvailable(IComponentFactory fac) 
					{
						fac.isLoadable(model, imports, rid).addResultListener(new IResultListener<Boolean>()
						{
							public void resultAvailable(Boolean loadable) 
							{
								if(loadable.booleanValue())
									ret.setResult(fac);
								else 
									check(it, model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
							}

							public void exceptionOccurred(Exception exception)
							{
//								System.out.println("Kernel cannot load: "+exta.getId()+" "+model);
								check(it, model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
							}
						});
					}
					
					public void exceptionOccurred(Exception exception) 
					{
						check(it, model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
					}
				});
			}
			else
			{	
				final Future<IComponentFactory> fut = new Future<>();
				kernels.put(f.getFirstEntity(), fut);
				
				CreationInfo ci = new CreationInfo();
				ci.setFilename(f.getFirstEntity()+".class");
				
//				System.out.println("create compo start: "+f.getFirstEntity());
				agent.createComponent(ci).addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess exta)
					{						
						exta.waitForTermination().addResultListener(new IResultListener<Map<String, Object>>()
						{
							public void resultAvailable(Map<String, Object> result)
							{
//								System.out.println("Killed kernel: " + f);
								kernels.remove(f.getFirstEntity());
							}
							
							public void exceptionOccurred(Exception exception)
							{
//								System.out.println("Killed kernel: " + f+", "+exception);
								kernels.remove(f.getFirstEntity());
							}
						});
						
						//System.out.println("Started factory: "+exta);
						kernels.put(f.getFirstEntity(), exta.getId());
						
						ServiceQuery<IComponentFactory> q = new ServiceQuery<IComponentFactory>(IComponentFactory.class);
						q.setProvider(exta.getId());
						final IComponentFactory fac = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(q);
						
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
//										System.out.println("adding icon: "+types[fi]);
										iconcache.put(types[fi], result);
									}
									
									public void exceptionOccurred(Exception exception)
									{
									}
								});
							}
						}
						
						fut.setResult(fac);
						
						fac.isLoadable(model, imports, rid).addResultListener(new IResultListener<Boolean>()
						{
							public void resultAvailable(Boolean loadable) 
							{
								if(loadable.booleanValue())
									ret.setResult(fac);
								else 
									check(it, model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
							}

							public void exceptionOccurred(Exception exception)
							{
//								System.out.println("Kernel cannot load: "+exta.getId()+" "+model);
								check(it, model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
							}
						});
					}
					
					public void exceptionOccurred(Exception exception) 
					{
						fut.setException(exception);
						System.out.println("error starting factory: "+exception);
						check(it, model, imports, rid).addResultListener(new DelegationResultListener<>(ret));
					}
				});
			}
		}
		else
		{
			ret.setException(new RuntimeException("No factory found"));
		}
		
		return ret;
	}
	
	/**
	 *  Get known kernels.
	 */
	protected Map<String, Collection<Tuple2<String, Set<String>>>> getKnownKernels()
	{
		if(known_kernels_cache==null)
		{
			known_kernels_cache = new MultiCollection<>();
		
			for(String kk: known_kernels)
			{
				// todo: use library loader (needs rid besides classname)
				try
				{
					ClassLoader cl = agent.getClassLoader();
					InputStream is = SUtil.getResource(kk, cl);
					ClassInfo ci = SClassReader.getClassInfo(is);
				
					String[] types = getKernelTypes(ci);
				
					if(types!=null)
					{
						for(String type: types)
						{
							known_kernels_cache.add(type, new Tuple2<String, Set<String>>(ci.getClassName(), SUtil.arrayToSet(types)));
						}
					}
				}
				catch(Exception e)
				{
					System.out.println("Error reading: "+kk);
				}
			}
		}
		
		return known_kernels_cache;
	}
	
	/**
	 *  Add infos about a kernel to the map.
	 */
	protected String[] getKernelTypes(ClassInfo ci)
	{
		AnnotationInfo ai = ci.getAnnotation("jadex.micro.annotation.Properties");
		if(ai!=null)
		{
			Object[] vals = (Object[])ai.getValue("value");
			if(vals!=null)
			{
				for(Object val: vals)
				{
					AnnotationInfo a = (AnnotationInfo)val;
					String name = (String)a.getValue("name");
					if("kernel.types".equals(name))
					{
						String value = (String)a.getValue("value");
						String[] types = (String[])SJavaParser.evaluateExpression(value, null);
//						System.out.println("foound: "+ci.getClassname()+" "+Arrays.toString(types));
						return types;
					}
				}
			}
		}
		return null;
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
//				System.out.println("facformodel: "+model);
				fac.isLoadable(model, imports, rid).addResultListener(new DelegationResultListener<Boolean>(ret)
				{
//					public void customResultAvailable(Boolean result)
//					{
//						super.customResultAvailable(result);
////						if(model.indexOf("Block")!=-1)
//							System.out.println("model: "+model+" "+result);
//					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("ex: "+exception);
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
		// todo: add additional suffixes
		// this hack is important. otherwise the multi factory cannot start its own micro factory subcomponent
		if(agent.getFeature(ISubcomponentsFeature.class).getChildcount()==0)
			return Collections.EMPTY_SET;
		
//		Map<String, Collection<Tuple2<String, Set<String>>>> types = getKernelFiles();
		Map<String, Collection<Tuple2<String, Set<String>>>> types = getKnownKernels();
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
}
