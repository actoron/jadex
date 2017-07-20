package jadex.bridge.fipa;

import jadex.bridge.IComponentIdentifier;

/**
 *  FIPA message as struct.
 */
public class FipaMessage
{
	// cf. http://www.fipa.org/specs/fipa00061/SC00061G.html
	
	//-------- constants --------
	
	/**
	 *  FIPA performatives. Not using enum as user may add custom performatives.
	 */
	public static abstract class Performative
	{
		public static final String ACCEPT_PROPOSAL = "accept-proposal";
		public static final String AGREE = "agree";
		public static final String CANCEL = "cancel";
		public static final String CFP = "cfp";
		public static final String CONFIRM = "confirm";
		public static final String DISCONFIRM = "disconfirm";
		public static final String FAILURE = "failure";
		public static final String INFORM = "inform";
		public static final String INFORM_IF = "inform-if";
		public static final String INFORM_REF = "inform-ref";
		public static final String NOT_UNDERSTOOD = "not-understood";
		public static final String PROPOSE = "propose";
		public static final String QUERY_IF = "query-if";
		public static final String QUERY_REF = "query-ref";
		public static final String REFUSE = "refuse";
		public static final String REJECT_PROPOSAL = "reject-proposal";
		public static final String REQUEST = "request";
		public static final String REQUEST_WHEN = "request-when";
		public static final String REQUEST_WHENEVER = "request-whenever";
		public static final String SUBSCRIBE = "subscribe";
		public static final String PROXY = "proxy";
		public static final String PROPAGATE = "propagate";
		public static final String UNKNOWN = "unknown";
	}
	
	//-------- Type of Communicative Act --------

	/** Denotes the type of the communicative act of the ACL message. */
	private String	performative;
	/** Denotes the type of the communicative act of the ACL message. */
	public String	getPerformative()
	{
		return performative;
	}
	/** Denotes the type of the communicative act of the ACL message. */
	public void	setPerformative(String performative)
	{
		this.performative	= performative;
	}

	//-------- Participants in Communication -------- 

	/** Denotes the identity of the sender of the message,
	 *  that is, the name of the agent of the communicative act. */
	private IComponentIdentifier	sender;
	/** Denotes the identity of the sender of the message,
	 *  that is, the name of the agent of the communicative act. */
	public IComponentIdentifier	getSender()
	{
		return sender;
	}
	/** Denotes the identity of the sender of the message,
	 *  that is, the name of the agent of the communicative act. */
	public void	setSender(IComponentIdentifier sender)
	{
		this.sender	= sender;
	}

	/** Denotes the identity of the intended recipients of the message. */
	// TODO: multiple receivers
	private IComponentIdentifier	receiver;
	/** Denotes the identity of the intended recipients of the message. */
	public IComponentIdentifier	getReceiver()
	{
		return receiver;
	}
	/** Denotes the identity of the intended recipients of the message. */
	public void	setReceiver(IComponentIdentifier receiver)
	{
		this.receiver	= receiver;
	}

//	/** This parameter indicates that subsequent messages in this conversation thread
//	 *  are to be directed to the agent named in the reply-to parameter,
//	 *  instead of to the agent named in the sender parameter. */
//	public IComponentIdentifier	reply_to;

	//-------- Content of Message -------- 

	/** Denotes the content of the message; equivalently denotes the object of the action.
	 *  The meaning of the content of any ACL message is intended to be interpreted by the receiver of the message.
	 *  This is particularly relevant for instance when referring to referential expressions,
	 *  whose interpretation might be different for the sender and the receiver. */
	private Object	content;
	/** Denotes the content of the message; equivalently denotes the object of the action.
	 *  The meaning of the content of any ACL message is intended to be interpreted by the receiver of the message.
	 *  This is particularly relevant for instance when referring to referential expressions,
	 *  whose interpretation might be different for the sender and the receiver. */
	public Object	getContent()
	{
		return content;
	}
	/** Denotes the content of the message; equivalently denotes the object of the action.
	 *  The meaning of the content of any ACL message is intended to be interpreted by the receiver of the message.
	 *  This is particularly relevant for instance when referring to referential expressions,
	 *  whose interpretation might be different for the sender and the receiver. */
	public void	setContent(Object content)
	{
		this.content	= content;
	}

//	//-------- Description of Content -------- 
//
//	/** Denotes the language in which the content parameter is expressed. */
//	public String	language;
//
//	/** Denotes the specific encoding of the content language expression. */
//	public String	encoding;
//
//	/** Denotes the ontology(s) used to give a meaning to the symbols in the content expression. */
//	public String	ontology;
//
//	//-------- Control of Conversation -------- 
//
//	/** Denotes the interaction protocol that the sending agent is employing with this ACL message. */
//	public String	protocol;
//
	/** Introduces an expression (a conversation identifier) which is used to identify
	 *  the ongoing sequence of communicative acts that together form a conversation. */
	private String	convid;
	/** Introduces an expression (a conversation identifier) which is used to identify
	 *  the ongoing sequence of communicative acts that together form a conversation. */
	public String	getConversationId()
	{
		return convid;
	}
	/** Introduces an expression (a conversation identifier) which is used to identify
	 *  the ongoing sequence of communicative acts that together form a conversation. */
	public void	setConversationId(String convid)
	{
		this.convid	= convid;
	}
//
//	/** Introduces an expression that will be used by the responding agent to identify this message. */
//	public String	reply_with;
//
//	/** Denotes an expression that references an earlier action to which this message is a reply. */
//	public String	in_reply_to;
//
//	/** Denotes a time and/or date expression which indicates the latest time
//	 *  by which the sending agent would like to receive a reply. */
//	public Long	reply_by;
	
	//-------- methods --------
	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return performative+"("+content+")";
	}
	
	/**
	 *  Create a reply for a given message.
	 */
	public FipaMessage	createReply()
	{
		FipaMessage	ret	= new FipaMessage();
		ret.setSender(getReceiver());
		ret.setReceiver(getSender());
		ret.setConversationId(getConversationId());
		return ret;
	}
}
