package jadex.micro.testcases;

import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=IAService.class, implementation=@Implementation(expression="$component")))
public class AAgent extends MicroAgent implements IAService
{
	/**
	 *  Init service method.
	 */
	public IFuture test()
	{
//		System.out.println("called service");
		return IFuture.DONE;
	}
	
	/**
	 *  The agent body.
	 */
	public void executeBody()
	{
		killAgent();
	}
}
