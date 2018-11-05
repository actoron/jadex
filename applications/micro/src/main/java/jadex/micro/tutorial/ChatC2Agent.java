package jadex.micro.tutorial;

import java.util.Date;

import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  Chat micro agent that uses the clock service. 
 */
@Description("This agent uses the clock service.")
@Agent
@RequiredServices(@RequiredService(name="clockservice", type=IClockService.class, scope=ServiceScope.PLATFORM))
public class ChatC2Agent
{
	/** The required services feature. */
	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
		IFuture<IClockService> fut = requiredServicesFeature.getService("clockservice");
		fut.addResultListener(new DefaultResultListener<IClockService>()
		{
			public void resultAvailable(IClockService cs)
			{
				System.out.println("Time for a chat, buddy: "+new Date(cs.getTime()));
			}
		});
	}
}