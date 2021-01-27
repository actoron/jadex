package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

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
public class GenerateAgent
{
	@Agent
	protected IInternalAccess agent;
	
	protected ICalculateService calcservice;
	protected IDisplayService displayservice;
	
	@OnService(name="calculateservice")
	protected void calculateServiceAvailable(ICalculateService cs)
	{
		//System.out.println("Found calculate service: "+cs);
		this.calcservice = cs;
		if(displayservice!=null)
			agent.getLocalService(IGenerateService.class).calcDefaultImage();
	}
	
	@OnService(name="displayservice")
	protected void displayServiceAvailable(IDisplayService ds)
	{
		//System.out.println("Found display service: "+cs);
		this.displayservice = ds;
		if(calcservice!=null)
			agent.getLocalService(IGenerateService.class).calcDefaultImage();
	}

	/**
	 * @return the calcservice
	 */
	public ICalculateService getCalculateService() 
	{
		return calcservice;
	}
}
