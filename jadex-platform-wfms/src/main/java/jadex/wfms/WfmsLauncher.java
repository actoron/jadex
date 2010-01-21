package jadex.wfms;

import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.service.PropertiesXMLHelper;
import jadex.wfms.client.ProcessStarterClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.impl.BasicAAAService;
import jadex.wfms.service.impl.BasicModelRepositoryService;

import java.util.HashSet;
import java.util.Set;

import com.daimler.client.GuiClient;


public class WfmsLauncher
{
	private BasicWfms wfms;
	
	/**
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		WfmsLauncher launcher = new WfmsLauncher();
		launcher.launchBasicWfms(args);
		launcher.launchGuiClient();
		launcher.launchProcessStarter();
	}
	
	public BasicWfms getWfms()
	{
		return wfms;
	}
	
	public void launchBasicWfms(String[] args) throws Exception
	{
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		String conffile = BasicWfms.FALLBACK_CONFIGURATION;
		if(args.length>0 && args[0].equals("-" + BasicWfms.CONFIGURATION))
		{
			conffile = args[1];
			String[] tmp= new String[args.length-2];
			System.arraycopy(args, 2, tmp, 0, args.length-2);
			args = tmp;
		}
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		ClassLoader cl = this.getClass().getClassLoader(); //Platform.class.getClassLoader();
		Properties configuration = (Properties)PropertiesXMLHelper.getPropertyReader().read(SUtil.getResource(conffile, cl), cl, null);
		wfms = new BasicWfms(configuration);
		wfms.start();
		
		long startup = System.currentTimeMillis() - starttime;
		((BasicWfms) wfms).getLogger().info("Wfms startup time: " + startup + " ms.");
		
		/*String[] imports = {"jadex.wfms.*", "jadex.bpmn.examples.dipp.*" };
		BasicModelRepositoryService mr = new BasicModelRepositoryService(wfms, imports);
		
		List exeservices = new ArrayList();
		exeservices.add(new BpmnProcessService(wfms));
		exeservices.add(new GpmnProcessService(wfms));
		IExecutionService es = new MetaExecutionService(wfms, exeservices);
		wfms.addService(IExecutionService.class, "exe_service", es);*/
		
		//mr.addBpmnModel("Ladungstraeger absichern", "jadex/wfms/DiPP1_LOG_Ladungstraeger_absichern.bpmn");
		//mr.addBpmnModel("User Interaction", "jadex/wfms/UserInteraction.bpmn");
		//mr.addProcessModel("jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn");
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		//mr.addProcessModel("jadex/wfms/simulation/testflow/testflow.gpmn");
		mr.addProcessModel("jadex/wfms/simulation/testflow/Credit_Workflow.bpmn");
		//mr.addProcessModel("jadex/bpmn/examples/dipp/dipp.gpmn");
		mr.addProcessModel("jadex/bpmn/examples/helloworld/UserInteraction2.bpmn");
		//wfms.addService(IModelRepositoryService.class, "repo_service", mr);
		
		BasicAAAService as = (BasicAAAService) wfms.getService(IAAAService.class);
		Set roles = new HashSet();
		roles.add("all");
		//as.addUser("TestUser", roles);
		((BasicWfms) wfms).addService(IAAAService.class, "auth_service", as);
			
		/*ClientConnector cc = new ClientConnector(wfms);
		wfms.addService(IClientService.class, "client_service", cc);
		wfms.addService(IWorkitemQueueService.class, "workitem_service", cc);
		wfms.addService(IProcessDefinitionService.class, "procdef_service", new ProcessDefinitionConnector(wfms));*/
		
	}
	
	public IClientService getClientService()
	{
		return (IClientService) wfms.getService(IClientService.class);
	}
	
	public void launchGuiClient()
	{
		new GuiClient("TestUser", getClientService());
	}
	
	public void launchProcessStarter()
	{
		new ProcessStarterClient(getClientService());
	}
}
