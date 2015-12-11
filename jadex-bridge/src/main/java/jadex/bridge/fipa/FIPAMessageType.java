package jadex.bridge.fipa;

import java.util.Date;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.message.MessageType;

/**
 *  The FIPA message type.
 */
public class FIPAMessageType extends MessageType
{
	//-------- attributes --------
	
	/** The parameters that are important for en/decoding. */
	protected static final String[] content_info = new String[]{SFipa.LANGUAGE, SFipa.ONTOLOGY};
	protected static final String[] empty = new String[0];
	
	static
	{
		MessageType.addMessageType(new FIPAMessageType());
	}
	
	//-------- constructors --------

	/**
	 *  Create a new fipa message type.
	 */
	public FIPAMessageType()
	{
		super(SFipa.MESSAGE_TYPE_NAME_FIPA, new MessageType.ParameterSpecification[]
		{
			// Std. parameters
			new MessageType.ParameterSpecification(SFipa.PERFORMATIVE, String.class, false),
			new MessageType.ParameterSpecification(SFipa.SENDER, IComponentIdentifier.class, false),
			new MessageType.ParameterSpecification(SFipa.REPLY_TO, IComponentIdentifier.class, false),
			new MessageType.ParameterSpecification(SFipa.CONTENT, Object.class, false),
			new MessageType.ParameterSpecification(SFipa.LANGUAGE, String.class, SFipa.LANGUAGE, false, false),
			new MessageType.ParameterSpecification(SFipa.ENCODING, String.class, SFipa.ENCODING, false, false),
			new MessageType.ParameterSpecification(SFipa.ONTOLOGY, String.class, SFipa.ONTOLOGY, false, false),
			new MessageType.ParameterSpecification(SFipa.PROTOCOL, String.class, SFipa.PROTOCOL, false, false),
			new MessageType.ParameterSpecification(SFipa.REPLY_WITH, String.class, false),
			new MessageType.ParameterSpecification(SFipa.IN_REPLY_TO, String.class, SFipa.REPLY_WITH, true, false),
			new MessageType.ParameterSpecification(SFipa.CONVERSATION_ID, String.class, SFipa.CONVERSATION_ID, true, false),
			new MessageType.ParameterSpecification(SFipa.REPLY_BY, Date.class, false),
			// Extra parameters
			new MessageType.ParameterSpecification(SFipa.X_MESSAGE_ID, String.class, false),
			new MessageType.ParameterSpecification(SFipa.X_TIMESTAMP, String.class, false),
			new MessageType.ParameterSpecification(SFipa.X_RID, IResourceIdentifier.class, SFipa.X_RID, false, false),
			new MessageType.ParameterSpecification(SFipa.X_RECEIVER, IComponentIdentifier.class, false),
			new MessageType.ParameterSpecification(SFipa.X_NONFUNCTIONAL, Map.class, SFipa.X_NONFUNCTIONAL, false, false)
		},

		// Second parameter represents the parameter sets.
		new MessageType.ParameterSpecification[]
		{
			new MessageType.ParameterSpecification(SFipa.RECEIVERS, IComponentIdentifier.class, SFipa.SENDER, false, true)
		});
	}

	//-------- methods --------

	/**
	 *  Get the identifier for fetching the receivers.
	 *  @return The receiver identifier.
	 */
	public String getReceiverIdentifier()
	{
		return SFipa.RECEIVERS;
	}

	/**
	 *  Get the identifier for fetching the sender.
	 *  @return The sender identifier.
	 */
	public String getSenderIdentifier()
	{
		return SFipa.SENDER;
	}
	
	/**
	 *  Get the identifier for fetching the message id.
	 *  Support for message identifiers is optional.
	 *  @return The id identifier.
	 */
	public String getIdIdentifier()
	{
		return SFipa.X_MESSAGE_ID;
	}
	
	/**
	 *  Get the identifier for fetching the send date.
	 *  Support for date is optional.
	 *  @return The send date identifier.
	 */
	public String getTimestampIdentifier()
	{
		return SFipa.X_TIMESTAMP;
	}
	
	/**
	 *  Get the identifier for fetching the resource identifier id.
	 *  @return The resource identifier id.
	 */
	public String getResourceIdIdentifier()
	{
		return SFipa.X_RID;
	}
	
	/**
	 *  Get the identifier for fetching the resource identifier id.
	 *  @return The resource identifier id.
	 */
	public String	getRealReceiverIdentifier()
	{
		return SFipa.X_RECEIVER;
	}
	
	/**
	 *  Get the identifier for fetching the non-functional properties.
	 *  @return The non-functional properties.
	 */
	public String getNonFunctionalPropertiesIdentifier()
	{
		return SFipa.X_NONFUNCTIONAL;
	}

	/**
	 *  Get the en/decode info (important) for a parameter/set.
	 *  @param The name of the parameter/set.
	 *  @return The en/decode infos.
	 */
	public String[] getCodecInfos(String name)
	{
//		return SFipa.CONTENT.equals(name)? content_info: empty;
		return SFipa.CONTENT.equals(name)? content_info: empty;
	}
	
	/**
	 *  Get a simplified human readable representation of the message content.
	 *  @param The message.
	 *  @return The simplified representation.
	 */
	public String	getSimplifiedRepresentation(Map<String, Object> msg)
	{
		StringBuffer	ret	= new StringBuffer();
		if(msg.containsKey(SFipa.PERFORMATIVE))
			ret.append(msg.get(SFipa.PERFORMATIVE));
		else
			ret.append("unknown");
		
		ret.append("(");
		if(msg.containsKey(SFipa.CONTENT))
			ret.append(msg.get(SFipa.CONTENT));
		ret.append(")");
		return ret.toString();
	}
}
