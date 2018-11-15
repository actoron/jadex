package jadex.micro.testcases;


import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Base class agent.
 */
@Agent
@Description("Base description")
@Imports({"b1", "b2"})
@Properties({@NameValue(name="a", value="\"ba\""), @NameValue(name="b", value="\"bb\"")})
@RequiredServices(@RequiredService(name="clock", type=IClockService.class, scope=ServiceScope.PLATFORM))
@ProvidedServices(@ProvidedService(name="myservice", type=IAService.class, implementation=@Implementation(Object.class)))
@Arguments(@Argument(name="arg1", defaultvalue="\"bval\"", clazz=String.class))
@Results(@Result(name="res1", defaultvalue="\"bres\"", clazz=String.class))
@Configurations({@Configuration(name="config1"), @Configuration(name="config2")})
public abstract class BaseAgent extends JunitAgentTest
{
}
