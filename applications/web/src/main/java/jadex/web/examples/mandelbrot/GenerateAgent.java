package jadex.web.examples.mandelbrot;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
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
import jadex.micro.examples.mandelbrot_new.GenerateService;
import jadex.micro.examples.mandelbrot_new.ICalculateService;
import jadex.micro.examples.mandelbrot_new.IDisplayService;
import jadex.micro.examples.mandelbrot_new.IGenerateService;

/**
 *  Agent that can process generate requests.
 */
@Description("Agent offering a generate service.")
@ProvidedServices(@ProvidedService(type=IGenerateService.class, implementation=@Implementation(GenerateService.class)))
@RequiredServices({
	@RequiredService(name="displayservice", type=IDisplayService.class),
	@RequiredService(name="calculateservice", type=ICalculateService.class, scope=ServiceScope.GLOBAL), 
	@RequiredService(name="generateservice", type=IGenerateService.class)
})
@Agent
public class GenerateAgent // todo: implements IGenerateGui 
{
	@Agent
	protected IInternalAccess agent;
	
	//protected List<ICalculateService> calcservices = new ArrayList<>();
	protected IDisplayService displayservice;
	
	@AgentArgument
	protected int port;
	
	/*@OnService(name="calculateservice")
	protected void calculateServiceAvailable(ICalculateService cs)
	{
		System.out.println("Found calculate service: "+cs);
		calcservices.add(cs);
		if(displayservice!=null)
			agent.getLocalService(IGenerateService.class).calcDefaultImage();
	}*/
	
	@OnService(name="displayservice")
	protected void displayServiceAvailable(IDisplayService ds)
	{
		//System.out.println("Found display service: "+cs);
		this.displayservice = ds;
		//if(calcservices.size()>0)
			agent.getLocalService(IGenerateService.class).generateArea(null);
	}
	

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
		
		IServiceIdentifier sid = ((IService)agent.getProvidedService(IGenerateService.class)).getServiceId();
		
		wps.publishService(sid, new PublishInfo("[http://localhost:"+port+"/]mandelbrotgenerate", IPublishService.PUBLISH_RS, null)).get();
		
		System.out.println("generate publish started: "+wps);
	}

	/**
	 * @return the calcservice
	 * /
	public List<ICalculateService> getCalculateServices() 
	{
		return calcservices;
	}*/
}
