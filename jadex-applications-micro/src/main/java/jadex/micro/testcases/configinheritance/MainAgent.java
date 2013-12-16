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
	@ComponentType(name="empty1", clazz=EmptyAgent.class),
	@ComponentType(name="empty2", clazz=Empty2Agent.class)
})
@Configurations(
{
	@Configuration(name="main", components=
	{
		@Component(type="empty1", number="1"),
		@Component(type="empty2", number="2")
	})
})
public class MainAgent
{
}
