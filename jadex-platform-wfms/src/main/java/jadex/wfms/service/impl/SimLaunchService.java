package jadex.wfms.service.impl;

import java.awt.EventQueue;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IComponentExecutionService;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.wfms.service.IAAAService;
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
				mr.addProcessModel("jadex/wfms/simulation/testflow/testflow.gpmn");
				BasicAAAService as = (BasicAAAService) wfms.getService(IAAAService.class);
				Set roles = new HashSet();
				roles.add(IAAAService.ALL_ROLES);
				as.addUser("TestUser", roles);
				//new ClientSimulator((IClientService) wfms.getService(IClientService.class));
				IComponentExecutionService ex = (IComponentExecutionService) wfms.getService(IComponentExecutionService.class);
				ex.createComponent(null, "jadex/wfms/bdi/clientinterface/WfmsClientInterface.agent.xml", null, null, false, null, null, null);
				ex.createComponent(null, "jadex/wfms/bdi/pdinterface/WfmsPdInterface.agent.xml", null, null, false, null, null, null);
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
