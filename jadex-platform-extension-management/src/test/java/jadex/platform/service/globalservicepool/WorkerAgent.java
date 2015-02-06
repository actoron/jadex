package jadex.platform.service.globalservicepool;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITestService.class))
public class WorkerAgent implements ITestService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	public IFuture<Void> methodA(int cnt)
	{
		System.out.println(cnt+" called methodA on: "+agent.getComponentIdentifier());
		return IFuture.DONE;
	}
}