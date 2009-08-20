package jadex.bpmn.examples.wfms;

import jadex.wfms.BasicWfms;
import jadex.wfms.client.GuiClient;
import jadex.wfms.client.ProcessStarterClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAdminService;
import jadex.wfms.service.IBpmnProcessService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IGpmnProcessService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IWorkitemQueueService;
import jadex.wfms.service.impl.AdminConnector;
import jadex.wfms.service.impl.BasicAAAService;
import jadex.wfms.service.impl.BasicModelRepositoryService;
import jadex.wfms.service.impl.BpmnProcessService;
import jadex.wfms.service.impl.ClientConnector;
import jadex.wfms.service.impl.GpmnProcessService;

import java.util.HashSet;
import java.util.Set;

public class WfmsLauncher
{
	public static void main(String[] args)
	{
		BasicWfms wfms = new BasicWfms();
		String[] imports = { "jadex.wfms.*", "jadex.bpmn.examples.dipp.*" };
		BasicModelRepositoryService mr = new BasicModelRepositoryService(imports);
		//mr.addBpmnModel("Ladungstraeger absichern", "jadex/wfms/DiPP1_LOG_Ladungstraeger_absichern.bpmn");
		//mr.addBpmnModel("User Interaction", "jadex/wfms/UserInteraction.bpmn");
		mr.addBpmnModel("HelloWorld", "jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn");
		//mr.addGpmnModel("DiPP", "jadex/bmpn/examples/dipp/dipp.gpmn");
		wfms.addService(IModelRepositoryService.class, mr);
		BasicAAAService as = new BasicAAAService();
		Set roles = new HashSet();
		roles.add("all");
		as.addUser("TestUser", roles);
		wfms.addService(IAAAService.class, as);
		wfms.addService(IBpmnProcessService.class, new BpmnProcessService(wfms));
		wfms.addService(IGpmnProcessService.class, new GpmnProcessService(wfms));
		ClientConnector cc = new ClientConnector(wfms);
		wfms.addService(IClientService.class, cc);
		wfms.addService(IWorkitemQueueService.class, cc);
		wfms.addService(IAdminService.class, new AdminConnector(wfms));
		new GuiClient("TestUser", cc);
		new GuiClient("TestUser", cc);
		new ProcessStarterClient(wfms);
		//cc.startBpmnProcess("Test");
		//cc.startBpmnProcess("Test");
	}
}
