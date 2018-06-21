package jadex.bdi.examples.ping;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;


/**
 *  The echo plan reacts on every message
 *  with the same message.
 */
public class EchoPlan extends Plan
{
	//-------- methods --------

	/**
	 *  Handle the ping request.
	 */
	public void body()
	{
		// Get the initial event.
		IMessageEvent me = (IMessageEvent)getReason();
	
//		System.out.println("Echo plan invoked: "+me.getParameter(SFipa.CONTENT).getValue());
		
		// Create the reply.
		IMessageEvent re = getEventbase().createReply(me, "any_message");
		re.getParameter(SFipa.PERFORMATIVE).setValue(me.getParameter(
			SFipa.PERFORMATIVE).getValue());
		re.getParameter(SFipa.CONTENT).setValue(me.getParameter(
				SFipa.CONTENT).getValue());
	
		// Send back the reply and terminate.
		sendMessage(re);
	}
}

