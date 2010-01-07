package jadex.application;

import jadex.application.model.ApplicationModel;
import jadex.application.model.MAgentInstance;
import jadex.application.model.MAgentType;
import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.model.MArgument;
import jadex.application.model.MSpaceInstance;
import jadex.application.model.MSpaceType;
import jadex.application.runtime.Application;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.ResourceInfo;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.QName;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;
import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.reader.Reader;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ApplicationComponentFactory	implements IComponentFactory
{
	//-------- constants --------
	
	/** The application agent file type. */
	public static final String	FILETYPE_APPLICATION = "Application";
	
	/** The application file extension. */
	public static final String	FILE_EXTENSION_APPLICATION	= ".application.xml";

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"application", SGUI.makeIcon(ApplicationComponentFactory.class, "/jadex/application/images/application.png"),
	});
	
	//-------- attributes --------
	
	/** The platform. */
	protected IServiceContainer container;
	
	/** The xml reader. */
	protected Reader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application factory.
	 *  @param platform	The agent platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	public ApplicationComponentFactory(IServiceContainer container, Set[] mappings)// Set[] linkinfos)
	{
		this.container = container;
		
		Set types = new HashSet();
		
		types.add(new TypeInfo(null, "applicationtype", MApplicationType.class, "description", null,
			new BeanAttributeInfo[]{new BeanAttributeInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AttributeInfo.IGNORE_READWRITE)}, null));
		types.add(new TypeInfo(null, "spacetype", MSpaceType.class));
		types.add(new TypeInfo(null, "agenttype", MAgentType.class));
		types.add(new TypeInfo(null, "application", MApplicationInstance.class, null, null, new BeanAttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, null));
		types.add(new TypeInfo(null, "space", MSpaceInstance.class));
		types.add(new TypeInfo(null, "agent", MAgentInstance.class, null, null, new BeanAttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, null));
		types.add(new TypeInfo(null, "argument", MArgument.class, null, "value"));
		types.add(new TypeInfo(null, "import", String.class));

//		String uri = "http://jadex.sourceforge.net/jadex-envspace";
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "applicationtype")}, MApplicationType.class, "description", null,
//			new BeanAttributeInfo[]{new BeanAttributeInfo("schemaLocation", null, AttributeInfo.IGNORE_READWRITE)}, null));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "spacetype")}, MSpaceType.class));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "agenttype")}, MAgentType.class));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "application")}, MApplicationInstance.class, null, null, new BeanAttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, null));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "space")}, MSpaceInstance.class));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "agent")}, MAgentInstance.class, null, null, new BeanAttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, null));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "argument")}, MArgument.class, null, "value"));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "import")}, String.class));
		
		for(int i=0; mappings!=null && i<mappings.length; i++)
		{
			types.addAll(mappings[i]);
		}
		
//		Set links = new HashSet();
//		for(int i=0; linkinfos!=null && i<linkinfos.length; i++)
//		{
//			links.addAll(linkinfos[i]);
//		}
		
//		Set ignored = new HashSet();
//		ignored.add("schemaLocation");
		
		this.reader = new Reader(new BeanObjectReaderHandler(types));
	}
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
	
	//-------- IComponentFactory interface --------
	
	/**
	 * Create a component instance.
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public IComponentInstance createComponentInstance(IComponentAdapter adapter, ILoadableComponentModel model, String config, Map arguments, IExternalAccess parent)
	{
		String name = adapter!=null? adapter.getComponentIdentifier().getLocalName(): "no_name";
		Application	context = null;
		
		MApplicationType apptype = ((ApplicationModel)model).getApplicationType();
		List apps = apptype.getMApplicationInstances();
				
		// Select application instance according to configuraion.
		MApplicationInstance app = null;
		if(config==null)
			app = (MApplicationInstance)apps.get(0);
		
		for(int i=0; app==null && i<apps.size(); i++)
		{
			MApplicationInstance tmp = (MApplicationInstance)apps.get(i);
			if(config.equals(tmp.getName()))
				app = tmp;
		}
		
		if(app==null)
			throw new RuntimeException("Could not find application name: "+config);


		// Create context for application.
		context	= new Application(name, (ApplicationModel)model, app, adapter, parent);
		
		// todo: result listener?
		
		// todo: create application context as return value?!
				
		return context;
	}
	
	/**
	 *  Load an agent model.
	 *  @param filename The filename.
	 */
	public ILoadableComponentModel loadModel(String filename)
	{
		ILoadableComponentModel ret = null;
		
		if(filename!=null && filename.toLowerCase().endsWith(FILE_EXTENSION_APPLICATION))
		{
			MApplicationType apptype = null;
			ResourceInfo	rinfo	= null;
			try
			{
				ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader();
				rinfo	= getResourceInfo(filename, FILE_EXTENSION_APPLICATION, null, cl);
				apptype = (MApplicationType)reader.read(rinfo.getInputStream(), cl, null);
				ret = new ApplicationModel(apptype, filename, cl);
//				System.out.println("Loaded application type: "+apptype);
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			finally
			{
				if(rinfo!=null)
					rinfo.cleanup();				
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model)
	{
		return model.endsWith(FILE_EXTENSION_APPLICATION);
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and should therefore also be loadable).
	 */
	public boolean isStartable(String model)
	{
		return model.endsWith(FILE_EXTENSION_APPLICATION);
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
	public Icon getComponentTypeIcon(String type)
	{
		return type.equals(FILETYPE_APPLICATION)? icons.getIcon("application"): null;
	}


	/**
	 *  Get the file type of a model.
	 */
	public String getComponentType(String model)
	{
		return model.toLowerCase().endsWith(FILE_EXTENSION_APPLICATION)? FILETYPE_APPLICATION: null;
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
	 */
	// Todo: fix directory stuff!???
	// Todo: Abstract model loader unifying app/bdi loading
	public static ResourceInfo getResourceInfo(String xml, String suffix, String[] imports, ClassLoader classloader) throws IOException
	{
		if(xml==null)
			throw new IllegalArgumentException("Required ADF name nulls.");
		if(suffix==null && !xml.endsWith(FILE_EXTENSION_APPLICATION))
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
			throw new IOException("File "+xml+" not found in imports: "+SUtil.arrayToString(imports));

		return ret;
	}	
}
