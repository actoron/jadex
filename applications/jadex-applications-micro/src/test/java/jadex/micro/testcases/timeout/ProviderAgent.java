package jadex.micro.testcases.timeout;

import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class ProviderAgent implements ITestService
{
//	@Agent
//	protected MicroAgent agent;
	
	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	public IFuture<Void> method(String msg)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
//		System.out.println("Called method: "+msg+" "+sc.getTimeout()
//			+" "+sc.isRealtime()+" "+sc.getProperties());
		Future<Void> ret = new Future<Void>();
		return ret;
//		return IFuture.DONE;
	}
}
