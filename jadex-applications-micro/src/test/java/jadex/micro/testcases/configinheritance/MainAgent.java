package jadex.micro.testcases.configinheritance;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

/**
 *  Simple test agent with one service.
 */
@Agent
@ComponentTypes(
{	
	@ComponentType(name="emptya", clazz=EmptyAAgent.class),
	@ComponentType(name="emptyb", clazz=EmptyBAgent.class)
})
@Configurations(
{
	@Configuration(name="main", components=
	{
		@Component(type="emptya", number="1"),
		@Component(type="emptyb", number="1")
	}),
	@Configuration(name="same", components=
	{
		@Component(type="emptya", number="1"),
		@Component(type="emptyb", number="1")
	})
})
public class MainAgent
{
}
