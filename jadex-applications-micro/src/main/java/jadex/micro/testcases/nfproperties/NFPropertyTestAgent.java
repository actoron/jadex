package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.testcases.serviceimpl.IInfoService;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=ICoreDependentService.class))
public class NFPropertyTestAgent implements ICoreDependentService
{
	@Agent
	protected MicroAgent agent;
	
	@AgentBody
	public IFuture<Void> body()
	{
		ICoreDependentService cds = SServiceProvider.getService(agent.getServiceProvider(), ICoreDependentService.class).get();
		IService iscds = (IService) cds;
		String[] names = iscds.getNonFunctionalPropertyNames().get();
		
		System.out.println("Begin list of non-functional properties:");
		for (String name : names)
		{
			System.out.println(name);
		}
		System.out.println("Finished list of non-functional properties.");
		
		System.out.println("Value: " + iscds.getNonFunctionalPropertyValue("cores", Integer.class).get());
		
		return IFuture.DONE;
	}
}
