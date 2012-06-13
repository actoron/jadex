package jadex.micro.testcases.securetrans;

import jadex.bridge.service.annotation.SecureTransmission;
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
public class ProviderAgent implements ITestService
{
	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	@SecureTransmission
	public IFuture<Void> secMethod(String msg)
	{
		System.out.println("Called secMethod: "+msg);
		return IFuture.DONE;
	}
	
	/**
	 *  Call a method that can use any transport.
	 */
	public IFuture<Void> unsecMethod(String msg)
	{
		System.out.println("Called unsecMethod: "+msg);
		return IFuture.DONE;
	}
}
