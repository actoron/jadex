package jadex.micro.tutorial;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
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
		binding=@Binding(dynamic=true, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="regservice", type=IRegistryServiceE3.class)
})
@Arguments(@Argument(name="nickname", clazz=String.class, defaultvalue="\"Willi\""))
public class ChatE3Agent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The nickname. */
	@AgentArgument
	protected String nickname;
	
	/** The injected service. */
//	@AgentService
	protected IRegistryServiceE3 regservice;
	
	@AgentCreated
	public IFuture<Void> init()
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IRegistryServiceE3>	fut	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("regservice");
		fut.addResultListener(new ExceptionDelegationResultListener<IRegistryServiceE3, Void>(ret)
		{
			public void customResultAvailable(final IRegistryServiceE3 rs)
			{
				regservice = rs;
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
//		IFuture<IRegistryServiceE3>	regservice	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("regservice");
//		regservice.addResultListener(new DefaultResultListener<IRegistryServiceE3>()
//		{
//			public void resultAvailable(final IRegistryServiceE3 rs)
//			{
				regservice.register(agent.getComponentIdentifier(), nickname);
				
				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(10000, new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						regservice.getChatters().addResultListener(new DefaultResultListener<Map<String, IComponentIdentifier>>()
						{
							public void resultAvailable(Map<String, IComponentIdentifier> result)
							{
								System.out.println("The current chatters: "+result);
							}
						});
						return IFuture.DONE;
					}
				});
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				super.exceptionOccurred(exception);
//			}
//		});
	}
}