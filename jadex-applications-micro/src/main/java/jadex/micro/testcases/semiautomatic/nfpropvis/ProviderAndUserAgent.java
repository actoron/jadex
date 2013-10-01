package jadex.micro.testcases.semiautomatic.nfpropvis;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IAService.class))
public class ProviderAndUserAgent extends UserAgent implements IAService
{
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** The test string. */
	protected long wait = (long)(Math.random()*1000);
	
	/** The invocation counter. */
	protected int cnt;
	
	/**
	 *  Test method.
	 */
	public IFuture<String> test()
	{
		System.out.println("invoked service: "+sid.getProviderId()+" cnt="+(++cnt)+" wait="+wait);
		agent.waitForDelay(wait).get();
		return new Future<String>(sid.toString());
	}
}
