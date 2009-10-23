package jadex.wfms.service.execution;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.standalone.Platform;
import jadex.bridge.IElementListener;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ILoadableElementModel;
import jadex.bridge.IPlatform;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.gpmn.GpmnXMLReader;
import jadex.gpmn.model.MGpmnModel;
import jadex.microkernel.MicroAgentFactory;
import jadex.service.PropertiesXMLHelper;
import jadex.service.library.ILibraryService;
import jadex.wfms.IProcessModel;
import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.repository.IModelRepositoryService;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 * 
 */
public class GpmnExecutionService implements IExecutionService
{
	//-------- constants --------
	
	/** The gpmn process file type. */
	public static final String	FILETYPE_GPMNPROCESS = "GPMN Process";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"gpmn_process",	SGUI.makeIcon(MicroAgentFactory.class, "/jadex/microkernel/images/micro_agent.png"),
	});
	
	//-------- attributes --------
	
	/** The WFMS */
	protected IWfms wfms;
	
	/** The platform */
	protected IPlatform platform;

	/** The created processes. (processid -> agentid) */
	protected Map processes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	public GpmnExecutionService(IWfms wfms)
	{
		this.wfms = wfms;
		this.processes = new HashMap();
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		String conffile = "jadex/bpmnbdi/standalone_bpmn_conf.xml";
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		String[] args = new String[0];
		if(args.length>0 && args[0].equals("-"+Platform.CONFIGURATION))
		{
			conffile = args[1];
			String[] tmp= new String[args.length-2];
			System.arraycopy(args, 2, tmp, 0, args.length-2);
			args = tmp;
		}
		ClassLoader cl = Platform.class.getClassLoader();
//		Properties configuration = XMLPropertiesReader.readProperties(SUtil.getResource(conffile, cl), cl);
		Properties configuration = null;
		try
		{
			configuration = (Properties)PropertiesXMLHelper.getPropertyReader().read(SUtil.getResource(conffile, cl), cl, null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(configuration);
		
		platform = new Platform(configuration, wfms);
		((Platform)platform).start();
		
		long startup = System.currentTimeMillis() - starttime;
		((Platform)platform).getLogger().info("Platform startup time: " + startup + " ms.");
		
		((IAMS)platform.getService(IAMS.class)).addAMSListener(new IElementListener()
		{
			
			public void agentRemoved(IAMSAgentDescription desc)
			{
				synchronized(GpmnExecutionService.this)
				{
					processes.remove(desc.getName().getLocalName());
				}
			}
			
			public void agentAdded(IAMSAgentDescription desc)
			{
			}
		});
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
	}
	
	/**
	 *  Load a process model.
	 *  @param filename The file name.
	 *  @return The process model.
	 */
	public ILoadableElementModel loadModel(String filename)
	{
		IProcessModel ret = null;
		try
		{
			ret = GpmnXMLReader.read(filename, ((ILibraryService)wfms.getService(ILibraryService.class)).getClassLoader(), null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 * Starts a Gpmn process
	 * @param name name of the Gpmn model
	 * @param stepmode if true, the process will start in step mode
	 * @return instance name
	 */
	public Object startProcess(String modelname, final Object id, Map arguments, boolean stepmode)
	{
		final String name = id.toString();
		final IAMS ams = (IAMS)platform.getService(IAMS.class);
		ams.createAgent(name, modelname, null, null, new IResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				ams.startAgent((IAgentIdentifier)result, null);
				processes.put(id, result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		}, null);
		
		return id;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param modelname The model name.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String modelname)
	{
		return modelname.endsWith(".gpmn");
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and loadable).
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
		return new String[]{FILETYPE_GPMNPROCESS};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type)
	{
		return type.equals(FILETYPE_GPMNPROCESS) ? icons.getIcon("micro_agent") : null;
	}

	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model)
	{
		return model.toLowerCase().endsWith("agent.class") ? FILETYPE_GPMNPROCESS: null;
	}
}
