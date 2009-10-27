package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.DefaultResultListener;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IContextService;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IPlatform;
import jadex.bridge.ISpace;
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
import jadex.service.library.ILibraryService;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for creating agent applications.
 */
public class ApplicationFactory implements IComponentFactory
{
	//-------- constants --------
	
	/** The application agent file type. */
	public static final String	FILETYPE_APPLICATION = "Agent Application";
	
	/** The application file extension. */
	public static final String	FILE_EXTENSION_APPLICATION	= ".application.xml";

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"application", SGUI.makeIcon(ApplicationFactory.class, "/jadex/adapter/base/images/application.png"),
	});
	
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform platform;
	
	/** The xml reader. */
	protected Reader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application factory.
	 *  @param platform	The agent platform.
	 *  @param mappings	The XML reader mappings (if any).
	 */
	public ApplicationFactory(IPlatform platform, Set[] mappings)// Set[] linkinfos)
	{
		this.platform = platform;
		
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
			listener.resultAvailable(null);
	}
	
	//-------- IAgentFactory interface --------
	
//	/**
//	 *  Create a new agent application.
//	 *  @param model	The agent model file (i.e. the name of the XML file).
//	 *  @param config	The name of the configuration (or null for default configuration) 
//	 *  @param arguments	The arguments for the agent as name/value pairs.
//	 *  @return	An instance of the application.
//	 */
//	public IApplicationContext createApplication(String name, String model, String config, Map arguments) throws Exception

	/**
	 * Create a component instance.
	 * @param model The component model file (i.e. the name of the XML file).
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @return An instance of a kernel agent.
	 */
	public IComponentInstance createComponentInstance(IComponentAdapter adapter, ILoadableComponentModel model, String config, Map arguments)
	{
		String name = adapter!=null? adapter.getComponentIdentifier().getLocalName(): "no_name";
		ApplicationContext	context = null;
		
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
		IContextService	cs	= (IContextService)platform.getService(IContextService.class);
		if(cs==null)
		{
			// Todo: use logger.
			System.out.println("Warning: No context service found. Application '"+name+"' may not work properly.");
		}
		else
		{
			Map	props	= new HashMap();
			props.put(ApplicationContext.PROPERTY_APPLICATION_TYPE, model);
			context	= (ApplicationContext)cs.createContext(name, IApplicationContext.class, props);
		}
		
		// todo: result listener?
		
		// todo: create application context as return value?!
		
		// Create spaces for context.
		if(cs!=null)
		{
			List spaces = app.getMSpaceInstances();
			if(spaces!=null)
			{
				for(int i=0; i<spaces.size(); i++)
				{
//					System.out.println(spaces.get(i));
					
					MSpaceInstance si = (MSpaceInstance)spaces.get(i);
					try
					{
						ISpace space = si.createSpace(context);
						context.addSpace(space);
						si.initSpace(space, context);
					}
					catch(Exception e)
					{
						System.out.println("Exception while creating space: "+si.getName());
					}
				}
			}
		}
		
		List agents = app.getMAgentInstances();
		ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
		for(int i=0; i<agents.size(); i++)
		{
			final MAgentInstance agent = (MAgentInstance)agents.get(i);
			
//			System.out.println("Create: "+agent.getName()+" "+agent.getTypeName()+" "+agent.getConfiguration());
			int num = agent.getNumber();
			for(int j=0; j<num; j++)
			{
//						ams.createAgent(agent.getName(), agent.getModel(apptype).getFilename(), agent.getConfiguration(), agent.getArguments(cl), new IResultListener()
//						{
//							public void exceptionOccurred(Exception exception)
//							{
//							}
//							public void resultAvailable(Object result)
//							{
//								if(appcontext!=null)
//									appcontext.addAgent((IComponentIdentifier) result);
//								
//								if(agent.isStart())
//									ams.startAgent((IComponentIdentifier)result, null);
//							}
//						}, null);						
				context.createAgent(agent.getName(), agent.getTypeName(),
					agent.getConfiguration(), agent.getArguments(platform, apptype, cl), agent.isStart(), agent.isMaster(),
					DefaultResultListener.getInstance(), null);	
			}
		}
		
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
				ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
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
	public String[] getFileTypes()
	{
		return new String[]{FILETYPE_APPLICATION};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type)
	{
		return type.equals(FILETYPE_APPLICATION)? icons.getIcon("application"): null;
	}


	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model)
	{
		return model.toLowerCase().endsWith(FILE_EXTENSION_APPLICATION)? FILETYPE_APPLICATION: null;
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