package jadex.micro;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.kernelbase.IBootstrapFactory;
import jadex.commons.LazyResource;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  Factory for creating micro agents.
 */
public class MicroAgentFactory extends BasicService implements IComponentFactory, IBootstrapFactory
{
	//-------- constants --------
	
	/** The supported component types (file extensions).
	 *  Convention used by platform config panel. */
	public static final String[]	FILETYPES	= new String[]{"Agent.class"};
	
	/** The micro agent file type. */
	public static final String	FILETYPE_MICROAGENT	= "Micro Agent";
	
	/** The image icon. */
	protected static final LazyResource ICON = new LazyResource(MicroAgentFactory.class, "/jadex/micro/images/micro_agent.png");

	//-------- attributes --------
	
	/** The application model loader. */
	protected MicroModelLoader loader;
	
	/** The platform. */
	protected IServiceProvider provider;
	
	/** The properties. */
	protected Map<String, Object> fproperties;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public MicroAgentFactory(IServiceProvider provider, Map<String, Object> properties)
	{
		super(provider.getId(), IComponentFactory.class, null);

		this.provider = provider;
		this.fproperties = properties;
		this.loader = new MicroModelLoader();
		
		this.libservicelistener = new ILibraryServiceListener()
		{
			public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, IResourceIdentifier rid)
			{
				loader.clearModelCache();
				return IFuture.DONE;
			}
			
			public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, IResourceIdentifier rid, boolean rem)
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
		super(new ComponentIdentifier(providerid), IComponentFactory.class, null);
		this.loader = new MicroModelLoader();
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService(IInternalAccess component, IResourceIdentifier rid)
	{
		this.provider = component.getServiceContainer();
		this.providerid = provider.getId();
		createServiceIdentifier("BootstrapFactory", IComponentFactory.class, rid, IComponentFactory.class);
		return startService();
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService()
	{
		final Future<Void> ret = new Future<Void>();
		SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
		{
			public void customResultAvailable(ILibraryService result)
			{
				libservice = result;
				libservice.addLibraryServiceListener(libservicelistener);
				MicroAgentFactory.super.startService()
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
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
				MicroAgentFactory.super.shutdownService()
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
			
		return ret;
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
//		System.out.println("filename: "+filename);
		
//		if(model.indexOf("HelloWorld")!=-1)
//			System.out.println("hw");
		
		if(libservice!=null)
		{
			libservice.getClassLoader(rid)
				.addResultListener(new ExceptionDelegationResultListener<ClassLoader, IModelInfo>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
						IModelInfo mi = loader.loadComponentModel(model, imports, cl, new Object[]{rid, getProviderId().getRoot()}).getModelInfo();
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
				IModelInfo mi = loader.loadComponentModel(model, imports, cl, new Object[]{rid, getProviderId().getRoot()}).getModelInfo();
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
	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid)
	{
//		System.out.println("isLoadable (micro): "+model);
		
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
	public IFuture<Boolean> isStartable(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		isLoadable(model, imports, rid).addResultListener(new DelegationResultListener<Boolean>(ret)
		{
			public void customResultAvailable(Boolean result)
			{
				if(!result.booleanValue())
				{
					ret.setResult(Boolean.FALSE);
				}
				else
				{
					loadModel(model, imports, rid).addResultListener(new ExceptionDelegationResultListener<IModelInfo, Boolean>(ret)
					{
						public void customResultAvailable(final IModelInfo mi) 
						{
							if(libservice!=null)
							{
								libservice.getClassLoader(rid)
									.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Boolean>(ret)
								{
									public void customResultAvailable(ClassLoader cl)
									{
										try
										{
											Class<?> clazz = getMicroAgentClass(mi.getFullName()+"Agent", null, cl);
											ret.setResult(!Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers()));
										}
										catch(Exception e)
										{
											ret.setResult(Boolean.FALSE);
//											ret.setException(e);
										}
									}
								});		
							}
							else
							{
								try
								{
									ClassLoader cl = getClass().getClassLoader();
									Class<?> clazz = getMicroAgentClass(mi.getFullName()+"Agent", null, cl);
									ret.setResult(!Modifier.isAbstract(clazz.getModifiers()));
								}
								catch(Exception e)
								{
									ret.setResult(Boolean.FALSE);
//									ret.setException(e);
								}
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							exception.printStackTrace();
							Logger.getLogger(MicroAgentFactory.class.toString()).warning(exception.toString());
							ret.setResult(Boolean.FALSE);
//							super.exceptionOccurred(exception);
						}
					});
				}
			}
		});
		
		return ret;
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
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
		Future<byte[]>	ret	= new Future<byte[]>();
		if(type.equals(FILETYPE_MICROAGENT))
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
	public IFuture<Tuple2<IComponentInstance, IComponentAdapter>> createComponentInstance(final IComponentDescription desc, final IComponentAdapterFactory factory, final IModelInfo model, 
		final String config, final Map<String, Object> arguments, final IExternalAccess parent, final RequiredServiceBinding[] binding, final boolean copy, final boolean realtime, final boolean persist,
		final IPersistInfo persistinfo,
		final IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> inited)
	{
		final Future<Tuple2<IComponentInstance, IComponentAdapter>> res = new Future<Tuple2<IComponentInstance, IComponentAdapter>>();
		
		if(libservice!=null)
		{
			// todo: is model info ok also in remote case?
	//		ClassLoader cl = libservice.getClassLoader(model.getResourceIdentifier());
			libservice.getClassLoader(model.getResourceIdentifier())
				.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Tuple2<IComponentInstance, IComponentAdapter>>(res)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
						MicroModel mm = loader.loadComponentModel(model.getFilename(), null, cl, new Object[]{model.getResourceIdentifier(), getProviderId().getRoot()});
						MicroAgentInterpreter mai = new MicroAgentInterpreter(desc, factory, mm, getMicroAgentClass(model.getFullName()+"Agent", 
							null, cl), arguments, config, parent, binding, copy, realtime, persist, persistinfo, resultlistener, inited);
						res.setResult(new Tuple2<IComponentInstance, IComponentAdapter>(mai, mai.getComponentAdapter()));
					}
					catch(Exception e)
					{
						res.setException(e);
					}
				}
			});
		}
		
		// For platform bootstrapping
		else
		{
			try
			{
				ClassLoader	cl	= getClass().getClassLoader();
				MicroModel mm = loader.loadComponentModel(model.getFilename(), null, cl, new Object[]{model.getResourceIdentifier(), getProviderId().getRoot()});
				MicroAgentInterpreter mai = new MicroAgentInterpreter(desc, factory, mm, getMicroAgentClass(model.getFullName()+"Agent", 
					null, cl), arguments, config, parent, binding, copy, realtime, persist, persistinfo, resultlistener, inited);
				res.setResult(new Tuple2<IComponentInstance, IComponentAdapter>(mai, mai.getComponentAdapter()));
			}
			catch(Exception e)
			{
				res.setException(e);
			}
		}

		return res;
//		return new Future<Tuple2<IComponentInstance, IComponentAdapter>>(new Tuple2<IComponentInstance, IComponentAdapter>(mai, mai.getAgentAdapter()));
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
	public Map<String, Object>	getProperties(String type)
	{
		return FILETYPE_MICROAGENT.equals(type)
		? fproperties: null;
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
	protected Class<?> getMicroAgentClass(String clname, String[] imports, ClassLoader classloader)
	{
		Class<?> ret = SReflect.findClass0(clname, imports, classloader);
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
}
