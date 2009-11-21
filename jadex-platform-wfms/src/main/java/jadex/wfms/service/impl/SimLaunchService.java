package jadex.wfms.service.impl;

import java.awt.EventQueue;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.simulation.ClientSimulator;

public class SimLaunchService implements IService
{
	public SimLaunchService(final IServiceContainer wfms)
	{
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
				mr.addProcessModel("jadex/wfms/simulation/testflow/Credit_Workflow.bpmn");
				new ClientSimulator((IClientService) wfms.getService(IClientService.class));
			}
		});
	}
	
	public void start()
	{
	}
	
	public void shutdown(IResultListener listener)
	{	
	}
}
