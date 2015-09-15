package jadex.platform.service.message.transport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.annotations.Alias;

/**
 *  The message envelope holding the native message,
 *  the receivers and the message type.
 */
@Alias("jadex.base.service.message.transport.MessageEnvelope")
public class MessageEnvelope
{
	//-------- attributes --------

	/** The message. */
	protected Map<String, Object> message;
	
	/** The receivers. */
	protected Collection<ITransportComponentIdentifier> receivers;
	
	/** The message type. */
	protected String message_type;
	
	//-------- constructors --------

	/**
	 *  Create a new message envelope.
	 *  (bean constructor)
	 */
	public MessageEnvelope()
	{
	}
	
	/**
	 *  Create a new message envelope.
	 */
	public MessageEnvelope(Map<String, Object> message, Collection<ITransportComponentIdentifier> receivers, String message_type)
	{
		this.message = message;
		this.receivers = receivers;
		this.message_type = message_type;
	}

	//-------- methods --------

	/**
	 *  Get native message.
	 *  @return The native message.
	 */
	public Map<String, Object> getMessage()
	{
		return message;
	}
	
	/**
	 *  Set native message.
	 *  @param message The native message.
	 */
	public void setMessage(Map<String, Object> message)
	{
		this.message = message;
	}
	
	/**
	 * Get the receivers.
	 */
	// Legacy compatibility hack. Should be ITransportComponentIdentifier
	public IComponentIdentifier[] getReceivers()
	{
		return receivers==null? new ComponentIdentifier[0]: receivers.toArray(new ComponentIdentifier[receivers.size()]);
	}
	
	/**
	 * Get the receivers.
	 */
	// Legacy compatibility hack. Should be ITransportComponentIdentifier
	public void setReceivers(IComponentIdentifier[] receivers)
	{
		this.receivers = new ArrayList<ITransportComponentIdentifier>();
		if(receivers!=null)
		{
			for(int i=0; i<receivers.length; i++)
			{
				this.receivers.add((ITransportComponentIdentifier)receivers[i]);
			}
		}
	}
	
	/**
	 *  Add a receiver.
	 */
	public void addReceiver(ITransportComponentIdentifier receiver)
	{
		if(receivers==null)
			receivers = new ArrayList<ITransportComponentIdentifier>();
		receivers.add(receiver);
	}

	/**
	 *  Set the type (e.g. "fipa").
	 * @param messagetypename 
	 */
	public void setTypeName(String messagetypename)
	{
		message_type = messagetypename;
	}

	/**
	 *  Get the type (e.g. "fipa").
	 */
	public String getTypeName()
	{
		return message_type;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(SReflect.getInnerClassName(this.getClass())+"(");
		//sb.append("sender: "+getSender()+", ");
		sb.append("receivers: "+SUtil.arrayToString(getReceivers())+", ");
		sb.append("message type: "+message_type);
		sb.append("raw values: "+message);
//		sb.append(super.toString());
		sb.append(")");
		return sb.toString();
	}
}
