package jadex.micro;

import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.IFilter;

/**
 *  Filter for message conversations.
 */
public class MessageConversationFilter implements IFilter<IMessageAdapter>
{
	//-------- attributes --------
	
	/** The initial conversation message. */
	protected IMessageAdapter message;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message conversation filter.
	 */
	public MessageConversationFilter(IMessageAdapter message)
	{
		this.message = message;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(IMessageAdapter reply)
	{
		boolean ret = false;
		if(message.getMessageType().equals(reply.getMessageType()))
		{
			ParameterSpecification[] ps = message.getMessageType().getConversationIdentifiers();
			for(int i=0; i<ps.length && !ret; i++)
			{
				String scid = (String)message.getParameterMap().get(ps[i].getSource());
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
