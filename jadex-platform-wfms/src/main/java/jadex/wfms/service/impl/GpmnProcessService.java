package jadex.wfms.service.impl;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IExecutionService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
public class GpmnProcessService implements IExecutionService, IService
{
	//-------- attributes --------
	
	/** The WFMS */
	protected IServiceContainer wfms;

	/** The created processes. (processid -> agentid) */
	protected Map processes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	public GpmnProcessService(IServiceContainer wfms)
	{
		this.wfms = wfms;
		this.processes = new HashMap();
		
		// Absolute start time (for testing and benchmarking).
		//long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		//String conffile = "jadex/wfms/wfms_conf.xml";
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		/*String[] args = new String[0];
		if(args.length>0 && args[0].equals("-"+Platform.CONFIGURATION))
		{
			conffile = args[1];
			String[] tmp= new String[args.length-2];
			System.arraycopy(args, 2, tmp, 0, args.length-2);
			args = tmp;
		}*/
		//ClassLoader cl = Platform.class.getClassLoader();
//		Properties configuration = XMLPropertiesReader.readProperties(SUtil.getResource(conffile, cl), cl);
		/*Properties configuration = null;
		try
		{
			configuration = (Properties)PropertiesXMLHelper.getPropertyReader().read(SUtil.getResource(conffile, cl), cl, null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(configuration);
		platform = new Platform(configuration);
		((Platform) platform).start();
		if (wfms instanceof BasicWfms)
			((BasicWfms) wfms).setLogger(((Platform) platform).getLogger());
		
		long startup = System.currentTimeMillis() - starttime;
		((Platform) platform).getLogger().info("Platform startup time: " + startup + " ms.");
		
		((IAMS) platform.getService(IAMS.class)).addAMSListener(new IAMSListener()
		{
			
			public void agentRemoved(IAMSAgentDescription desc)
			{
				synchronized(GpmnProcessService.this)
				{
					processes.remove(desc.getName().getLocalName());
					((IWfmsClientService) GpmnProcessService.this.wfms.getService(IWfmsClientService.class)).fireProcessFinished(desc.getName().getLocalName());
				}
			}
			
			public void agentAdded(IAMSAgentDescription desc)
			{
			}
		});*/
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
	
	/**
	 *  Load a process model.
	 *  @param filename The file name.
	 *  @return The process model.
	 */
	public ILoadableComponentModel loadModel(String filename, String[] imports)
	{
		ILoadableComponentModel ret = null;
		ILibraryService ls = (ILibraryService) wfms.getService(ILibraryService.class);
		try
		{
			IComponentFactory factory = (IComponentFactory) wfms.getService(IComponentFactory.class, "gpmn_factory");
			ret = factory.loadModel(ls.getClassLoader().getResource(filename).getPath(), imports);
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
		final IComponentManagementService ces = (IComponentManagementService)wfms.getService(IComponentManagementService.class);
		ces.createComponent(String.valueOf(id), modelname, new CreationInfo(null, arguments, null, true, false), new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				processes.put(id, result);
				ces.addComponentListener((IComponentIdentifier) result, new IComponentListener() 
				{
					public void componentRemoved(IComponentDescription desc, Map results)
					{
						synchronized (GpmnProcessService.this)
						{
							processes.remove(id);
							
							Logger.getLogger("Wfms").log(Level.INFO, "Finished GPMN process " + id.toString());
							((AdministrationService) wfms.getService(IAdministrationService.class)).fireProcessFinished(id.toString());
						}
					}
					
					public void componentChanged(IComponentDescription desc)
					{
					}
					
					public void componentAdded(IComponentDescription desc)
					{
					}
				});
				ces.resumeComponent((IComponentIdentifier) result, null);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				Logger.getLogger("Wfms").log(Level.SEVERE, "Failed to start model: " + name);
			}
		}, null);
		
		
		/*ams.createAgent(name, modelname, null, null, new IResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				ams.startAgent((IAgentIdentifier)result, null);
				processes.put(id, result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		}, null);*/
		
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
}
