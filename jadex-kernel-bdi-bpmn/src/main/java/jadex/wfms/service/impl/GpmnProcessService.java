package jadex.wfms.service.impl;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.standalone.Platform;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;
import jadex.bridge.Properties;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.wfms.IWfms;
import jadex.wfms.service.IGpmnProcessService;
import jadex.wfms.service.IModelRepositoryService;

import java.util.HashMap;
import java.util.Map;

public class GpmnProcessService implements IGpmnProcessService
{
	/** The WFMS */
	private IWfms wfms;
	
	/** Running Gpmn process instances */
	private Map processes;
	
	/** Counter for instance names */
	private long instanceCounter;
	
	/** The platform */
	private IPlatform platform;
	
	public GpmnProcessService(IWfms wfms)
	{
		this.instanceCounter = 0;
		this.processes = new HashMap();
		this.wfms = wfms;
		
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
			configuration = (Properties)Platform.getPropertyReader().read(SUtil.getResource(conffile, cl), cl, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(configuration);
		platform = new Platform(configuration);
		((Platform) platform).start();
		
		long startup = System.currentTimeMillis() - starttime;
		((Platform) platform).getLogger().info("Platform startup time: " + startup + " ms.");
	}
	
	/**
	 * Starts a Gpmn process
	 * @param name name of the Gpmn model
	 * @param stepmode if true, the process will start in step mode
	 * @return instance name
	 */
	public synchronized String startProcess(String name)
	{
		IModelRepositoryService mr = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		String modelPath = mr.getGpmnModel(name);
		
		String tmpName;
		do
			tmpName = name + "_" + String.valueOf(++instanceCounter);
		while (processes.containsKey(tmpName));
		final String instanceName = tmpName;
		final IAMS ams = (IAMS) platform.getService(IAMS.class);
		ams.createAgent(instanceName, modelPath, null, null, new IResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				ams.startAgent((IAgentIdentifier) result, null);
				processes.put(instanceName, result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		}, null);
		return instanceName;
	}
}
