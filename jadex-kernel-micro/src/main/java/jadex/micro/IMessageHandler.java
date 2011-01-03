package jadex.micro;

import jadex.bridge.IInternalAccess;
import jadex.bridge.MessageType;
import jadex.commons.IFilter;

import java.util.Map;

/**
 *  Interface for message handlers.
 */
public interface IMessageHandler
{
	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter getFilter();
	
	/**
	 *  Test if handler should be removed.
	 *  @return True if it should be removed. 
	 */
	public boolean isRemove();
	
	/**
	 *  Handle the message.
	 *  @param ia The internal access.
	 *  @param msg The message.
	 *  @param type The message type.
	 */
	public void handleMessage(IInternalAccess ia, Map msg, MessageType type);

}
