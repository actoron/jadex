package jadex.micro.tutorial;

import java.util.Date;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Description("This agent uses the clock service.")
@Agent
@RequiredServices(@RequiredService(name = "clockservice", type = IClockService.class, binding = @Binding(scope = Binding.SCOPE_PLATFORM)))
public class ChatC1Agent
{
	@Agent
	protected IInternalAccess agent;
	
	// Lazy false means that agent init waits for service search being done
	@AgentService(lazy=false)
	protected IClockService clockservice;
	
	@AgentBody
	public void executeBody()
	{
		System.out.println("Time for a chat, buddy: "+ new Date(clockservice.getTime()));
	}
}

//@Description("This agent uses the clock service.")
//@Agent
//@RequiredServices(@RequiredService(name = "clockservice", type = IClockService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
//public class ChatC1Agent
//{
//	@Agent
//	protected MicroAgent	agent;
//	
//	@AgentBody
//	public void executeBody()
//	{
//		agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("clockservice")
//			.addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				IClockService cs = (IClockService)result;
//				System.out.println("Time for a chat buddy: "
//						+ new Date(cs.getTime()));
//			}
//		});
//	}
//}