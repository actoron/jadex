package jadex.web.examples.mandelbrot;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.mandelbrot_new.DisplayService;
import jadex.micro.examples.mandelbrot_new.IDisplayService;
import jadex.micro.examples.mandelbrot_new.IGenerateService;

/**
 *  Agent offering a display service.
 */
@Description("Agent offering a web display service.")
@ProvidedServices({
	@ProvidedService(type=IDisplayService.class, implementation=@Implementation(DisplayService.class))
})
@RequiredServices({
	@RequiredService(name="generateservice", type=IGenerateService.class),
})
@Agent
public class DisplayAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
		
	@AgentArgument
	protected int port;
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Wait for the IWebPublishService and then publish the resources.
	 *  @param pubser The publish service.
	 */
	//@AgentServiceQuery
	@OnService(requiredservice = @RequiredService(min = 1, max = 1))
	protected void publish(IWebPublishService wps)
	{
		if(port==0)
			port = 8080;
		
		IServiceIdentifier sid = ((IService)agent.getProvidedService(IDisplayService.class)).getServiceId();
		
		wps.publishService(sid, new PublishInfo("[http://localhost:"+port+"/]mandelbrotdisplay", IPublishService.PUBLISH_RS, null)).get();
		
		System.out.println("display publish started: "+wps);
		wps.publishResources("[http://localhost:"+port+"/mandelbrot]", "META-INF/resources/mandelbrot").get();
	}
}
