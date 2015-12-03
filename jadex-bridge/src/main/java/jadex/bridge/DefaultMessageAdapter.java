package jadex.bridge;

import java.util.Map;

import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SReflect;

/**
 *  The adapter for messages on the standalone platform.
 */
// Todo: remove this struct?
public class DefaultMessageAdapter implements IMessageAdapter
{
	//-------- attributes ---------
	
	/** The message envelope. */
	protected Map message;
	
	/** The message type. */
	protected MessageType type;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public DefaultMessageAdapter()
	{
	}
	
	/**
	 *  Create a new message adapter.
	 *  @param message The message.
	 */
	public DefaultMessageAdapter(Map message, MessageType type)
	{
		assert type != null;
		
		this.message = message;
		this.type = type;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the message type.
	 *  @return The message type. 
	 */
	public MessageType getMessageType()
	{
		return type;
	}
	
	/**
	 *  Set the message type.
	 *  @param type The message type. 
	 */
	public void	setMessageType(MessageType type)
	{
		this.type	= type;
	}
	
	/**
	 *  Get the platform message.
	 *  @return The platform specific message.
	 */
	public Object getMessage()
	{
		return message;
	}
	
	/**
	 *  Get all parameter names.
	 *  @return The parameter names.
	 * /
	public String[] getParameterNames()
	{
		return (String[])message.getMessage().keySet().toArray(new String[0]);
	}*/
	
	/**
	 *  Get the value for a parameter, or the values
	 *  for a parameter set.
	 *  Parameter set values can be provided as array, collection,
	 *  iterator or enumeration.
	 */
	public Object getValue(String name)
	{
		return message.get(name);
	}

	/** 
	 *  Get the unique message id.
	 *  @return The id of this message.
	 * /
	public String getId()
	{
		return (String)message.get(SFipa.X_MESSAGE_ID);
	}*/
	
	/**
	 *  Get the parameters as map.
	 *  @return A map of parameters.
	 */
	public Map getParameterMap()
	{
		return message;
	}
	
	/**
	 *  Set the parameters as map.
	 *  @param	parameters A map of parameters.
	 */
	public void	setParameterMap(Map parameters)
	{
		this.message	= parameters;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(SReflect.getInnerClassName(this.getClass())+"(");
//		sb.append("message type: "+getMessageType()+", ");
//		sb.append("id: "+getId()+", ");
		sb.append("message: "+getMessage());
		sb.append(")");
		return sb.toString();
	}
}
