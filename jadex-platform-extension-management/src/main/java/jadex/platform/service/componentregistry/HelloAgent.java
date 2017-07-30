package jadex.platform.service.componentregistry;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

@Agent(autoprovide=true)
public class HelloAgent implements IHelloService
{
	public IFuture<String> sayHello(String name)
	{
		return new Future<String>("Hello "+name);
	}
}
