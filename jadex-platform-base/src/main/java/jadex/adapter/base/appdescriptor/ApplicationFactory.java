package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.AgentCreationException;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IAgentModel;
import jadex.bridge.IKernelAgent;
import jadex.bridge.IPlatform;
import jadex.commons.SGUI;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for creating agent applications.
 */
public class ApplicationFactory implements IAgentFactory
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
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public ApplicationFactory(IPlatform platform)
	{
		this.platform = platform;
	}
	
	//-------- IAgentFactory interface --------
	
	/**
	 *  Create a kernel agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a kernel agent.
	 */
	public IKernelAgent createKernelAgent(IAgentAdapter adapter, String model, String config, Map arguments)
	{
		IKernelAgent ret = null;
		
		if(model!=null && model.toLowerCase().endsWith(".application.xml"))
		{
			ApplicationType apptype = null;
			try
			{
				// todo: classloader null?
				apptype = XMLApplicationReader.readApplication(new FileInputStream(model), null);
				List apps = apptype.getApplications();
				
				Application app = null;
				if(config==null)
					app = (Application)apps.get(0);
				
				for(int i=0; app==null && i<apps.size(); i++)
				{
					Application tmp = (Application)apps.get(i);
					if(config.equals(tmp.getName()))
						app = tmp;
				}
				
				if(app==null)
					throw new RuntimeException("Could not finded application name: "+config);
				
				// todo: result listener?
				
				List agents = app.getAgents();
				for(int i=0; i<agents.size(); i++)
				{
					Agent agent = (Agent)agents.get(i);
					
					IAMS ams = (IAMS)platform.getService(IAMS.class);
//					ams.createAgent(agent.getName(), agent.getModel(), agent.getConfiguration(), agent.getArguments(), null);
				}
				
				System.out.println("Loaded application type: "+apptype);
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(ret==null)
			throw new AgentCreationException(""+model, null);
		return ret;
	}
	
	/**
	 *  Load an agent model.
	 *  @param filename The filename.
	 */
	public IAgentModel loadModel(String filename)
	{
		IAgentModel ret = null;
		
		if(filename!=null && filename.toLowerCase().endsWith(".application.xml"))
		{
			ApplicationType apptype = null;
			try
			{
				// todo: classloader null?
				apptype = XMLApplicationReader.readApplication(new FileInputStream(filename), null);
				ret = new ApplicationModel(apptype, filename);
				System.out.println("Loaded application type: "+apptype);
			
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
		return true;
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and should therefore also be loadable).
	 */
	public boolean isStartable(String model)
	{
		return true;
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
