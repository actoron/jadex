package jadex.bridge.fipa;

import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFilter;

/**
 *  FIPA message as struct.
 */
public class FipaMessage	implements IFilter<Object>
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
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public FipaMessage(){}
	
	/**
	 *  Constructor for most common fields (for sending).
	 */
	public FipaMessage(IComponentIdentifier receiver, String performative, Object content)
	{
		this.performative	= performative;
		this.content	= content;
		addReceiver(receiver);
	}
	
	/**
	 *  Constructor for all fields (e.g. for match template).
	 */
	public FipaMessage(IComponentIdentifier sender, Set<IComponentIdentifier> receivers, String performative, Object content,
		String convid, String protocol, IComponentIdentifier reply_to, String language, String ontology, String encoding,
		String reply_with, String in_reply_to, Long reply_by)
	{
		this.sender	= sender;
		this.receivers	= receivers;
		this.performative	= performative;
		this.content	= content;
		
		this.convid	= convid;
		this.protocol	= protocol;
		this.reply_to	= reply_to;
		this.language	= language;
		this.ontology	= ontology;
		this.encoding	= encoding;
		
		this.reply_with	= reply_with;
		this.in_reply_to	= in_reply_to;
		this.reply_by	= reply_by;
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
	private Set<IComponentIdentifier>	receivers;
	/** Denotes the identity of the intended recipients of the message. */
	public Set<IComponentIdentifier>	getReceivers()
	{
		return receivers;
	}
	/** Denotes the identity of the intended recipients of the message. */
	public void	setReceivers(Set<IComponentIdentifier> receivers)
	{
		this.receivers	= receivers;
	}
	/** Denotes the identity of the intended recipients of the message. */
	public void	addReceiver(IComponentIdentifier receiver)
	{
		if(receivers==null)
			this.receivers	= new LinkedHashSet<IComponentIdentifier>();
		receivers.add(receiver);
	}
	/** Denotes the identity of the intended recipients of the message. */
	public void	removeReceiver(Set<IComponentIdentifier> receiver)
	{
		if(receivers!=null)
			receivers.remove(receiver);
	}

	/** This parameter indicates that subsequent messages in this conversation thread
	 *  are to be directed to the agent named in the reply-to parameter,
	 *  instead of to the agent named in the sender parameter. */
	private IComponentIdentifier	reply_to;
	/** This parameter indicates that subsequent messages in this conversation thread
	 *  are to be directed to the agent named in the reply-to parameter,
	 *  instead of to the agent named in the sender parameter. */
	public IComponentIdentifier	getReplyTo()
	{
		return reply_to;
	}
	/** This parameter indicates that subsequent messages in this conversation thread
	 *  are to be directed to the agent named in the reply-to parameter,
	 *  instead of to the agent named in the sender parameter. */
	public void	setReplyTo(IComponentIdentifier reply_to)
	{
		this.reply_to	= reply_to;
	}


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

	//-------- Description of Content -------- 

	/** Denotes the language in which the content parameter is expressed. */
	private String	language;
	/** Denotes the language in which the content parameter is expressed. */
	public String	getLanguage()
	{
		return language;
	}
	/** Denotes the language in which the content parameter is expressed. */
	public void	setLanguage(String language)
	{
		this.language	= language;
	}

	/** Denotes the specific encoding of the content language expression. */
	private String	encoding;
	/** Denotes the specific encoding of the content language expression. */
	public String	getEncoding()
	{
		return encoding;
	}
	/** Denotes the specific encoding of the content language expression. */
	public void	setEncoding(String encoding)
	{
		this.encoding	= encoding;
	}

	/** Denotes the ontology(s) used to give a meaning to the symbols in the content expression. */
	private String	ontology;
	/** Denotes the ontology(s) used to give a meaning to the symbols in the content expression. */
	public String	getOntology()
	{
		return ontology;
	}
	/** Denotes the ontology(s) used to give a meaning to the symbols in the content expression. */
	public void	setOntology(String ontology)
	{
		this.ontology	= ontology;
	}

	//-------- Control of Conversation -------- 

	/** Denotes the interaction protocol that the sending agent is employing with this ACL message. */
	private String	protocol;
	/** Denotes the interaction protocol that the sending agent is employing with this ACL message. */
	public String	getProtocol()
	{
		return protocol;
	}
	/** Denotes the interaction protocol that the sending agent is employing with this ACL message. */
	public void	setProtocol(String protocol)
	{
		this.protocol	= protocol;
	}

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

	/** Introduces an expression that will be used by the responding agent to identify this message. */
	private String	reply_with;
	/** Introduces an expression that will be used by the responding agent to identify this message. */
	public String	getReplyWith()
	{
		return reply_with;
	}
	/** Introduces an expression that will be used by the responding agent to identify this message. */
	public void	setReplyWith(String reply_with)
	{
		this.reply_with	= reply_with;
	}

	/** Denotes an expression that references an earlier action to which this message is a reply. */
	private String	in_reply_to;
	/** Denotes an expression that references an earlier action to which this message is a reply. */
	public String	getInReplyTo()
	{
		return in_reply_to;
	}
	/** Denotes an expression that references an earlier action to which this message is a reply. */
	public void	setInReplyTo(String in_reply_to)
	{
		this.in_reply_to	= in_reply_to;
	}

	/** Denotes a time and/or date expression which indicates the latest time
	 *  by which the sending agent would like to receive a reply. */
	private Long	reply_by;
	/** Denotes a time and/or date expression which indicates the latest time
	 *  by which the sending agent would like to receive a reply. */
	public Long	getReplyBy()
	{
		return reply_by;
	}
	/** Denotes a time and/or date expression which indicates the latest time
	 *  by which the sending agent would like to receive a reply. */
	public void	setReplyBy(Long reply_by)
	{
		this.reply_by	= reply_by;
	}
	
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

		// Copied parameters
		ret.setConversationId(getConversationId());
		ret.setProtocol(getProtocol());
		ret.setLanguage(getLanguage());
		ret.setOntology(getOntology());
		ret.setEncoding(getEncoding());
		
		// Mapped parameters
		if(getReplyTo()!=null || getSender()!=null)
		{
			ret.addReceiver(getReplyTo()!=null ? getReplyTo() : getSender());
		}
		ret.setInReplyTo(getReplyWith());
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(Object obj)
	{
		if(obj instanceof FipaMessage)
		{
			FipaMessage	msg	= (FipaMessage)obj;
			return sender==null || sender.equals(msg.getSender())
				&& receivers==null || receivers.isEmpty() && msg.getReceivers()==null || receivers.equals(msg.getReceivers())
				&& performative==null || performative.equals(msg.getPerformative())
				&& content==null || content.equals(msg.getContent())
				&& convid==null || convid.equals(msg.getConversationId());
		}
		else
		{
			return false;
		}
	}
}
