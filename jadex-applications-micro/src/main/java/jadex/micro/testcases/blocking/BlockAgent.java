package jadex.micro.testcases.blocking;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  An agent that provides the blocking service.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IBlockService.class,
	implementation=@Implementation(expression="$pojoagent")))
public class BlockAgent	implements	IBlockService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	//-------- IBlockingService interface --------
	
	/**
	 *  Block until the given time has passed.
	 */
	public IFuture<Void>	block(long millis)
	{
		agent.waitForDelay(millis).get();
		
		return IFuture.DONE;
	}
}
