package jadex.microservice.examples.helloworld;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;

/**
 *  This agent is used to set up the application consisting
 *  of one microservice and on agent agent that searches for the
 *  service and invokes it.
 */
@Description("This agent is used to set up the application consisting\r\n" + 
	" of one microservice and on agent agent that searches for the\r\n" + 
	" service and invokes it")
@Agent
@ComponentTypes(
{
	@ComponentType(clazz=HelloService.class, name="service"),
	@ComponentType(clazz=ServiceUser.class, name="user")
})
@Configurations(@Configuration(name="def", components= 
{
	@Component(type="service"),
	@Component(type="user")
}))
public class HelloWorld
{
}
