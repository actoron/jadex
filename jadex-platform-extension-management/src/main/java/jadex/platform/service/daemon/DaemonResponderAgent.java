package jadex.platform.service.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.util.HashMap;
import java.util.Map;

/**
 *  Simple agent that sends a message on startup.
 *  Used by daemon to receive a message from a started platform.
 */
@Arguments({
	@Argument(name="cid", clazz=IComponentIdentifier.class, description="The identifier of the receiver."),
	@Argument(name="content", clazz=String.class, description="The message content.")
})
@Agent
public class DaemonResponderAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent	agent;
	
	/** The message receiver. */
	@AgentArgument
	protected IComponentIdentifier	cid;

	/** The message content. */
	@AgentArgument
	protected String	content;

	//-------- methods --------
	
	/**
	 *  Agent behavior.
	 */
	@AgentBody
	public IFuture<Void> start()
	{
		agent.getLogger().info("Sending message "+content+" to "+cid+", "+SUtil.arrayToString(cid.getAddresses()));
		
		Map<String, Object>	msg	= new HashMap<String, Object>();
		msg.put(SFipa.RECEIVERS, cid);
		msg.put(SFipa.CONTENT, content);
		return agent.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE);
	}
}
