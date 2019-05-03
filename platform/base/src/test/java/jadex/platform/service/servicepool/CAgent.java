package jadex.platform.service.servicepool;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Functionality agent that provides service C.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ICService.class))
public class CAgent implements ICService
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Example method 1.
	 */
	public IFuture<String> ma1(String str)
	{
//		System.out.println("ma1 called with: "+str+" "+agent.getComponentIdentifier().getLocalName());
		return new Future<String>(str+" result of ma1");
	}
}
