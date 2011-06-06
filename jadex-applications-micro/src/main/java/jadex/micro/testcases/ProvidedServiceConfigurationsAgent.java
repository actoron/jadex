package jadex.micro.testcases;

import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
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
public class ProvidedServiceConfigurationsAgent extends MicroAgent implements IAService
{
	/**
	 *  Agent created.
	 */
	public IFuture agentCreated()
	{
		IAService as = (IAService)getServiceContainer().getProvidedServices(IAService.class)[0];
		as.test().addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				System.out.println(result);
			}
		});
		return super.agentCreated();
	}
	
	/**
	 * 
	 */
	public IFuture test()
	{
		return new Future("a");
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
				return new Future("b");
			}
		};
	}
}