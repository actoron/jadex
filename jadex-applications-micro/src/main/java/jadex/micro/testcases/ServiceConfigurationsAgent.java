package jadex.micro.testcases;

import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@ProvidedServices(@ProvidedService(type=IAService.class, implementation=@Implementation(expression="$component")))
@Configurations({
	@Configuration(name="a"),
	@Configuration(name="b", providedservices=@ProvidedService(type=IAService.class, 
		implementation=@Implementation(expression="$component.getService()")))
})
public class ServiceConfigurationsAgent extends MicroAgent implements IAService
{
	/**
	 *  Agent created.
	 */
	public IFuture agentCreated()
	{
		System.out.println("service impl: "+getServiceContainer().getProvidedService(IAService.class));
		return super.agentCreated();
	}
	
	/**
	 * 
	 */
	public IFuture test()
	{
		System.out.println("a");
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public static IAService getService()
	{
		return new IAService()
		{
			public IFuture test()
			{
				System.out.println("b");
				return IFuture.DONE;
			}
		};
	}
}