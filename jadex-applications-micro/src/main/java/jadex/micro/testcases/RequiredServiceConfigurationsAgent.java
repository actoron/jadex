package jadex.micro.testcases;

import jadex.bridge.service.BasicServiceContainer;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Test if binding of required service info can be overridden in configuration.
 */
@RequiredServices(@RequiredService(name="as", type=IAService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@Configurations({
	@Configuration(name="a"),
	@Configuration(name="b", requiredservices=@RequiredService(name="as", type=IAService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_LOCAL)))
})
public class RequiredServiceConfigurationsAgent extends MicroAgent
{
	/**
	 *  Agent created.
	 */
	public IFuture agentCreated()
	{
		BasicServiceContainer con = (BasicServiceContainer)getServiceContainer();
		RequiredServiceInfo rsi = con.getRequiredServiceInfo("as");
		System.out.println(rsi.getDefaultBinding().getScope());
		return super.agentCreated();
	}
}