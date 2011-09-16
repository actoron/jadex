package jadex.micro.tutorial;

import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Date;

/**
 *  Chat micro agent that uses the clock service. 
 */
@Description("This agent uses the clock service.")
@Agent
@RequiredServices(@RequiredService(name="clockservice", type=IClockService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class ChatC2Agent
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
		IFuture<IClockService>	cs	= agent.getServiceContainer().getRequiredService("clockservice");
		cs.addResultListener(new DefaultResultListener<IClockService>()
		{
			public void resultAvailable(IClockService cs)
			{
				System.out.println("Time for a chat, buddy: "+new Date(cs.getTime()));
			}
		});
	}
}