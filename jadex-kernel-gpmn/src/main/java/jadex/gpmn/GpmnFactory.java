package jadex.gpmn;

import jadex.bdi.BDIAgentFactory;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.runtime.impl.JavaStandardPlanExecutor;
import jadex.bdibpmn.BpmnPlanExecutor;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.gpmn.model.MGpmnModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for loading gpmn processes. 
 */
public class GpmnFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The gpmn process file type. */
	public static final String	FILETYPE_GPMNPROCESS = "GPMN Process";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"gpmn_process",	SGUI.makeIcon(GpmnFactory.class, "/jadex/gpmn/images/gpmn_process.png"),
	});
	
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
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	public GpmnFactory(IInternalAccess access, Map properties)
	{
		super(access.getServiceContainer().getId(), IComponentFactory.class, properties);
		
		this.properties	= properties;
		this.ia = access;
		this.loader = new GpmnModelLoader();
		this.converter = new GpmnBDIConverter();
	}
	
	public IFuture<Void> startService()
	{
//		final IFuture<Void> sfuture = super.startService();
		final Future<Void> ret = new Future<Void>();
		
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				libservice = (ILibraryService)result;
//				libservice.addLibraryServiceListener(libservicelistener);
				
				SServiceProvider.getService(ia.getServiceContainer(), IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DefaultResultListener()
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
	public IFuture<IModelInfo> loadModel(String model, String[] imports, IResourceIdentifier rid)
	{
		// Todo: support imports for GPMN models (-> use abstract model loader). 
		Future<IModelInfo> ret = new Future<IModelInfo>();
		try
		{
//			ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
//			ClassLoader	cl = libservice.getClassLoader();
			ResourceInfo rinfo = SUtil.getResourceInfo0(model, libservice.getClassLoader(rid));
			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
			br.readLine();
			ret.setResult(GpmnXMLReader.read(model, libservice.getClassLoader(rid), null).getModelInfo());
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
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
		return new Future<Boolean>(model.endsWith(".gpmn")? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<Boolean>(model.endsWith(".gpmn")? Boolean.TRUE: Boolean.FALSE);
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
	public IFuture<Icon> getComponentTypeIcon(String type)
	{
		return new Future<Icon>(type.equals(FILETYPE_GPMNPROCESS) ? icons.getIcon("gpmn_process") : null);
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
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public IFuture<Tuple2<IComponentInstance, IComponentAdapter>> createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, 
		IModelInfo modelinfo, String config, Map arguments, IExternalAccess parent, RequiredServiceBinding[] bindings, boolean copy, Future<Tuple2<IComponentInstance, IComponentAdapter>> inited)
	{
//		ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
		System.out.println(factory.getClass().toString());
		
		try
		{
			MGpmnModel amodel = (MGpmnModel)loader.loadModel(modelinfo.getFilename(), null, libservice.getClassLoader(modelinfo.getResourceIdentifier()));

			Object ret = null;
//			ResourceInfo rinfo = SUtil.getResourceInfo0(modelinfo.getFilename(), modelinfo.getClassLoader());
//			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
//			br.readLine();
//			ret = GpmnXMLReader.read(modelinfo.getFilename(), modelinfo.getClassLoader(), null);
			
//			ret = converter.convertGpmnModelToBDIAgents((jadex.gpmn.model.MGpmnModel)ret, modelinfo.getClassLoader());
			ret = converter.convertGpmnModelToBDIAgents(amodel, amodel.getClassLoader());
	
			//factory.createComponentAdapter(desc, model, instance, parent);
			return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(this.factory.createComponentInstance(desc, factory, (OAVAgentModel)ret, config, arguments, parent, bindings, copy, inited));
		}
		catch(Exception e)
		{
			return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(e);
		}
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
		? properties : null;
	}
}
