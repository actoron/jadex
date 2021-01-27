package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

@ComponentTypes({
	@ComponentType(name="Generator", clazz=GenerateAgent.class),
	@ComponentType(name="Display", clazz=DisplayAgent.class),
	@ComponentType(name="CalculatorPool", filename = "jadex/platform/service/servicepool/ServicePoolAgent.class")	// avoid compile time dependency to platform
})
@Configurations({
	
	/*@Configuration(name="default", components={
		@Component(type="Generator"),
		@Component(type="Display")
	})*/
	@Configuration(name="pool", components={
		@Component(type="Generator"),
		@Component(type="CalculatorPool", arguments = @NameValue(name="serviceinfos",
			value="new jadex.platform.service.servicepool.PoolServiceInfo[]{new jadex.platform.service.servicepool.PoolServiceInfo(\"jadex/micro/examples/mandelbrot_new/CalculateAgent.class\", jadex.micro.examples.mandelbrot_new.ICalculateService.class, new jadex.commons.DefaultPoolStrategy(10, 10000, 20))}")),
		@Component(type="Display")
	})
})
@Agent
public class MandelbrotAgent
{
}

