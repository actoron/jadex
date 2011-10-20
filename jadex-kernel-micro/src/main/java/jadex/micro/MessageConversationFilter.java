package jadex.micro;

import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.IFilter;

import java.util.Map;

/**
 *  Filter for message conversations.
 */
public class MessageConversationFilter implements IFilter
{
	//-------- attributes --------
	
	/** The message. */
	protected Map message;
	
	/** The message type. */
	protected MessageType messagetype;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message conversation filter.
	 */
	public MessageConversationFilter(Map message, MessageType messagetype)
	{
		this.message = message;
		this.messagetype = messagetype;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(Object obj)
	{
		boolean ret = false;
		IMessageAdapter reply = (IMessageAdapter)obj;
		if(messagetype.equals(reply.getMessageType()))
		{
			ParameterSpecification[] ps = messagetype.getConversationIdentifiers();
			for(int i=0; i<ps.length && !ret; i++)
			{
				String scid = (String)message.get(ps[i].getSource());
				if(scid!=null)
				{
					String rcid = (String)reply.getValue(ps[i].getName());
					ret = scid.equals(rcid);
				}
			}
		}
		return ret;
	}	
}
