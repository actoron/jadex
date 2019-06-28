package jadex.extension.websocket;


import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Properties;

/**
 *  Agent that creates a nano websocket endpoint.
 */
@Agent(autostart=Boolean3.TRUE)
@Properties(@NameValue(name="system", value="true"))
public class NanoWebsocketAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentBody
	public void body()
	{
		System.out.println("nano websocket agent started");
		WebsocketServer wss = new WebsocketServer(8082, agent);
	}
}
