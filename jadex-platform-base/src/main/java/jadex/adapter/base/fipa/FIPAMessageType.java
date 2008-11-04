package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;
import jadex.bridge.MessageType;

import java.util.Date;

/**
 *  The FIPA message type.
 */
public class FIPAMessageType extends MessageType
{
	//-------- attributes --------
	
	/** The parameters that are important for en/decoding. */
	protected static final String[] content_info = new String[]{SFipa.LANGUAGE, SFipa.ONTOLOGY};
	protected static final String[] empty = new String[0];
	
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
			new MessageType.ParameterSpecification(SFipa.SENDER, IAgentIdentifier.class, false),
			new MessageType.ParameterSpecification(SFipa.REPLY_TO, IAgentIdentifier.class, false),
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
		},

		// Second parameter represents the parameter sets.
		new MessageType.ParameterSpecification[]
		{
			new MessageType.ParameterSpecification(SFipa.RECEIVERS, IAgentIdentifier.class, SFipa.SENDER, false, true)
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
	 *  Get the en/decode info (important) for a parameter/set.
	 *  @param The name of the parameter/set.
	 *  @return The en/decode infos.
	 */
	public String[] getCodecInfos(String name)
	{
//		return SFipa.CONTENT.equals(name)? content_info: empty;
		return SFipa.CONTENT.equals(name)? content_info: empty;
	}

}
