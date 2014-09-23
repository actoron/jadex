package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.IFilter;

/**
 *  Filter for message conversations.
 */
public class MessageConversationFilter implements IFilter<IMessageAdapter>
{
	//-------- attributes --------
	
	/** The initial conversation message. */
	protected IMessageAdapter adapter;
	
	/** The message. */
	protected Map<String, Object> message;
	
	/** The message type. */
	protected MessageType messagetype;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message conversation filter.
	 */
	public MessageConversationFilter(IMessageAdapter adapter)
	{
		this.adapter = adapter;
	}
	
	/**
	 *  Create a new message conversation filter.
	 */
	public MessageConversationFilter(Map<String, Object> message, MessageType messagetype)
	{
		this.message = message;
		this.messagetype = messagetype;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(IMessageAdapter reply)
	{
		boolean ret = false;
		
		MessageType mt = adapter!=null? adapter.getMessageType(): messagetype;
		Map<String, Object> map = adapter!=null? adapter.getParameterMap(): message;
		
		if(mt.equals(reply.getMessageType()))
		{
			ParameterSpecification[] ps = mt.getConversationIdentifiers();
			for(int i=0; i<ps.length && !ret; i++)
			{
				String scid = (String)map.get(ps[i].getSource());
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
