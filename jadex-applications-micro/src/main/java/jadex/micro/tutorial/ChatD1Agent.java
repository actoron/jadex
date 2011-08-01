package jadex.micro.tutorial;

import java.util.Collection;
import java.util.Iterator;

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
 *  Chat micro agent provides a basic chat service. 
 */
@Description("This agent provides a basic chat service.")
@Agent
@ProvidedServices(@ProvidedService(type=IChatService.class, 
	implementation=@Implementation(ChatServiceD1.class)))
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true,
		binding=@Binding(dynamic=true, scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class ChatD1Agent
{
	/** The underlying mirco agent. */
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
		agent.getServiceContainer().getRequiredServices("chatservices")
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
				{
					IChatService cs = (IChatService)it.next();
					cs.message(agent.getComponentIdentifier().getName(), "Hello");
				}
			}
		});
	}
}