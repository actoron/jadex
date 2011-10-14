package jadex.bpmn;

import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
/* $if !android $ */
import jadex.commons.gui.SGUI;
/* $endif $ */

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/* $if !android $ */
import javax.swing.Icon;
import javax.swing.UIDefaults;
/* $endif $ */

/**
 *  Foctory for loading bpmn processes.
 */
public class BpmnFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The micro agent file type. */
	public static final String	FILETYPE_BPMNPROCESS = "BPMN Process";
	
	/**
	 * The image icons.
	 */
	/* $if !android $ */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"bpmn_process",	SGUI.makeIcon(BpmnFactory.class, "/jadex/bpmn/images/bpmn_process.png"),
	});
	/* $endif $ */
	
	//-------- attributes --------
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** Running process instances */
	protected Map processes;
	
	/** The model loader */
	protected BpmnModelLoader loader;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	/** The properties. */
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BpmnProcessService.
	 */
	public BpmnFactory(IServiceProvider provider, Map properties)
	{
		super(provider.getId(), IComponentFactory.class, null);

		this.provider = provider;
		this.processes = new HashMap();
		this.loader = new BpmnModelLoader();
		this.properties	= properties;
		
		this.libservicelistener = new ILibraryServiceListener()
		{
			public IFuture urlRemoved(URL url)
			{
				loader.clearModelCache();
				return IFuture.DONE;
			}
			
			public IFuture urlAdded(URL url)
			{
				loader.clearModelCache();
				return IFuture.DONE;
			}
		};
		SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ILibraryService libService = (ILibraryService) result;
				libService.addLibraryServiceListener(libservicelistener);
			}
		});
	}
	
	/**
	 *  Create a new factory for startup.
	 *  @param platform	The platform.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public BpmnFactory(String providerid)
	{
		super(providerid, IComponentFactory.class, null);
		this.loader = new BpmnModelLoader();
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 * /
	public synchronized IFuture	startService()
	{
		return super.startService();
	}*/
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public synchronized IFuture<Void>	shutdownService()
	{
		SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				libservice = (ILibraryService)result;
				libservice.removeLibraryServiceListener(libservicelistener);
			}
		});
		return super.shutdownService();
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture loadModel(String model, String[] imports, IResourceIdentifier rid)
	{
		Future ret = new Future();
//		System.out.println("filename: "+filename);
		try
		{
			ClassLoader cl = libservice.getClassLoader(rid);
			MBpmnModel amodel = loader.loadBpmnModel(model, imports, 
				libservice.getClassLoader(rid), rid);
			amodel.setClassLoader(cl);
			ret.setResult(amodel.getModelInfo());
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
		return new Future<Boolean>(model.endsWith(".bpmn")? Boolean.TRUE: Boolean.FALSE);
	}

	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<Boolean>(model.endsWith(".bpmn")? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_BPMNPROCESS};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	/* $if !android $ */
	public IFuture<Icon> getComponentTypeIcon(String type)
	{
		return new Future<Icon>(type.equals(FILETYPE_BPMNPROCESS)? icons.getIcon("bpmn_process") : null);
	}
	/* $endif $ */

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<String>(model.toLowerCase().endsWith(".bpmn") ? FILETYPE_BPMNPROCESS: null);
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
		try
		{
			MBpmnModel model = loader.loadBpmnModel(modelinfo.getFilename(), null, libservice==null? getClass().getClassLoader(): libservice.getClassLoader(modelinfo.getResourceIdentifier()), modelinfo.getResourceIdentifier());
			BpmnInterpreter interpreter = new BpmnInterpreter(desc, factory, model, arguments, config, parent, null, null, null, bindings, copy, inited);
			return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(new Tuple2<IComponentInstance, IComponentAdapter>(interpreter, interpreter.getComponentAdapter()));
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
		return FILETYPE_BPMNPROCESS.equals(type)
		? properties : null;
	}
}
