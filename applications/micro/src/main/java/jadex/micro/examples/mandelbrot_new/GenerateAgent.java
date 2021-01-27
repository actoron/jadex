package jadex.micro.examples.mandelbrot_new;

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
	protected ICalculateService calcservice;
	
	@OnService(name="calculateservice")
	protected void setCalculateService(ICalculateService cs)
	{
		System.out.println("Found ICalculate service: "+cs);
		this.calcservice = cs;
	}

	/**
	 * @return the calcservice
	 */
	public ICalculateService getCalculateService() 
	{
		return calcservice;
	}
}
