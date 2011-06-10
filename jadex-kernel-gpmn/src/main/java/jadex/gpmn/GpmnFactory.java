package jadex.gpmn;

import jadex.bdi.BDIAgentFactory;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.runtime.impl.JavaStandardPlanExecutor;
import jadex.bdibpmn.BpmnPlanExecutor;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
	
	/** The provider. */
	protected IServiceProvider provider;

	/** The gpmn loader. */
	protected GpmnModelLoader loader;

	/** The gpmn bdiagent converter. */
	protected GpmnBDIConverter converter;
	
	/** The bdi agent factory. */
	protected BDIAgentFactory factory;
	//protected IComponentFactory factory;
	
	/** The properties. */
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	public GpmnFactory(IServiceProvider provider, Map properties)
	{
		super(provider.getId(), IComponentFactory.class, properties);
		
		this.properties	= properties;
		this.provider = provider;
		this.loader = new GpmnModelLoader();
		this.converter = new GpmnBDIConverter();
	}
	
	public IFuture startService()
	{
		final IFuture sfuture = super.startService();
		final Future ret = new Future();
		SServiceProvider.getService(provider, IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IThreadPoolService tps = (IThreadPoolService) result;
				Map bdiprops = new HashMap();
				bdiprops.put("planexecutor_standard", new JavaStandardPlanExecutor(tps));
				bdiprops.put("microplansteps", Boolean.TRUE);
				bdiprops.put("planexecutor_bpmn", new BpmnPlanExecutor());
				factory = new BDIAgentFactory(bdiprops, provider);
				sfuture.addResultListener(new DelegationResultListener(ret));
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
	public IFuture loadModel(String model, String[] imports, ClassLoader classloader)
	{
		// Todo: support imports for GPMN models (-> use abstract model loader). 
		Future ret = new Future();
		try
		{
//			ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
//			ClassLoader	cl = libservice.getClassLoader();
			ResourceInfo rinfo = SUtil.getResourceInfo0(model, classloader);
			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
			br.readLine();
			ret.setResult(GpmnXMLReader.read(model, classloader, null).getModelInfo());
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
	public IFuture isLoadable(String model, String[] imports, ClassLoader classloader)
	{
		System.out.println("loadable " + model.endsWith(".gpmn"));
		System.out.println("startable " + isStartable(model, imports, classloader));
		return new Future(model.endsWith(".gpmn"));
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture isStartable(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.endsWith(".gpmn"));
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
	public IFuture getComponentTypeIcon(String type)
	{
		return new Future(type.equals(FILETYPE_GPMNPROCESS) ? icons.getIcon("gpmn_process") : null);
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture getComponentType(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.toLowerCase().endsWith(".gpmn") ? FILETYPE_GPMNPROCESS: null);
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
	public IFuture createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, 
		IModelInfo modelinfo, String config, Map arguments, IExternalAccess parent, RequiredServiceBinding[] bindings, Future inited)
	{
//		ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
		System.out.println(factory.getClass().toString());
		try
		{
			Object ret = null;
			ResourceInfo rinfo = SUtil.getResourceInfo0(modelinfo.getFilename(), modelinfo.getClassLoader());
			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
			br.readLine();
			
			ret = GpmnXMLReader.read(modelinfo.getFilename(), modelinfo.getClassLoader(), null);
			
			ret = converter.convertGpmnModelToBDIAgents((jadex.gpmn.model.MGpmnModel)ret, modelinfo.getClassLoader());
	
			//factory.createComponentAdapter(desc, model, instance, parent);
			return new Future(this.factory.createComponentInstance(desc, factory, (OAVAgentModel)ret, config, arguments, parent, bindings, inited));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
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
