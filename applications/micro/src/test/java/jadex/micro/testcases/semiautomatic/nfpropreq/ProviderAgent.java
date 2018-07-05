package jadex.micro.testcases.semiautomatic.nfpropreq;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IAService.class))
public class ProviderAgent implements IAService
{
	@Agent
	protected IInternalAccess agent;
	
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
		agent.getFeature(IExecutionFeature.class).waitForDelay(wait).get();
		return new Future<String>(sid.toString());
	}
}
