package jadex.micro.tutorial;

import java.util.Collection;
import java.util.Iterator;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent with a . 
 */
@Description("This agent provides a basic chat service.")
@Agent
@ProvidedServices(@ProvidedService(type=IExtendedChatService.class, 
	implementation=@Implementation(value=ChatServiceD5.class)))
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="chatservices", type=IExtendedChatService.class, multiple=true,
		binding=@Binding(dynamic=true, scope=RequiredServiceInfo.SCOPE_GLOBAL)),
	@RequiredService(name="regservice", type=IRegistryServiceE3.class)
})
public class ChatE3Agent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
		agent.getServiceContainer().getRequiredService("regservice")
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IRegistryServiceE3 rs = (IRegistryServiceE3)result;
				rs.register(agent.getComponentIdentifier(), "my_nick");
				
				agent.waitFor(10000, new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						rs.getChatters().addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								System.out.println("The current chatters: "+result);
							}
						});
						return null;
					}
				});
			}
		});
	}
}