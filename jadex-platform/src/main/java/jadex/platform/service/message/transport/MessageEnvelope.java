package jadex.platform.service.message.transport;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.annotations.Alias;

/**
 *  The message envelope holding the native message,
 *  the receivers and the message type.
 */
public class MessageEnvelope
{
	//-------- attributes --------
	
	/** Optional message type defining the structure, e.g. "fipa". */
	protected String messagetype;
	
	/** The receivers. */
	protected ITransportComponentIdentifier[] receivers;
	
	/** The receivers. */
	protected IComponentIdentifier servicerec;
	
	/** The rid for decoding if specified. */
	protected IResourceIdentifier rid;
	
	/** Extension properties. */
	protected Map<String, Object> properties;
	
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
	public MessageEnvelope(ITransportComponentIdentifier[] receivers, IComponentIdentifier servicerec, IResourceIdentifier rid, String messagetype, byte messagetypeid)
	{
		this.receivers = receivers;
		this.messagetype = messagetype;
		this.servicerec = servicerec;
		this.rid = rid;
	}
	
	/**
	 * Get the receivers.
	 */
	// Legacy compatibility hack. Should be ITransportComponentIdentifier
	public ITransportComponentIdentifier[] getReceivers()
	{
		return receivers==null? new ITransportComponentIdentifier[0]: receivers;
	}
	
	/**
	 * Get the receivers.
	 */
	// Legacy compatibility hack. Should be ITransportComponentIdentifier
	public void setReceivers(ITransportComponentIdentifier[] receivers)
	{
		this.receivers = receivers;
	}
	
	/**
	 *  Add a receiver.
	 */
	public void addReceiver(ITransportComponentIdentifier receiver)
	{
		if(receivers==null)
			receivers = new ITransportComponentIdentifier[1];
		else
		{
			ITransportComponentIdentifier[] tmp = new ITransportComponentIdentifier[receivers.length + 1];
			System.arraycopy(receivers, 0, tmp, 0, receivers.length);
			receivers = tmp;
		}
		receivers[receivers.length - 1] = receiver;
	}
	
	/**
	 * @return the rid
	 */
	public IResourceIdentifier getRid()
	{
		return rid;
	}

	/**
	 *  Sets the rid.
	 *  @param rid The rid to set
	 */
	public void setRid(IResourceIdentifier rid)
	{
		this.rid = rid;
	}

	/**
	 * @return the servicerec
	 */
	public IComponentIdentifier getServiceRec()
	{
		return servicerec;
	}

	/**
	 *  Sets the servicerec.
	 *  @param servicerec The servicerec to set
	 */
	public void setServiceRec(IComponentIdentifier servicerec)
	{
		this.servicerec = servicerec;
	}
	
	

	/**
	 *  Set the type (e.g. "fipa").
	 * @param messagetypename 
	 */
	public void setTypeName(String messagetypename)
	{
		messagetype = messagetypename;
	}

	/**
	 *  Get the type (e.g. "fipa").
	 */
	public String getTypeName()
	{
		return messagetype;
	}
	
	/**
	 *  Adds a property to the envelope.
	 * @param name Property name.
	 * @param value Property value.
	 */
	public void addProperty(String name, Object value)
	{
		if (properties == null)
			properties = new HashMap<String, Object>();
		properties.put(name, value);
	}
	
	/**
	 *  Removes a property from the envelope.
	 *  @param name Property name.
	 *  @return The property value if found, null otherwise.
	 */
	public Object removeProperty(String name)
	{
		if (properties != null)
			return properties.remove(name);
		return null;
	}
	
	/**
	 *  Gets a property from the envelope.
	 *  @param name Property name.
	 *  @return The property value if found, null otherwise.
	 */
	public Object getProperty(String name)
	{
		if (properties != null)
			return properties.get(name);
		return null;
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
		sb.append("message type: "+messagetype);
//		sb.append("raw values: "+message);
//		sb.append(super.toString());
		sb.append(")");
		return sb.toString();
	}
}
