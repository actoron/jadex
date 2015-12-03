package jadex.micro.examples.ping;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.message.MessageType;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Sends back the same message it received. 
 */
@Agent
@ProvidedServices(@ProvidedService(type=IEchoService.class,
	implementation=@Implementation(expression="$pojoagent")))
@Service
public class EchoAgent implements IEchoService
{
	/** The micro agent class. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Send same message back to the sender.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
		Map<String, Object> reply = new HashMap<String, Object>(msg);
		IComponentIdentifier sender = (IComponentIdentifier)msg.get(SFipa.SENDER);
		reply.put(SFipa.SENDER, agent.getComponentIdentifier());
		reply.put(SFipa.RECEIVERS, new IComponentIdentifier[]{sender});
		
		agent.getComponentFeature(IMessageFeature.class).sendMessage(reply, mt);
	}
}
