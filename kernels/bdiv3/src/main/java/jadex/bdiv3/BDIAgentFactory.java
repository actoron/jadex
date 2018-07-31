package jadex.bdiv3;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* $if !android $ */
/* $endif $ */

import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.BDIExecutionComponentFeature;
import jadex.bdiv3.features.impl.BDILifecycleAgentFeature;
import jadex.bdiv3.features.impl.BDIMonitoringComponentFeature;
import jadex.bdiv3.features.impl.BDIProvidedServicesComponentFeature;
import jadex.bdiv3.features.impl.BDIRequiredServicesComponentFeature;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.LazyResource;
import jadex.commons.ResourceInfo;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfos;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.kernelbase.IBootstrapFactory;
import jadex.micro.MicroAgentFactory;
import jadex.micro.annotation.Agent;


/**
 *  Factory for creating micro agents.
 */
public class BDIAgentFactory extends BasicService implements IComponentFactory, IBootstrapFactory
{
	//-------- constants --------
	
	/** The BDI agent model type name (human readable for display). */
	public static final String	FILETYPE_BDIAGENT = "BDIV3 Agent";
	
	/** The BDI capability model type name (human readable for display). */
	public static final String	FILETYPE_BDICAPA = "BDIV3 Capability";
	
	/** The model type identifier for use in the @Agent annotation. */
	public static final String	TYPE = "bdi";
	
	/** The model type identifier for a capability. */
	public static final String	CAPA_TYPE = "capability";
	
	/** The image icon. */
	protected static final LazyResource ICON = new LazyResource(BDIAgentFactory.class, "/jadex/bdiv3/images/bdi_agent.png");

	/** The specific component features for micro agents. */
	public static final Collection<IComponentFeatureFactory> BDI_FEATURES = Collections.unmodifiableCollection(
		Arrays.asList(
			new ComponentFeatureFactory(IProvidedServicesFeature.class, BDIProvidedServicesComponentFeature.class),
			BDIAgentFeature.FACTORY, 
			BDILifecycleAgentFeature.FACTORY,
			new ComponentFeatureFactory(IExecutionFeature.class, BDIExecutionComponentFeature.class),
			new ComponentFeatureFactory(IMonitoringComponentFeature.class, BDIMonitoringComponentFeature.class),
			new ComponentFeatureFactory(IRequiredServicesFeature.class, BDIRequiredServicesComponentFeature.class)
		));
	
	//-------- attributes --------
	
	/** The application model loader. */
	protected BDIModelLoader loader;
	
	/** The platform. */
	protected IInternalAccess provider;
	
//	/** The properties. */
//	protected Map<String, Object> properties;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	/** The standard + micro component features. */
	protected Collection<IComponentFeatureFactory>	features;

	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public BDIAgentFactory(IInternalAccess provider)//, Map properties)
	{
		this(provider, null);
	}
	
	/**
	 *  Create a new agent factory.
	 */
	public BDIAgentFactory(IInternalAccess provider, Map<String, Object> properties)
	{
		super(provider.getId(), IComponentFactory.class, properties);

		this.provider = provider;
//		this.properties = properties;
		this.loader = new BDIModelLoader();
		
		this.libservicelistener = new ILibraryServiceListener()
		{
			public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, IResourceIdentifier rid, boolean removable)
			{
				loader.clearModelCache();
				return IFuture.DONE;
			}
			
			public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, IResourceIdentifier rid)
			{
				loader.clearModelCache();
				return IFuture.DONE;
			}
		};
		
		features	= SComponentFactory.orderComponentFeatures(SReflect.getUnqualifiedClassName(getClass()), Arrays.asList(SComponentFactory.DEFAULT_FEATURES, MicroAgentFactory.MICRO_FEATURES, BDI_FEATURES));
	}
	
	/**
	 *  Create a new agent factory for startup.
	 *  @param platform	The platform.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public BDIAgentFactory(String providerid)
	{
		super(new BasicComponentIdentifier(providerid), IComponentFactory.class, null);
		this.loader = new BDIModelLoader();
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService(IInternalAccess component, IResourceIdentifier rid)
	{
		this.provider = component;
		this.providerid = component.getId();
		setServiceIdentifier(createServiceIdentifier(provider, "Bootstrap Factory", IComponentFactory.class, IComponentFactory.class, rid, null));
		return startService();
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService()
	{
		libservice	= provider.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
		libservice.addLibraryServiceListener(libservicelistener);	// TODO: wait for future?
		return BDIAgentFactory.super.startService();
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public synchronized IFuture<Void>	shutdownService()
	{
		final Future<Void>	ret	= new Future<Void>();
		libservice.removeLibraryServiceListener(libservicelistener)
			.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				BDIAgentFactory.super.shutdownService()
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
			
		return ret;
	}
	
	/**
	 *  Get the component features for a model.
	 *  @param model The component model.
	 *  @return The component features.
	 */
	public IFuture<Collection<IComponentFeatureFactory>> getComponentFeatures(IModelInfo model)
	{
		return new Future<Collection<IComponentFeatureFactory>>(features);
	}
	
	//-------- IAgentFactory interface --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		final Future<IModelInfo> ret = new Future<IModelInfo>();
//		System.out.println("filename: "+model);
		
		if(libservice!=null)
		{
			libservice.getClassLoader(rid)
				.addResultListener(new ExceptionDelegationResultListener<ClassLoader, IModelInfo>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
						IModelInfo mi = loader.loadComponentModel(model, imports, rid, cl, new Object[]{rid, getProviderId().getRoot(), features}).getModelInfo();
						ret.setResult(mi);
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			});		
		}
		else
		{
			try
			{
				ClassLoader cl = getClass().getClassLoader();
				IModelInfo mi = loader.loadComponentModel(model, imports, rid, cl, new Object[]{rid, getProviderId().getRoot(), features}).getModelInfo();
				
				if(model.indexOf("Block")!=-1)
					System.out.println("model2: "+mi);
				
				ret.setResult(mi);
			}
			catch(Exception e)
			{
				ret.setException(e);
			}			
		}

		
		return ret;
	}
		
//	/**
//	 *  Test if a model can be loaded by the factory.
//	 *  @param model The model (e.g. file name).
//	 *  @param The imports (if any).
//	 *  @return True, if model can be loaded.
//	 */
//	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid)
//	{
////		System.out.println("isLoadable: "+model);
////		boolean ret = model.toLowerCase().endsWith("bdi.class");
//		boolean ret = model.endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3);
//		
////		if(ret)
////			System.out.println("isLoadable: "+model+" "+ret);
//		
////		if(model.toLowerCase().endsWith("Agent.class"))
////		{
////			ILibraryService libservice = (ILibraryService)platform.getService(ILibraryService.class);
////			String clname = model.substring(0, model.indexOf(".class"));
////			Class cma = SReflect.findClass0(clname, null, libservice.getClassLoader());
////			ret = cma!=null && cma.isAssignableFrom(IMicroAgent.class);
////			System.out.println(clname+" "+cma+" "+ret);
////		}
//		return new Future<Boolean>(ret? Boolean.TRUE: Boolean.FALSE);
//	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	// todo: reuse code from MicroAgentFactory :-(
	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid)
	{
		Future<Boolean> ret = new Future<>();
		loadClassInfo0(model, imports, rid)
			.addResultListener(new IResultListener<ClassInfo>()
		{
			@Override
			public void resultAvailable(ClassInfo ci)
			{
				if(ci!=null)
				{
					String	modeltype	= getLoadableType(ci);
					ret.setResult(modeltype!=null);
				}
				else
				{
					ret.setResult(false);
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(false);
			}
		});
		return ret;
	}
	
	/**
	 * 	Get the model type from the class info.
	 *  @return null, if no BDI model.
	 */
	public String getLoadableType(ClassInfo ci)
	{
		String	ret	= null;
		AnnotationInfos ai	= ci.getAnnotation(Agent.class.getName());
		if(ai!=null)
		{
			// Check type in agent annotation
			Map<String, Object> vals = ai.getValues();
			String type	= null;
			if(vals==null)
			{
				Method method;
				try
				{
					method = Agent.class.getMethod("type");
					type = (String)method.getDefaultValue();
				}
				catch(Exception e)
				{
				}
			}
			else
			{
				type = (String)vals.get("type");
			}
			
			if(TYPE.equalsIgnoreCase(type))
			{
				ret	= TYPE;
			}
		}
		else
		{
			ai	= ci.getAnnotation(Capability.class.getName());
			if(ai!=null)
			{
				ret	= CAPA_TYPE;
			}
		}
		
		return ret;
	}
	
	/**
	 * 	Load the class info for the model.
	 *  @return null, if it could not be loaded.
	 */
	public IFuture<ClassInfo> loadClassInfo0(String model, String[] imports, IResourceIdentifier rid)
	{
		Future<ClassInfo> ret = new Future<ClassInfo>();
		
		if(model.toLowerCase().endsWith(".class"))
		{
			ILibraryService libservice = getLibraryService();
			if(libservice!=null)
			{
				libservice.getClassLoader(rid)
					.addResultListener(new ExceptionDelegationResultListener<ClassLoader, ClassInfo>(ret)
				{
					public void customResultAvailable(ClassLoader cl)
					{
						try
						{
							ResourceInfo ri = loader.getResourceInfo0(model, imports, cl);
							if(ri==null)
							{
								ret.setResult(null);
							}
							else
							{
								ret.setResult(SClassReader.getClassInfo(ri.getInputStream()));
							}
						}
						catch(Exception e)
						{
							ret.setResult(null);
						}						
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Get the library service
	 */
	protected ILibraryService getLibraryService()
	{
		return internalaccess==null? null: internalaccess.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		Future<Boolean> ret = new Future<>();
		loadClassInfo0(model, imports, rid)
			.addResultListener(new IResultListener<ClassInfo>()
		{
			@Override
			public void resultAvailable(ClassInfo ci)
			{
				if(ci!=null)
				{
					String	modeltype	= getLoadableType(ci);
					ret.setResult(TYPE.equals(modeltype) && !ci.isAbstract());
				}
				else
				{
					ret.setResult(false);
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(false);
			}
		});
		return ret;
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_BDIAGENT};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
		Future<byte[]>	ret	= new Future<byte[]>();
		if(type.equals(FILETYPE_BDIAGENT))
		{
			try
			{
				ret.setResult(ICON.getData());
			}
			catch(IOException e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
//		System.out.println("getIcon: "+type+" "+type.equals(FILETYPE_BDIAGENT));
		return ret;
	}	

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, IResourceIdentifier rid)
	{
		Future<String>	ret	= new Future<>();
		loadClassInfo0(model, imports, rid)
			.addResultListener(new IResultListener<ClassInfo>()
		{
			@Override
			public void resultAvailable(ClassInfo ci)
			{
				if(ci!=null)
				{
					String	modeltype	= getLoadableType(ci);
					ret.setResult(TYPE.equals(modeltype) ? FILETYPE_BDIAGENT
						: CAPA_TYPE.equals(modeltype) ? FILETYPE_BDIAGENT : null);
				}
				else
				{
					ret.setResult(null);
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
//	/**
//	 * Create a component instance.
//	 * @param desc	The component description.
//	 * @param factory The component adapter factory.
//	 * @param model The component model.
//	 * @param config The name of the configuration (or null for default configuration) 
//	 * @param arguments The arguments for the component as name/value pairs.
//	 * @param parent The parent component (if any).
//	 * @param bindings	Optional bindings to override bindings from model.
//	 * @param pinfos	Optional provided service infos to override settings from model.
//	 * @param copy	Global flag for parameter copying.
//	 * @param realtime	Global flag for real time timeouts.
//	 * @param persist	Global flag for persistence support.
//	 * @param resultlistener	Optional listener to be notified when the component finishes.
//	 * @param init	Future to be notified when init of the component is completed.
//	 * @return An instance of a component and the corresponding adapter.
//	 */
//	@Excluded
//	public @Reference IFuture<Tuple2<IComponentInstance, IComponentAdapter>> createComponentInstance(@Reference final IComponentDescription desc, 
//		final IComponentAdapterFactory factory, final IModelInfo model, final String config, final Map<String, Object> arguments, 
//		final IExternalAccess parent, @Reference final RequiredServiceBinding[] bindings, @Reference final ProvidedServiceInfo[] pinfos, final boolean copy, final boolean realtime, final boolean persist,
//		final IPersistInfo persistinfo, 
//		final IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> init, @Reference final LocalServiceRegistry registry)
//	{
//		final Future<Tuple2<IComponentInstance, IComponentAdapter>> res = new Future<Tuple2<IComponentInstance, IComponentAdapter>>();
//		
//		if(libservice!=null)
//		{
//			// todo: is model info ok also in remote case?
//	//		ClassLoader cl = libservice.getClassLoader(model.getResourceIdentifier());
//			libservice.getClassLoader(model.getResourceIdentifier())
//				.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Tuple2<IComponentInstance, IComponentAdapter>>(res)
//			{
//				public void customResultAvailable(ClassLoader cl)
//				{
//					try
//					{
//						BDIModel mm = loader.loadComponentModel(model.getFilename(), null, cl, new Object[]{model.getResourceIdentifier(), getProviderId().getRoot()});
//						BDIAgentInterpreter mai = new BDIAgentInterpreter(desc, factory, mm, getMicroAgentClass(model.getFullName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST, 
//							null, cl), arguments, config, parent, bindings, pinfos, copy, realtime, persist, persistinfo, resultlistener, init, registry);
//						res.setResult(new Tuple2<IComponentInstance, IComponentAdapter>(mai, mai.getComponentAdapter()));
//					}
//					catch(Exception e)
//					{
//						res.setException(e);
//					}
//				}
//			});
//		}
//		
//		// For platform bootstrapping
//		else
//		{
//			try
//			{
//				ClassLoader	cl	= getClass().getClassLoader();
//				BDIModel mm = loader.loadComponentModel(model.getFilename(), null, cl, new Object[]{model.getResourceIdentifier(), getProviderId().getRoot()});
//				BDIAgentInterpreter mai = new BDIAgentInterpreter(desc, factory, mm, getMicroAgentClass(model.getFullName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST, 
//					null, cl), arguments, config, parent, bindings, pinfos, copy, realtime, persist, persistinfo, resultlistener, init, registry);
//				res.setResult(new Tuple2<IComponentInstance, IComponentAdapter>(mai, mai.getComponentAdapter()));
//			}
//			catch(Exception e)
//			{
//				res.setException(e);
//			}
//		}
//
//		return res;
////		return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(new Tuple2<IComponentInstance, IComponentAdapter>(mai, mai.getAgentAdapter()));
//	}
	
	/**
	 *  Get the element type.
	 *  @return The element type (e.g. an agent, application or process).
	 * /
	public String getElementType()
	{
		return IComponentFactory.ELEMENT_TYPE_AGENT;
	}*/
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map<String, Object> getProperties(String type)
	{
		return FILETYPE_BDIAGENT.equals(type)? super.getPropertyMap(): null;
	}
	
	/**
	 *  Start the service.
	 * /
	public synchronized IFuture	startService()
	{
		return new Future(null);
	}*/
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 * /
	public synchronized IFuture	shutdownService()
	{
		return new Future(null);
	}*/
	
//	/**
//	 *  Get the mirco agent class.
//	 */
//	// todo: make use of cache
//	protected Class getMicroAgentClass(String clname, String[] imports, ClassLoader classloader)
//	{
//		Class<?> ret = SReflect.findClass0(clname, imports, classloader);
////		System.out.println("getMAC:"+clname+" "+SUtil.arrayToString(imports)+" "+ret);
//		int idx;
//		while(ret==null && (idx=clname.indexOf('.'))!=-1)
//		{
//			clname	= clname.substring(idx+1);
//			ret = SReflect.findClass0(clname, imports, classloader);
////			System.out.println(clname+" "+cma+" "+ret);
//		}
//		if(ret==null)// || !cma.isAssignableFrom(IMicroAgent.class))
//			throw new RuntimeException("No bdi agent file: "+clname+" "+classloader);
//		return ret;
//	}
	
	/**
	 *  Add excluded methods.
	 */
	public static void addExcludedMethods(Map<String, String[]> props, String[] excludes)
	{
		Object ex = props.get("remote_excluded");
		if(ex!=null)
		{
			List<String> newex = new ArrayList<String>();
			for(Iterator<String> it=SReflect.getIterator(ex); it.hasNext(); )
			{
				newex.add(it.next());
			}
			for(int i=0; i<excludes.length; i++)
			{
				newex.add(excludes[i]);
			}
		}
		else
		{
			props.put("remote_excluded", excludes);
		}
	}
}
