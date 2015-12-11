package jadex.bridge;

import java.util.Map;

import jadex.bridge.service.types.message.MessageType;


/**
 *  Interface for external messages to be passed to a kernel component.
 */
//Todo: remove this struct?
public interface IMessageAdapter
{
	/**
     * The Class object representing the class corresponding to
     * the this interface. Need due to JavaFlow Bug:
     * http://issues.apache.org/jira/browse/SANDBOX-111
     */
	public static final Class<?> TYPE = IMessageAdapter.class;
	
	/**
	 *  Get the message type.
	 *  @return The message type. 
	 */
	public MessageType getMessageType();
	
	/**
	 *  Get the platform message.
	 *  @return The platform specific message.
	 */
	public Object getMessage();

	/**
	 *  Get the value for a parameter, or the values
	 *  for a parameter set.
	 *  Parameter set values can be provided as array, collection,
	 *  iterator or enumeration.
	 */
	public Object getValue(String name);

	/**
	 *  Get the parameters as map.
	 *  @return A map of parameters.
	 */
	public Map<String, Object> getParameterMap();
	
	/** 
	 *  Get the unique message id.
	 *  @return The id of this message.
	 * /
	public String getId();*/
}
