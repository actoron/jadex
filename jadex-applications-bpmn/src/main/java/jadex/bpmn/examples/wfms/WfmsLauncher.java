package jadex.bpmn.examples.wfms;

import jadex.wfms.BasicWfms;
import jadex.wfms.client.GuiClient;
import jadex.wfms.client.ProcessStarterClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IProcessDefinitionService;
import jadex.wfms.service.IWorkitemQueueService;
import jadex.wfms.service.impl.BasicAAAService;
import jadex.wfms.service.impl.BasicModelRepositoryService;
import jadex.wfms.service.impl.BpmnProcessService;
import jadex.wfms.service.impl.ClientConnector;
import jadex.wfms.service.impl.GpmnProcessService;
import jadex.wfms.service.impl.MetaExecutionService;
import jadex.wfms.service.impl.ProcessDefinitionConnector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class WfmsLauncher
{
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		BasicWfms wfms = new BasicWfms(null);
		String[] imports = {"jadex.wfms.*", "jadex.bpmn.examples.dipp.*" };
		BasicModelRepositoryService mr = new BasicModelRepositoryService(wfms, imports);
		
		//mr.addBpmnModel("Ladungstraeger absichern", "jadex/wfms/DiPP1_LOG_Ladungstraeger_absichern.bpmn");
		//mr.addBpmnModel("User Interaction", "jadex/wfms/UserInteraction.bpmn");
		mr.addProcessModel("jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn");
		mr.addProcessModel("jadex/bpmn/examples/dipp/dipp.gpmn");
		wfms.addService(IModelRepositoryService.class, "repo_service", mr);
		
		BasicAAAService as = new BasicAAAService();
		Set roles = new HashSet();
		roles.add("all");
		as.addUser("TestUser", roles);
		wfms.addService(IAAAService.class, "auth_service", as);
		
		List exeservices = new ArrayList();
		exeservices.add(new BpmnProcessService(wfms));
		exeservices.add(new GpmnProcessService(wfms));
		IExecutionService es = new MetaExecutionService(wfms, exeservices);
		wfms.addService(IExecutionService.class, "exe_service", es);
		
			
		ClientConnector cc = new ClientConnector(wfms);
		wfms.addService(IClientService.class, "client_service", cc);
		wfms.addService(IWorkitemQueueService.class, "workitem_service", cc);
		wfms.addService(IProcessDefinitionService.class, "procdef_service", new ProcessDefinitionConnector(wfms));
		
		new GuiClient("TestUser", cc);
		new ProcessStarterClient(cc);
//		(new Simulator(cc)).test();
	}
}
