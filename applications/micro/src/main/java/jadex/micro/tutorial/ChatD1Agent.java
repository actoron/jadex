package jadex.micro.tutorial;

import java.util.Collection;
import java.util.Iterator;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent provides a basic chat service. 
 */
@Description("This agent provides a basic chat service.")
@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class, 
	implementation=@Implementation(ChatServiceD1.class)))
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class),
	@RequiredService(name="chatservices", type=IChatService.class, scope=ServiceScope.PLATFORM) // multiple=true,
})
public class ChatD1Agent
{
	/** The underlying micro agent. */
	@Agent
	protected IInternalAccess agent;

	/** The required services feature. */
	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	//@AgentBody
	@OnStart
	public void executeBody()
	{
		IFuture<Collection<IChatService>>	chatservices	= requiredServicesFeature.getServices("chatservices");
		chatservices.addResultListener(new DefaultResultListener<Collection<IChatService>>()
		{
			public void resultAvailable(Collection<IChatService> result)
			{
				for(Iterator<IChatService> it=result.iterator(); it.hasNext(); )
				{
					IChatService cs = it.next();
					cs.message(agent.getId().getName(), "Hello");
				}
			}
		});
	}
}