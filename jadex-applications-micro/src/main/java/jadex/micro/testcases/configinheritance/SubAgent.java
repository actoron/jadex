package jadex.micro.testcases.configinheritance;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

/**
 *  Simple test agent with one service.
 */
@Agent
@Configurations(
{
	@Configuration(name="main", components=
	{
		@Component(type="empty1", number="2"),
		@Component(type="empty2", number="2")
	}, replace=false)
})
public class SubAgent extends MainAgent
{
}
