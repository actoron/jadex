package jadex.micro.testcases.authenticate;

import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 *  Agent implementing the test service without extra settings.
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class BasicProviderAgent implements ITestService
{
	/**
	 *  Test unrestricted access.
	 */
	@Security(roles=Security.UNRESTRICTED)
	public IFuture<Void> unrestrictedMethod()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Test default access.
	 */
	@Security
	public IFuture<Void> defaultMethod()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Test custom access.
	 */
	@Security(roles="custom")
	public IFuture<Void> customMethod()
	{
		return IFuture.DONE;
	}

	/**
	 *  Test custom access with multiple roles.
	 */
	@Security(roles={"custom1", "custom"})
	public IFuture<Void> custom1Method()
	{
		return IFuture.DONE;
	}
}
