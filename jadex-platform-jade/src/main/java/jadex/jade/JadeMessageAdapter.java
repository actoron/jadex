package jadex.jade;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jadex.base.fipa.SFipa;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.types.message.MessageType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Message adapter for Jade messages.
 */
public class JadeMessageAdapter implements IMessageAdapter
{
	//-------- attributes --------

	/** The message. */
	protected ACLMessage message;
	
	protected Map decvals;

	//-------- constructors --------

	/**
	 *  Create a new message adapter.
	 */
	public JadeMessageAdapter(ACLMessage message)
	{
		this.message = message;
	}

	//-------- methods --------

	/**
	 *  Get the message type.
	 *  @return The message type. 
	 */
	public MessageType getMessageType()
	{
		return SFipa.FIPA_MESSAGE_TYPE;
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
	 *  Get the value for a parameter, or the values
	 *  for a parameter set.
	 *  Parameter set values can be provided as array, collection,
	 *  iterator or enumeration.
	 */
	public Object	getValue(String name)
	{
		Object ret = null;
		ACLMessage message = (ACLMessage)getMessage();

		if(decvals!=null && decvals.containsKey(name))
		{	
			ret = decvals.get(name);
		}
		else if(name.equals(SFipa.ENCODING))
		{
			ret = message.getEncoding();
		}
		else if(name.equals(SFipa.IN_REPLY_TO))
		{
			ret = message.getInReplyTo();
		}
		else if(name.equals(SFipa.LANGUAGE))
		{
			ret = message.getLanguage();
		}
		else if(name.equals(SFipa.ONTOLOGY))
		{
			ret = message.getOntology();
		}
		else if(name.equals(SFipa.PROTOCOL))
		{
			ret = message.getProtocol();
		}
		else if(name.equals(SFipa.REPLY_BY))
		{
			ret = message.getReplyByDate();
		}
		else if(name.equals(SFipa.REPLY_WITH))
		{
			ret = message.getReplyWith();
		}
		else if(name.equals(SFipa.CONVERSATION_ID))
		{
			ret = message.getConversationId();
		}
		else if(name.equals(SFipa.PERFORMATIVE))
		{
			ret = ACLMessage.getPerformative(message.getPerformative()).toLowerCase();
		}
		else if(name.equals(SFipa.CONTENT))
		{
			ret = message.getContent();
		}
		else if(name.equals(SFipa.SENDER))
		{
			ret = SJade.convertAIDtoFipa(message.getSender());
		}
		else if(name.equals(SFipa.REPLY_TO))
		{
			Iterator	it	= message.getAllReplyTo();
			if(it.hasNext())
				ret	= SJade.convertAIDtoFipa((AID)it.next());
			if(it.hasNext())
				System.out.println("Ignoring additional reply tos of message: "+message);
				//jadexagent.getLogger().warning("Ignoring additional reply tos of message: "+message);
		}
		else if(name.equals(SFipa.RECEIVERS))
		{
			final Iterator	it	= message.getAllReceiver();
			if(it.hasNext())
			{
				ret	= new Iterator()
				{
					public boolean hasNext()
					{
						return it.hasNext();
					}
					public Object next()
					{
						return SJade.convertAIDtoFipa((AID)it.next());
					}
					public void remove()
					{
						it.remove();
					}
				};
			}
		}

		return ret;
	}
	
	/**
	 *  Get the parameters as map.
	 *  @return A map of parameters.
	 */
	public Map getParameterMap()
	{
		Map ret = decvals!=null ? (Map)((HashMap)decvals).clone() : new HashMap();
		
		for(Iterator it=SFipa.MESSAGE_ATTRIBUTES.iterator(); it.hasNext(); )
		{
			String	attr	= (String)it.next();
			if(!ret.containsKey(attr))
			{
				ret.put(attr, getValue(attr));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Set a decoded value (e.g. content).
	 *  @param name The name.
	 *  @param value The value.
	 */
	public void setDecodedValue(String name, Object value)
	{
		if(decvals==null)
			decvals = new HashMap();
		decvals.put(name, value);
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "JadeMessageAdapter: "+getMessage();
	}

	/** The X_MESSAGE_ID. */
	//static final String	MESSAGE_ID	= "MessageID";
	
	/** 
	 *  Get the id.
	 *  @return The message id.
	 */
	public String getId()
	{
		ACLMessage message = (ACLMessage)getMessage();
		return message.getUserDefinedParameter(SFipa.X_MESSAGE_ID);
	}
	
	/** 
	 *  Set the id.
	 *  @param id
	 */
	public void setId(String id) 
	{
		ACLMessage message = (ACLMessage)getMessage();
		message.addUserDefinedParameter(SFipa.X_MESSAGE_ID, id);
	}
}
