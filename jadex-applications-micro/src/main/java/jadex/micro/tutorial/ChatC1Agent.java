package jadex.micro.tutorial;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent that declares the required clock service. 
 */
@Description("This agent declares a required clock service.")
@Agent
@RequiredServices(@RequiredService(name="clockservice", type=IClockService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class ChatC1Agent
{
}