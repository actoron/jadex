package jadex.web.examples.hellobdiv3;

import jadex.bdiv3.IBDIAgent;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  Say hello agent.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public abstract class SayHelloBDI implements ISayHelloService, IBDIAgent
{
	/**
	 *  Say hello method.
	 *  @return Say hello text.
	 */
	public IFuture<String> sayHello()
	{
		String text = Math.random()>0.5? "Hello User, I am ": "Hallo Nutzer, ich bin ";
		text += getIdentifier();
		return new Future<String>(text);
	}
}
