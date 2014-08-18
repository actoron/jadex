package jadex.component;

import jadex.bridge.ComponentIdentifier;
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
import jadex.bridge.service.search.LocalServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IComponentFactoryExtensionService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.LazyResource;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.kernelbase.IBootstrapFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ComponentComponentFactory extends BasicService implements IComponentFactory, IBootstrapFactory
{
	//-------- constants --------

	/** The supported component types (file extensions).
	 *  Convention used by platform config panel. */
	public static final String[]	FILETYPES	= new String[]{ComponentModelLoader.FILE_EXTENSION_COMPONENT};
	
	/** The component component file type. */
	public static final String	FILETYPE_COMPONENT = "Component";
	
//	/** The application file extension. */
//	public static final String	FILE_EXTENSION_APPLICATION	= ".application.xml";

	/** The image icon. */
	protected static final LazyResource ICON = new LazyResource(ComponentComponentFactory.class, "/jadex/component/images/component.png");
	
	//-------- attributes --------
	
	/** The application model loader. */
	protected ComponentModelLoader loader;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application factory for startup.
	 *  @param platform	The platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public ComponentComponentFactory(String providerid)
	{
		super(new ComponentIdentifier(providerid), IComponentFactory.class, null);
		// Todo: hack!!! make extensions configurable also for reflective constructor (how?)
		String[]	extensions	= new String[]
		{
			"jadex.extension.envsupport.MEnvSpaceType", "getXMLMapping",
			"jadex.extension.agr.AGRExtensionService", "getXMLMapping"
		};
		List<Set<?>>	mappings	= new ArrayList<Set<?>>();
		for(int i=0; i<extensions.length; i+=2)
		{
			try
			{
				Class<?>	clazz	= Class.forName(extensions[i], true, getClass().getClassLoader());
				Method	m	= clazz.getMethod(extensions[i+1], new Class[0]);
				mappings.add((Set<?>)m.invoke(null, new Object[0]));
			}
			catch(Exception e)
			{
//				e.printStackTrace();
			}
			
		}
		
		
		this.loader = new ComponentModelLoader(mappings.toArray(new Set[0]));
	}
	
	/**
	 *  Create a new application factory.
	 *  @param platform	The platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	public ComponentComponentFactory(IServiceProvider provider)
	{
		super(provider.getId(), IComponentFactory.class, null);
		this.provider = provider;
		// todo: get mappings whenever changes to extension providers occur or on each load model?
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService(IInternalAccess component, IResourceIdentifier rid)
	{
		this.provider = (IServiceProvider)component.getServiceContainer();
		this.providerid = provider.getId();
		createServiceIdentifier("BootstrapFactory", IComponentFactory.class, rid, IComponentFactory.class, null);
		return startService();
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService()
	{
		final Future<Void> ret = new Future<Void>();
		super.startService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
				{
					public void customResultAvailable(ILibraryService result)
					{
						libservice = result;
//						System.out.println("Got Libservice " + libservice);
						
						SServiceProvider.getServices(provider, IComponentFactoryExtensionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new ExceptionDelegationResultListener<Collection<IComponentFactoryExtensionService>, Void>(ret)
						{
							public void customResultAvailable(Collection<IComponentFactoryExtensionService> fes)
							{
								CollectionResultListener<Set<Object>> lis = new CollectionResultListener<Set<Object>>(fes.size(), true, new ExceptionDelegationResultListener<Collection<Set<Object>>, Void>(ret)
								{
									public void customResultAvailable(Collection<Set<Object>> exts)
									{
										Set<Object>[] mappings = (Set<Object>[])exts.toArray(new Set[exts.size()]);
										
										loader = new ComponentModelLoader(mappings);
										
										libservicelistener = new ILibraryServiceListener()
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
										
										libservice.addLibraryServiceListener(libservicelistener);
										
										ret.setResult(null);
									}
								});
								
								for(Iterator<IComponentFactoryExtensionService> it=fes.iterator(); it.hasNext(); )
								{
									IComponentFactoryExtensionService fex = it.next();
									fex.getExtension(FILETYPE_COMPONENT).addResultListener(lis);
								}
							}	
						});
					}
				});
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
				ComponentComponentFactory.super.shutdownService()
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
			
		return ret;
	}
	
	//-------- IComponentFactory interface --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(final String model, final String[] imports, final IResourceIdentifier rid)
	{
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
						ret.setResult(loader.loadComponentModel(model, imports, cl, 
							new Object[]{rid, getServiceIdentifier().getProviderId().getRoot()}).getModelInfo());
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
				ret.setResult(loader.loadComponentModel(model, imports, cl, 
					new Object[]{rid, getProviderId().getRoot()}).getModelInfo());
			}
			catch(Exception e)
			{
				ret.setException(e);
			}			
		}
		
		return ret;
		
		
//		System.out.println("filename: "+filename);
//		try
//		{
//			ret.setResult(loader.loadComponentModel(model, imports,
//				libservice==null? getClass().getClassLoader(): libservice.getClassLoader(rid), rid).getModelInfo());
//		}
//		catch(Exception e)
//		{
//			ret.setException(e);
//		}
//		
//		return ret;
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
		final IExternalAccess parent, @Reference final RequiredServiceBinding[] bindings, @Reference final ProvidedServiceInfo[] pinfos, final boolean copy, final boolean realtime, final boolean persist,
		final IPersistInfo persistinfo, 
		final IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> init, @Reference final LocalServiceRegistry registry)
	{
		final Future<Tuple2<IComponentInstance, IComponentAdapter>>	ret	= new Future<Tuple2<IComponentInstance, IComponentAdapter>>();
		
		if(libservice!=null)
		{
			libservice.getClassLoader(model.getResourceIdentifier()).addResultListener(
				new ExceptionDelegationResultListener<ClassLoader, Tuple2<IComponentInstance, IComponentAdapter>>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
//						CacheableKernelModel model = loader.loadComponentModel(modelinfo.getFilename(), null, cl, 
//							new Object[]{modelinfo.getResourceIdentifier(), getServiceIdentifier().getProviderId().getRoot()});
						ComponentInterpreter interpreter = new ComponentInterpreter(desc, model/*.getModelInfo()*/, config, factory, parent, 
							arguments, bindings, pinfos, copy, realtime, persist, persistinfo, resultlistener, init, cl, registry);
						ret.setResult(new Tuple2<IComponentInstance, IComponentAdapter>(interpreter, interpreter.getComponentAdapter()));
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
}
			});
		}
		
		// For platform ootstrapping 
		else
		{
			try
			{
				ClassLoader cl = getClass().getClassLoader();
//				CacheableKernelModel model = loader.loadComponentModel(modelinfo.getFilename(), null, cl, 
//					new Object[]{modelinfo.getResourceIdentifier(), getProviderId().getRoot()});
				ComponentInterpreter interpreter = new ComponentInterpreter(desc, model/*.getModelInfo()*/, config, factory, parent,
					arguments, bindings, pinfos, copy, realtime, persist, persistinfo, resultlistener, init, cl, registry);
				ret.setResult(new Tuple2<IComponentInstance, IComponentAdapter>(interpreter, interpreter.getComponentAdapter()));
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
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
		return new Future<Boolean>(model.endsWith(ComponentModelLoader.FILE_EXTENSION_COMPONENT));
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<Boolean>(model.endsWith(ComponentModelLoader.FILE_EXTENSION_COMPONENT));
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_COMPONENT};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
		Future<byte[]>	ret	= new Future<byte[]>();
		if(type.equals(FILETYPE_COMPONENT))
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
		return new Future<String>(model.toLowerCase().endsWith(ComponentModelLoader.FILE_EXTENSION_COMPONENT)? FILETYPE_COMPONENT: null);
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
		return FILETYPE_COMPONENT.equals(type)
			? Collections.EMPTY_MAP : null;
	}
}
