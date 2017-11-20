package jadex.micro.testcases.nfcallreturn;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class))
@Service
public class ProviderAgent implements ITestService
{
//	@Agent
//	protected MicroAgent agent;
	
	@AgentCreated
	public void created(IInternalAccess agent)
	{
		agent.getLogger().severe("Agent created: "+agent.getComponentDescription());
	}

	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	public IFuture<Void> method(String msg)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		
		System.out.println("Called method: "+msg+" "+sc.getTimeout()
			+" "+sc.isRealtime()+" "+sc.getProperties());
		
		sc.setProperty("extra", "someotherval");
		sc.setProperty("new", "new");
		
//		Future<Void> ret = new Future<Void>();
//		return ret;
		return IFuture.DONE;
	}
}
