package jadex.component;

import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentFactoryExtensionService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
/* $if !android $ */
import jadex.commons.gui.SGUI;
/* $endif $ */
import jadex.kernelbase.CacheableKernelModel;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* $if !android $ */
import javax.swing.UIDefaults;
/* $endif $ */

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ComponentComponentFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The component component file type. */
	public static final String	FILETYPE_COMPONENT = "Component";
	
//	/** The application file extension. */
//	public static final String	FILE_EXTENSION_APPLICATION	= ".application.xml";

	/**
	 * The image icons.
	 */
	/* $if !android $ */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"component", SGUI.makeIcon(ComponentComponentFactory.class, "/jadex/component/images/component.png"),
	});
	/* $endif $ */
	
	//-------- attributes --------
	
	/** The application model loader. */
	protected ComponentModelLoader loader;
	
	/** The provider. */
	protected IServiceProvider provider;
	
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
		super(providerid, IComponentFactory.class, null);
		this.loader = new ComponentModelLoader(new Set[0]);
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
	public IFuture startService()
	{
		final Future ret = new Future();
		super.startService().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService libservice = (ILibraryService)result;
						
						SServiceProvider.getServices(provider, IComponentFactoryExtensionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								Collection fes = (Collection)result;
								
								CollectionResultListener lis = new CollectionResultListener(fes.size(), true, new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										Collection exts = (Collection)result;
										Set[] mappings = (Set[])exts.toArray(new Set[exts.size()]);
										
										loader = new ComponentModelLoader(mappings);
										
										libservicelistener = new ILibraryServiceListener()
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
										
										libservice.addLibraryServiceListener(libservicelistener);

										ret.setResult(null);
									}
								});
								
								for(Iterator it=fes.iterator(); it.hasNext(); )
								{
									IComponentFactoryExtensionService fex = (IComponentFactoryExtensionService)it.next();
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
	
	//-------- IComponentFactory interface --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture loadModel(String model, String[] imports, ClassLoader classloader)
	{
		Future ret = new Future();
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
	
	/**
	 * Create a component instance.
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the component as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public IFuture createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, 
		IModelInfo modelinfo, String config, Map arguments, IExternalAccess parent, RequiredServiceBinding[] bindings, boolean copy, Future ret)
	{
		try
		{
			CacheableKernelModel model = loader.loadComponentModel(modelinfo.getFilename(), null, modelinfo.getClassLoader());
//			List apps = apptype.getConfigurations();
					
//			// Select application instance according to configuration.
//			MConfiguration app = null;
//			if(config!=null)
//			{
//				for(int i=0; app==null && i<apps.size(); i++)
//				{
//					MConfiguration tmp = (MConfiguration)apps.get(i);
//					if(config.equals(tmp.getName()))
//						app = tmp;
//				}
//			}
//			if(app==null && apps.size()>0)
//			{
//				app = (MConfiguration)apps.get(0);
//			}
//			if(app==null)
//				app = new MConfiguration("default");
	
			// Create context for application.
			ComponentInterpreter interpreter = new ComponentInterpreter(desc, model.getModelInfo(), config, factory, parent, arguments, bindings, copy, ret);
			
			// todo: result listener?
			// todo: create application context as return value?!
					
			return new Future(new Object[]{interpreter, interpreter.getComponentAdapter()});
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
		
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture isLoadable(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.endsWith(ComponentModelLoader.FILE_EXTENSION_COMPONENT));
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture isStartable(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.endsWith(ComponentModelLoader.FILE_EXTENSION_COMPONENT));
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
	/* $if !android $ */
	public IFuture getComponentTypeIcon(String type)
	{
		return new Future(type.equals(FILETYPE_COMPONENT)? icons.getIcon("component"): null);
	}
	/* $endif $ */

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture getComponentType(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.toLowerCase().endsWith(ComponentModelLoader.FILE_EXTENSION_COMPONENT)? FILETYPE_COMPONENT: null);
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

	//-------- helper methods --------
	
	/**
	 *  Load an xml Jadex model.
	 *  Creates file name when specified with or without package.
	 *  @param xml The filename | fully qualified classname
	 *  @return The loaded model.
	 * /
	// Todo: fix directory stuff!???
	// Todo: Abstract model loader unifying app/bdi loading
	public static ResourceInfo getResourceInfo(String xml, String suffix, String[] imports, ClassLoader classloader) throws IOException
	{
		if(xml==null)
			throw new IllegalArgumentException("Required ADF name nulls.");
		if(suffix==null && !xml.endsWith(ApplicationModelLoader.FILE_EXTENSION_APPLICATION))
			throw new IllegalArgumentException("Required suffix nulls.");

		if(suffix==null)
			suffix="";
		
		// Try to find directly as absolute path.
		String resstr = xml;
		ResourceInfo ret = SUtil.getResourceInfo0(resstr, classloader);

		if(ret==null || ret.getInputStream()==null)
		{
			// Fully qualified package name? Can also be full package name with empty package ;-)
			//if(xml.indexOf(".")!=-1)
			//{
				resstr	= SUtil.replace(xml, ".", "/") + suffix;
				//System.out.println("Trying: "+resstr);
				ret	= SUtil.getResourceInfo0(resstr, classloader);
			//}

			// Try to find in imports.
			for(int i=0; (ret==null || ret.getInputStream()==null) && imports!=null && i<imports.length; i++)
			{
				// Package import
				if(imports[i].endsWith(".*"))
				{
					resstr = SUtil.replace(imports[i].substring(0,
						imports[i].length()-1), ".", "/") + xml + suffix;
					//System.out.println("Trying: "+resstr);
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
				// Direct import
				else if(imports[i].endsWith(xml))
				{
					resstr = SUtil.replace(imports[i], ".", "/") + suffix;
					//System.out.println("Trying: "+resstr);
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
			}
		}

		if(ret==null || ret.getInputStream()==null)
			throw new IOException("File "+xml+" not found in imports");//: "+SUtil.arrayToString(imports));

		return ret;
	}	*/	
}
