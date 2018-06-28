package jadex.micro.examples.mandelbrot;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

@ComponentTypes({
	@ComponentType(name="Generator", filename="jadex/micro/examples/mandelbrot/GenerateAgent.class"),
	@ComponentType(name="Calculator", filename="jadex/micro/examples/mandelbrot/CalculateAgent.class"),
	@ComponentType(name="Display", filename="jadex/micro/examples/mandelbrot/DisplayAgent.class")
})
@Configurations(
	@Configuration(name="default", components={
		@Component(type="Generator"),
		@Component(type="Calculator"),
		@Component(type="Display")
	})
)
@Agent
public class MandelbrotAgent
{
}
