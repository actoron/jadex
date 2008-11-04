package jadex.tools.common.plugin;

import jadex.bdi.runtime.IMessageEvent;



/**
 *  An interface allowing to listen for received messages. 
 */
public interface IMessageListener
{
	/**
	 *  A message was received.
	 *  @return True, when the message was processed and should not
	 *  	be delivered to other message listeners.
	 */
	public boolean processMessage(IMessageEvent msg);
}
