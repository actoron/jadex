package jadex.micro;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.commons.ICacheableModel;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
/* $if !android $ */
import jadex.commons.gui.SGUI;
/* $endif $ */
import jadex.kernelbase.CacheableKernelModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* $if !android $ */
import javax.swing.Icon;
import javax.swing.UIDefaults;
/* $endif $ */

/**
 *  Factory for creating micro agents.
 */
public class MicroAgentFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The micro agent file type. */
	public static final String	FILETYPE_MICROAGENT	= "Micro Agent";
	
	/** The image icons. */
	/* $if !android $ */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"micro_agent",	SGUI.makeIcon(MicroAgentFactory.class, "/jadex/micro/images/micro_agent.png"),
	});
	/* $endif $ */

	//-------- attributes --------
	
	/** The application model loader. */
	protected MicroModelLoader loader;
	
	/** The platform. */
	protected IServiceProvider provider;
	
	/** The properties. */
	protected Map properties;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public MicroAgentFactory(IServiceProvider provider, Map properties)
	{
		super(provider.getId(), IComponentFactory.class, null);

		this.provider = provider;
		this.properties = properties;
		this.loader = new MicroModelLoader();
		
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
	}
	
	/**
	 *  Create a new agent factory for startup.
	 *  @param platform	The platform.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public MicroAgentFactory(String providerid)
	{
		super(providerid, IComponentFactory.class, null);
		this.loader = new MicroModelLoader();
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture startService()
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService libservice = (ILibraryService)result;
				libservice.addLibraryServiceListener(libservicelistener);
				MicroAgentFactory.super.startService().addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public synchronized IFuture	shutdownService()
	{
		SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ILibraryService libService = (ILibraryService) result;
				libService.removeLibraryServiceListener(libservicelistener);
			}
		});
		return super.shutdownService();
	}
	
	//-------- IAgentFactory interface --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(String model, String[] imports, ClassLoader classloader)
	{
		Future<IModelInfo> ret = new Future<IModelInfo>();
//		System.out.println("filename: "+filename);
		try
		{
			ret.setResult(loader.loadComponentModel(model, imports, classloader).getModelInfo());
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
//	/**
//	 *  Load a  model.
//	 *  @param model The model (e.g. file name).
//	 *  @param The imports (if any).
//	 *  @return The loaded model.
//	 */
//	public IModelInfo loadModel(InputStream in, String[] imports, ClassLoader classloader)
//	{
//		IModelInfo ret = null;
//		
//		ByteClassLoader cl = new ByteClassLoader(classloader);
//		BufferedInputStream bin = new BufferedInputStream(in);
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//		try
//		{
//			int d;
//			while((d = bin.read())!=-1)
//				bos.write(d);
//		
//			Class cma = cl.loadClass(null, bos.toByteArray(), true);
//			String model = cma.getName().replace('.', '/');
//			
//			ret = loader.read(model, cma, cl);
//		}
//		catch(Exception e)
//		{
//		}
//		
//		try
//		{
//			bin.close();
//		}
//		catch(IOException e)
//		{
//		}
//		try
//		{
//			bos.close();
//		}
//		catch(IOException e)
//		{
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(String model, String[] imports, ClassLoader classloader)
	{
		boolean ret = model.toLowerCase().endsWith("agent.class");
//		if(model.toLowerCase().endsWith("Agent.class"))
//		{
//			ILibraryService libservice = (ILibraryService)platform.getService(ILibraryService.class);
//			String clname = model.substring(0, model.indexOf(".class"));
//			Class cma = SReflect.findClass0(clname, null, libservice.getClassLoader());
//			ret = cma!=null && cma.isAssignableFrom(IMicroAgent.class);
//			System.out.println(clname+" "+cma+" "+ret);
//		}
		return new Future<Boolean>(ret? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, ClassLoader classloader)
	{
		return isLoadable(model, imports, classloader);
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_MICROAGENT};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	/* $if !android $ */
	public IFuture<Icon> getComponentTypeIcon(String type)
	{
		return new Future<Icon>(type.equals(FILETYPE_MICROAGENT) ? icons.getIcon("micro_agent") : null);
	}
	/* $endif $ */

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, ClassLoader classloader)
	{
		return new Future<String>(model.toLowerCase().endsWith("agent.class") ? FILETYPE_MICROAGENT: null);
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
	public IFuture<Tuple2<IComponentInstance, IComponentAdapter>> createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, IModelInfo model, 
		String config, Map arguments, IExternalAccess parent, RequiredServiceBinding[] binding, boolean copy, Future<Tuple2<IComponentInstance, IComponentAdapter>> ret)
	{
		try
		{
			// todo: is model info ok also in remote case?
			MicroModel mm = loader.loadComponentModel(model.getFilename(), null, model.getClassLoader());
	
			MicroAgentInterpreter mai = new MicroAgentInterpreter(desc, factory, mm, getMicroAgentClass(model.getFullName()+"Agent", 
				null, model.getClassLoader()), arguments, config, parent, binding, copy, ret);
			return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(new Tuple2<IComponentInstance, IComponentAdapter>(mai, mai.getAgentAdapter()));
		}
		catch(Exception e)
		{
			return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(e);
		}
	}
	
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
	public Map	getProperties(String type)
	{
		return FILETYPE_MICROAGENT.equals(type)
		? properties: null;
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
	
	/**
	 *  Get the mirco agent class.
	 */
	// todo: make use of cache
	protected Class getMicroAgentClass(String clname, String[] imports, ClassLoader classloader)
	{
		Class ret = SReflect.findClass0(clname, imports, classloader);
//		System.out.println(clname+" "+cma+" "+ret);
		int idx;
		while(ret==null && (idx=clname.indexOf('.'))!=-1)
		{
			clname	= clname.substring(idx+1);
			ret = SReflect.findClass0(clname, imports, classloader);
//			System.out.println(clname+" "+cma+" "+ret);
		}
		if(ret==null)// || !cma.isAssignableFrom(IMicroAgent.class))
			throw new RuntimeException("No micro agent file: "+clname);
		return ret;
	}
	
	/**
	 *  Add excluded methods.
	 */
	public static void addExcludedMethods(Map props, String[] excludes)
	{
		Object ex = props.get("remote_excluded");
		if(ex!=null)
		{
			List newex = new ArrayList();
			for(Iterator it=SReflect.getIterator(ex); it.hasNext(); )
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
