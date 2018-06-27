package jadex.micro.testcases.blocking;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  An agent that provides the blocking service.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IBlockService.class))
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
		Future<Void> ret = new Future<Void>();
		if(millis>0)
		{
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(millis).get();
			ret.setResult(null);
		}
		else
		{
			// do not set result at all and block forever
			ret.get();
		}
		return ret;
	}
}
