package jadex.platform.service.servicepool;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Functionality agent that provides service A.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IBService.class, implementation=@Implementation(expression="$pojoagent")))
public class BAgent implements IBService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Example method 1.
	 */
	public IFuture<String> mb1(String str)
	{
		return new Future<String>(str+" result of mb1");
	}
	
//	@AgentKilled
//	public IFuture<Void> killed()
//	{
//		System.out.println("killed: "+agent.getComponentIdentifier());
//		return IFuture.DONE;
//	}
}
