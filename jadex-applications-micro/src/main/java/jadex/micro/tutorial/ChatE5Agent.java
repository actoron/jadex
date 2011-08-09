package jadex.micro.tutorial;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Map;

/**
 *  Chat micro agent with a . 
 */
@Description("This agent provides a basic chat service.")
@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class, 
	implementation=@Implementation(value=ChatServiceD5.class)))
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true,
		binding=@Binding(dynamic=true, scope=RequiredServiceInfo.SCOPE_GLOBAL)),
	@RequiredService(name="regservice", type=IRegistryServiceE3.class)
})
@Arguments({
	@Argument(name="nickname", clazz=String.class, defaultvalue="\"Willi\""),
	@Argument(name="partner", clazz=String.class)
})
public class ChatE5Agent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The nickname. */
	@AgentArgument
	protected String nickname;
	
	/** The partner's nickname. */
	@AgentArgument
	protected String partner;
	
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
				rs.register(agent.getComponentIdentifier(), nickname);
				
				agent.waitFor(10000, new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						rs.getChatters().addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								Map chatters = (Map)result;
								System.out.println("The current chatters: "+result);
								final IComponentIdentifier cid = (IComponentIdentifier)chatters.get(partner);
								if(cid==null)
								{
									System.out.println("Could not find chat partner named: "+partner);
								}
								else
								{
									agent.getServiceContainer().getService(IChatService.class, cid)
										.addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object result)
										{
											IChatService cs = (IChatService)result;
											cs.message(agent.getComponentIdentifier().toString(), "Private hello from: "+nickname);
										}
									});
								}
							}
						});
						return null;
					}
				});
			}
		});
	}
}