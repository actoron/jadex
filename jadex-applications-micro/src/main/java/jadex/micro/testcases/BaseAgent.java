package jadex.micro.testcases;

import jadex.bridge.service.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Base class agent.
 */
@Agent
@Description("Base description")
@Imports({"1", "2"})
@Properties({@NameValue(name="a", value="a"), @NameValue(name="b", value="b")})
@RequiredServices(@RequiredService(name="clock", type=IClockService.class))
@ProvidedServices(@ProvidedService(type=IClockService.class, implementation=@Implementation(expression="$component")))
@Arguments(@Argument(name="arg1", defaultvalue="val1", clazz=String.class))
public class BaseAgent
{

}
