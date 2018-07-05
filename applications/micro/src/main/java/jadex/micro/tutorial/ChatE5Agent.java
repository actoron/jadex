package jadex.micro.tutorial;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
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

/**
 *  Chat micro agent with a . 
 */
@Description("This agent provides a basic chat service.")
@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class, 
	implementation=@Implementation(value=ChatServiceD5.class)))
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class, 
		binding=@Binding(scope=Binding.SCOPE_PLATFORM)),
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true,
		binding=@Binding(scope=Binding.SCOPE_GLOBAL)),
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
	protected IInternalAccess agent;
	
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
		IFuture<IRegistryServiceE3>	regservice	= agent.getFeature(IRequiredServicesFeature.class).getService("regservice");
		regservice.addResultListener(new DefaultResultListener<IRegistryServiceE3>()
		{
			public void resultAvailable(final IRegistryServiceE3 rs)
			{
				rs.register(agent.getIdentifier(), nickname);
				
				agent.getFeature(IExecutionFeature.class).waitForDelay(10000, new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						rs.getChatters().addResultListener(new DefaultResultListener<Map<String, IComponentIdentifier>>()
						{
							public void resultAvailable(Map<String, IComponentIdentifier> chatters)
							{
								System.out.println("The current chatters: "+chatters);
								final IComponentIdentifier cid = chatters.get(partner);
								if(cid==null)
								{
									System.out.println("Could not find chat partner named: "+partner);
								}
								else
								{
									agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IChatService.class, cid))
										.addResultListener(new DefaultResultListener<IChatService>()
									{
										public void resultAvailable(IChatService cs)
										{
											System.out.println("is on: "+IComponentIdentifier.LOCAL.get());
											cs.message(agent.getIdentifier().toString(), "Private hello from: "+nickname);
										}
									});
								}
							}
						});
						return IFuture.DONE;
					}
				});
			}
		});
	}
}