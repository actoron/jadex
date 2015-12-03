package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;

/**
 *  Interface for message handlers.
 */
public interface IMessageHandler
{
	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter<IMessageAdapter> getFilter();
	
	/**
	 *  Get the timeout.
	 *  @return The timeout.
	 */
	public long getTimeout();
	
	/**
	 *  Test if handler should be removed.
	 *  @return True if it should be removed. 
	 */
	public boolean isRemove();
	
	/**
	 *  Test if handler should be real time.
	 *  @return True if it should be real time. 
	 */
	public boolean isRealtime();
	
	/**
	 *  Handle the message.
	 *  @param ia The internal access.
	 *  @param msg The message.
	 *  @param type The message type.
	 */
	public void handleMessage(Map<String, Object> msg, MessageType type);
	
	/**
	 *  Timeout occurred.
	 */
	public void timeoutOccurred();

}
