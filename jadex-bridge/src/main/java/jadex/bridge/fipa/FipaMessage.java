package jadex.bridge.fipa;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;

/**
 *  FIPA message as struct.
 */
public class FipaMessage
{
	// cf. http://www.fipa.org/specs/fipa00061/SC00061G.html
	
	//-------- Type of Communicative Act --------

	/** Denotes the type of the communicative act of the ACL message. */
	public String	performative;


	//-------- Participants in Communication -------- 

	/** Denotes the identity of the sender of the message,
	 *  that is, the name of the agent of the communicative act. */
	public IComponentIdentifier	sender;

	/** Denotes the identity of the intended recipients of the message. */
	public Set<IComponentIdentifier>	receivers;

	/** This parameter indicates that subsequent messages in this conversation thread
	 *  are to be directed to the agent named in the reply-to parameter,
	 *  instead of to the agent named in the sender parameter. */
	public IComponentIdentifier	reply_to;

	//-------- Content of Message -------- 

	/** Denotes the content of the message; equivalently denotes the object of the action.
	 *  The meaning of the content of any ACL message is intended to be interpreted by the receiver of the message.
	 *  This is particularly relevant for instance when referring to referential expressions,
	 *  whose interpretation might be different for the sender and the receiver. */
	public Object	content;

	//-------- Description of Content -------- 

	/** Denotes the language in which the content parameter is expressed. */
	public String	language;

	/** Denotes the specific encoding of the content language expression. */
	public String	encoding;

	/** Denotes the ontology(s) used to give a meaning to the symbols in the content expression. */
	public String	ontology;

	//-------- Control of Conversation -------- 

	/** Denotes the interaction protocol that the sending agent is employing with this ACL message. */
	public String	protocol;

	/** Introduces an expression (a conversation identifier) which is used to identify
	 *  the ongoing sequence of communicative acts that together form a conversation. */
	public String	conversation_id;

	/** Introduces an expression that will be used by the responding agent to identify this message. */
	public String	reply_with;

	/** Denotes an expression that references an earlier action to which this message is a reply. */
	public String	in_reply_to;

	/** Denotes a time and/or date expression which indicates the latest time
	 *  by which the sending agent would like to receive a reply. */
	public Long	reply_by;
}
