package jadex.gpmn;

import jadex.bdi.BDIAgentFactory;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.runtime.impl.JavaStandardPlanExecutor;
import jadex.bdibpmn.BpmnPlanExecutor;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.search.PlatformServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.LazyResource;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.gpmn.model.MGpmnModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  Factory for loading gpmn processes. 
 */
public class GpmnFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The supported component types (file extensions).
	 *  Convention used by platform config panel. */
	public static final String[]	FILETYPES	= new String[]{".gpmn"};
	
	/** The gpmn process file type. */
	public static final String	FILETYPE_GPMNPROCESS = "GPMN Process";
	
	/** The image icon. */
	protected static final LazyResource ICON = new LazyResource(GpmnFactory.class, "/jadex/gpmn/images/gpmn_process.png");
	
	//-------- attributes --------
	
	/** The internal access. */
	protected IInternalAccess ia;

	/** The gpmn loader. */
	protected GpmnModelLoader loader;

	/** The gpmn bdiagent converter. */
	protected GpmnBDIConverter converter;
	
	/** The bdi agent factory. */
	protected BDIAgentFactory factory;
	//protected IComponentFactory factory;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The properties. */
	protected Map fproperties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public GpmnFactory(String providerid)
	{
		super(new BasicComponentIdentifier(providerid), IComponentFactory.class, null);
		
		this.loader = new GpmnModelLoader();
		this.converter = new GpmnBDIConverter(getProviderId().getRoot());
	}
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	public GpmnFactory(IInternalAccess access, Map properties)
	{
		super(((IServiceProvider)access.getServiceContainer()).getId(), IComponentFactory.class, properties);
		
		this.fproperties	= properties;
		this.ia = access;
		this.loader = new GpmnModelLoader();
		this.converter = new GpmnBDIConverter(access.getComponentIdentifier().getRoot());
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService()
	{
//		final IFuture<Void> sfuture = super.startService();
		final Future<Void> ret = new Future<Void>();
		
		(IServiceProvider)ia.getServiceContainer().searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				libservice = (ILibraryService)result;
//				libservice.addLibraryServiceListener(libservicelistener);
				
				(IServiceProvider)ia.getServiceContainer().searchService( new ServiceQuery<>( IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IThreadPoolService tps = (IThreadPoolService) result;
						Map bdiprops = new HashMap();
						bdiprops.put("planexecutor_standard", new JavaStandardPlanExecutor(tps));
						bdiprops.put("microplansteps", Boolean.TRUE);
						bdiprops.put("planexecutor_bpmn", new BpmnPlanExecutor());
						factory = new BDIAgentFactory(bdiprops, ia);
						factory.startService();
//						sfuture.addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
						GpmnFactory.super.startService().addResultListener(new DelegationResultListener(ret));
					}
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 * /
	public synchronized IFuture	shutdownService()
	{
		return super.shutdownService();
	}*/
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		// Todo: support imports for GPMN models (-> use abstract model loader). 
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		
		if(libservice!=null)
		{
			libservice.getClassLoader(rid).addResultListener(
				new ExceptionDelegationResultListener<ClassLoader, IModelInfo>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
						ret.setResult(loader.loadGpmnModel(model, imports, cl, rid));
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
				ret.setResult(loader.loadGpmnModel(model, imports, cl, rid));
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		
		
//		try
//		{
////			ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
////			ClassLoader	cl = libservice.getClassLoader();
//			ResourceInfo rinfo = SUtil.getResourceInfo0(model, libservice.getClassLoader(rid));
//			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
//			br.readLine();
//			ret.setResult(GpmnXMLReader.read(model, libservice.getClassLoader(rid), null).getModelInfo());
//		}
//		catch(Exception e)
//		{
//			ret.setException(e);
//		}
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
		return model.endsWith(".gpmn") ? IFuture.TRUE : IFuture.FALSE;
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{
		return model.endsWith(".gpmn") ? IFuture.TRUE : IFuture.FALSE;
	}
	
	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_GPMNPROCESS};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
		Future<byte[]>	ret	= new Future<byte[]>();
		if(type.equals(FILETYPE_GPMNPROCESS))
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
		return ret;
	}	

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<String>(model.toLowerCase().endsWith(".gpmn") ? FILETYPE_GPMNPROCESS: null);
	}
	
	/**
	 * Create a component instance.
	 * @param desc	The component description.
	 * @param factory The component adapter factory.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the component as name/value pairs.
	 * @param parent The parent component (if any).
	 * @param bindings	Optional bindings to override bindings from model.
	 * @param pinfos	Optional provided service infos to override settings from model.
	 * @param copy	Global flag for parameter copying.
	 * @param realtime	Global flag for real time timeouts.
	 * @param persist	Global flag for persistence support.
	 * @param resultlistener	Optional listener to be notified when the component finishes.
	 * @param init	Future to be notified when init of the component is completed.
	 * @return An instance of a component and the corresponding adapter.
	 */
	@Excluded
	public @Reference IFuture<Tuple2<IComponentInstance, IComponentAdapter>> createComponentInstance(@Reference final IComponentDescription desc, 
		final IComponentAdapterFactory factory, final IModelInfo model, final String config, final Map<String, Object> arguments, 
		final IExternalAccess parent, @Reference final RequiredServiceBinding[] bindings, @Reference final ProvidedServiceInfo[] pinfos, final boolean copy, final boolean realtime, boolean persist,
		IPersistInfo persistinfo, 
		final IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> init, @Reference final PlatformServiceRegistry registry)
	{
		final Future<Tuple2<IComponentInstance, IComponentAdapter>> ret = new Future<Tuple2<IComponentInstance, IComponentAdapter>>();

//		ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
//		System.out.println(factory.getClass().toString());
		
		if(libservice!=null)
		{
			libservice.getClassLoader(model.getResourceIdentifier()).addResultListener(
				new ExceptionDelegationResultListener<ClassLoader, Tuple2<IComponentInstance, IComponentAdapter>>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
						MGpmnModel amodel = (MGpmnModel)loader.loadModel(model.getFilename(), null, 
							cl, model.getResourceIdentifier());
						OAVAgentModel agmodel = converter.convertGpmnModelToBDIAgents(amodel, amodel.getClassLoader());
						ret.setResult(GpmnFactory.this.factory.createComponentInstance(desc, factory, agmodel, config, arguments, parent, bindings, pinfos, copy, realtime, resultlistener, init, registry));
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
				MGpmnModel amodel = (MGpmnModel)loader.loadModel(model.getFilename(), null, 
					cl, model.getResourceIdentifier());
				OAVAgentModel agmodel = converter.convertGpmnModelToBDIAgents(amodel, amodel.getClassLoader());
				ret.setResult(GpmnFactory.this.factory.createComponentInstance(desc, factory, agmodel, config, arguments, parent, bindings, pinfos, copy, realtime, resultlistener, init, registry));
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		
		return ret;
		
//		try
//		{
//			MGpmnModel amodel = (MGpmnModel)loader.loadModel(modelinfo.getFilename(), null, 
//				libservice.getClassLoader(modelinfo.getResourceIdentifier()), modelinfo.getResourceIdentifier());
//
//			Object ret = null;
////			ResourceInfo rinfo = SUtil.getResourceInfo0(modelinfo.getFilename(), modelinfo.getClassLoader());
////			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
////			br.readLine();
////			ret = GpmnXMLReader.read(modelinfo.getFilename(), modelinfo.getClassLoader(), null);
//			
////			ret = converter.convertGpmnModelToBDIAgents((jadex.gpmn.model.MGpmnModel)ret, modelinfo.getClassLoader());
//			ret = converter.convertGpmnModelToBDIAgents(amodel, amodel.getClassLoader());
//	
//			//factory.createComponentAdapter(desc, model, instance, parent);
//			return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(this.factory.createComponentInstance(desc, factory, (OAVAgentModel)ret, config, arguments, parent, bindings, copy, inited));
//		}
//		catch(Exception e)
//		{
//			return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(e);
//		}
	}
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map	getProperties(String type)
	{
		return FILETYPE_GPMNPROCESS.equals(type)
		? fproperties : null;
	}
}
