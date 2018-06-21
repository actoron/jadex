package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Calculator component that provides all services by itself.  
 */
@Description("This agent is a minimal calculator.")
@ProvidedServices({
	//@ProvidedService(type=IAddService.class, expression="new AddService($component)"),
	@ProvidedService(type=IAddService.class, implementation=@Implementation(PojoAddService.class)),
	@ProvidedService(type=ISubService.class, implementation=@Implementation(expression="new SubService($component)"))
})
@Agent
public class CalculatorAgent
{
}
