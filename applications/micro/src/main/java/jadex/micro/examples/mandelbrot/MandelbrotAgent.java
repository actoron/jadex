package jadex.micro.examples.mandelbrot;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.examples.lottery.HumanPlayerAgent;
import jadex.micro.examples.lottery.LotteryAgent;
import jadex.micro.examples.lottery.PlayerAgent;

@ComponentTypes({
	@ComponentType(name="Generator", clazz=GenerateAgent.class),
	@ComponentType(name="Display", clazz=DisplayAgent.class),
	//@ComponentType(name="CalculatorPool", clazz=ServicePoolAgent.class)
})
@Configurations({
	
	@Configuration(name="default", components={
		@Component(type="Generator"),
		@Component(type="Display")
	})
	// todo: use generic service pool. currently an internal impl is used
	/*@Configuration(name="pool", components={
		@Component(type="Generator"),
		@Component(type="CalculatorPool", arguments = @NameValue(name="serviceinfos",
			value="new jadex.platform.service.servicepool.PoolServiceInfo[]{new jadex.platform.service.servicepool.PoolServiceInfo(\"jadex/micro/examples/mandelbrot/CalculateAgent.class\", jadex.micro.examples.mandelbrot.ICalculateService.class, new jadex.commons.DefaultPoolStrategy(10, 10000, 20))}")),
		@Component(type="Display")
	})*/
})
@Agent
public class MandelbrotAgent
{
}

