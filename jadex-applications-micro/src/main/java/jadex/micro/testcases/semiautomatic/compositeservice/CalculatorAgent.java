package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Calculator component that provides all services by itself.  
 */
@Description("This agent is a minimal calculator.")
@ProvidedServices({
	//@ProvidedService(type=IAddService.class, expression="new AddService($component)"),
	@ProvidedService(type=IAddService.class, expression="new PojoAddService()"),
	@ProvidedService(type=ISubService.class, expression="new SubService($component)")
})
public class CalculatorAgent extends MicroAgent
{
}
