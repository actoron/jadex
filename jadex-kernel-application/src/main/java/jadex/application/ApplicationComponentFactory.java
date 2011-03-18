package jadex.application;

import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.runtime.impl.ApplicationInterpreter;
import jadex.application.space.agr.MAGRSpaceType;
import jadex.application.space.envsupport.MEnvSpaceType;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.UIDefaults;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ApplicationComponentFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The application component file type. */
	public static final String	FILETYPE_APPLICATION = "Application";
	
//	/** The application file extension. */
//	public static final String	FILE_EXTENSION_APPLICATION	= ".application.xml";

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"application", SGUI.makeIcon(ApplicationComponentFactory.class, "/jadex/application/images/application.png"),
	});
	
	//-------- attributes --------
	
	/** The application model loader. */
	protected ApplicationModelLoader loader;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application factory.
	 */
	public ApplicationComponentFactory(IServiceProvider provider)
	{
		this(null, provider);
	}
	
	/**
	 *  Create a new application factory for startup.
	 *  @param platform	The platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public ApplicationComponentFactory(String providerid)
	{
		super(providerid, IComponentFactory.class, null);
		
		// Todo: hack!!! make mappings configurable also for reflective constructor (how?)
		this.loader = new ApplicationModelLoader(new Set[]
		{
			MEnvSpaceType.getXMLMapping(),
			MAGRSpaceType.getXMLMapping()
		});
	}
	
	/**
	 *  Create a new application factory.
	 *  @param platform	The platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	public ApplicationComponentFactory(Set[] mappings, IServiceProvider provider)
	{
		super(provider.getId(), IComponentFactory.class, null);
		this.loader = new ApplicationModelLoader(mappings);
		this.provider = provider;
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
				ILibraryService libService = (ILibraryService)result;
				libService.addLibraryServiceListener(libservicelistener);
			}
		});
	}
	
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
			ret.setResult(loader.loadApplicationModel(model, imports, classloader).getModelInfo());
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
	public Object[] createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, 
		IModelInfo modelinfo, String config, Map arguments, IExternalAccess parent, RequiredServiceBinding[] bindings, Future ret)
	{
		try
		{
			MApplicationType apptype = loader.loadApplicationModel(modelinfo.getFilename(), null, modelinfo.getClassLoader());
			List apps = apptype.getMApplicationInstances();
					
			// Select application instance according to configuration.
			MApplicationInstance app = null;
				
			if(config!=null)
			{
				for(int i=0; app==null && i<apps.size(); i++)
				{
					MApplicationInstance tmp = (MApplicationInstance)apps.get(i);
					if(config.equals(tmp.getName()))
						app = tmp;
				}
			}
			if(app==null && apps.size()>0)
			{
				app = (MApplicationInstance)apps.get(0);
			}
			if(app==null)
				app = new MApplicationInstance("default");
	
			// Create context for application.
			ApplicationInterpreter interpreter = new ApplicationInterpreter(desc, apptype, app, factory, parent, arguments, bindings, ret);
			
			// todo: result listener?
			// todo: create application context as return value?!
					
			return new Object[]{interpreter, interpreter.getComponentAdapter()};
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
		return new Future(model.endsWith(ApplicationModelLoader.FILE_EXTENSION_APPLICATION));
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture isStartable(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.endsWith(ApplicationModelLoader.FILE_EXTENSION_APPLICATION));
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_APPLICATION};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture getComponentTypeIcon(String type)
	{
		return new Future(type.equals(FILETYPE_APPLICATION)? icons.getIcon("application"): null);
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture getComponentType(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.toLowerCase().endsWith(ApplicationModelLoader.FILE_EXTENSION_APPLICATION)? FILETYPE_APPLICATION: null);
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
		return FILETYPE_APPLICATION.equals(type)
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
