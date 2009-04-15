package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.contextservice.IContextService;
import jadex.adapter.base.contextservice.ISpace;
import jadex.bridge.IApplicationFactory;
import jadex.bridge.ILibraryService;
import jadex.bridge.ILoadableElementModel;
import jadex.bridge.IPlatform;
import jadex.commons.SGUI;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.Reader;
import jadex.commons.xml.TypeInfo;

import java.io.FileInputStream;
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
public class ApplicationFactory implements IApplicationFactory
{
	//-------- constants --------
	
	/** The application agent file type. */
	public static final String	FILETYPE_APPLICATION = "Agent Application";
	
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
	public ApplicationFactory(IPlatform platform, Set[] mappings, Set[] linkinfos)
	{
		this.platform = platform;
		
		Set types = new HashSet();
		Map attrs = new HashMap();
		attrs.put("type", "typeName");
		types.add(new TypeInfo("applicationtype", MApplicationType.class, "description", null));
		types.add(new TypeInfo("spacetype", MSpaceType.class));
		types.add(new TypeInfo("agenttype", MAgentType.class));
		types.add(new TypeInfo("application", MApplicationInstance.class, null, null, attrs, null, null));
		types.add(new TypeInfo("space", MSpaceInstance.class));
		types.add(new TypeInfo("agent", MAgentInstance.class, null, null, attrs, null, null));
		types.add(new TypeInfo("parameter", MArgument.class));
		types.add(new TypeInfo("parameterset", MArgumentSet.class));
		types.add(new TypeInfo("value", String.class));
		types.add(new TypeInfo("import", String.class));
		types.add(new TypeInfo("property", String.class));
		
		for(int i=0; mappings!=null && i<mappings.length; i++)
		{
			types.addAll(mappings[i]);
		}
		
		Set links = new HashSet();
		for(int i=0; linkinfos!=null && i<linkinfos.length; i++)
		{
			links.addAll(linkinfos[i]);
		}
		
		Set ignored = new HashSet();
		ignored.add("schemaLocation");
		
		this.reader = new Reader(new BeanObjectHandler(), types, links, ignored);
	}
	
	//-------- IAgentFactory interface --------
	
	/**
	 *  Create a new agent application.
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of the application.
	 */
	public Object createApplication(String name, String model, String config, Map arguments)
	{
		ApplicationContext	context = null;
		
		if(model!=null && model.toLowerCase().endsWith(".application.xml"))
		{
			MApplicationType apptype = null;
			try
			{
				// Load application type.
				ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
				apptype = (MApplicationType)reader.read(new FileInputStream(model), cl, null);
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
					props.put(ApplicationContext.PROPERTY_APPLICATION_TYPE, apptype);
					context	= (ApplicationContext)cs.createContext(name, ApplicationContext.class, props);
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
							MSpaceInstance si = (MSpaceInstance)spaces.get(i);
							ISpace space = si.createSpace(context);
							context.addSpace(space);
						}
					}
				}
				
				List agents = app.getMAgentInstances();
				
				for(int i=0; i<agents.size(); i++)
				{
					final MAgentInstance agent = (MAgentInstance)agents.get(i);
					
					System.out.println("Create: "+agent.getName()+" "+agent.getTypeName()+" "+agent.getConfiguration());
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
//									appcontext.addAgent((IAgentIdentifier) result);
//								
//								if(agent.isStart())
//									ams.startAgent((IAgentIdentifier)result, null);
//							}
//						}, null);						
						context.createAgent(agent.getName(), agent.getTypeName(),
							agent.getConfiguration(), agent.getArguments(platform, apptype, cl), agent.isStart(), agent.isMaster(),
							DefaultResultListener.getInstance(), null);	
					}
				}
			
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return context;
	}
	
	/**
	 *  Load an agent model.
	 *  @param filename The filename.
	 */
	public ILoadableElementModel loadModel(String filename)
	{
		ILoadableElementModel ret = null;
		
		if(filename!=null && filename.toLowerCase().endsWith(".application.xml"))
		{
			MApplicationType apptype = null;
			try
			{
				ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
				apptype = (MApplicationType)reader.read(new FileInputStream(filename), cl, null);
				ret = new ApplicationModel(apptype, filename);
//				System.out.println("Loaded application type: "+apptype);
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
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
		return model.endsWith(".application.xml");
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and should therefore also be loadable).
	 */
	public boolean isStartable(String model)
	{
		return model.endsWith(".application.xml");
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
		return model.toLowerCase().endsWith(".application.xml")? FILETYPE_APPLICATION: null;
	}
}
