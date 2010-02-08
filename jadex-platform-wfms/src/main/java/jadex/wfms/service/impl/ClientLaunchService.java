package jadex.wfms.service.impl;

import java.awt.EventQueue;

import com.daimler.client.GuiClient;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.wfms.client.ProcessStarterClient;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.simulation.ClientSimulator;

public class ClientLaunchService implements IService
{
	public ClientLaunchService(final IServiceContainer wfms)
	{
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
				mr.addProcessModel("jadex/wfms/simulation/testflow/Credit_Workflow.bpmn");
				mr.addProcessModel("jadex/wfms/simulation/testflow/testflow.gpmn");
				IClientService cs = (IClientService) wfms.getService(IClientService.class);
				new GuiClient("TestUser", cs);
				new ProcessStarterClient(cs);
			}
		});
	}
	
	public void startService()
	{
	}
	
	public void shutdownService(IResultListener listener)
	{	
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
}
