package jadex.platform.service.globalservicepool;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
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
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	public IFuture<Void> methodA()
	{
		System.out.println("Called methodA");
		return IFuture.DONE;
	}
}