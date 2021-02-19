package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Imports;

@Imports(
{
	"jadex.platform.service.servicepool.*"
})
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
			value="new PoolServiceInfo[]{new PoolServiceInfo().setWorkermodel(\"jadex/micro/examples/mandelbrot_new/CalculateAgent.class\").setServiceType(ICalculateService.class).setPoolStrategy(new jadex.commons.DefaultPoolStrategy(2, 2)).setPublicationScope(jadex.bridge.service.ServiceScope.GLOBAL)}")),
		@Component(type="Display")
	}),
	@Configuration(name="pools", components={
		@Component(type="Generator"),
		/*@Component(type="CalculatorPool", number = "3", arguments = @NameValue(name="serviceinfos",
			value="new PoolServiceInfo[]{new PoolServiceInfo().setWorkermodel(\"jadex/micro/examples/mandelbrot_new/CalculateAgent.class\").setServiceType(ICalculateService.class).setPoolStrategy(new jadex.commons.DefaultPoolStrategy(2, 2)).setPublicationScope(jadex.bridge.service.ServiceScope.GLOBAL)}")),
		*/@Component(type="Display")
	})
})
@Agent
public class MandelbrotAgent
{
}

