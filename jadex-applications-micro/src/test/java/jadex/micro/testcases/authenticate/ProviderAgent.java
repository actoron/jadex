package jadex.micro.testcases.authenticate;

import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Authenticated;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 *  Agent implementing the test service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class ProviderAgent implements ITestService
{
	/**
	 *  Allow calling a method only from an authenticated user.
	 */
	@Authenticated(virtuals="testuser")
	public IFuture<Void> method(String msg)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		System.out.println("Called method: "+msg+" "+sc.getTimeout()
			+" "+sc.isRealtime()+" "+sc.getProperties());
		return IFuture.DONE;
	}
}
