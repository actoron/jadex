package jadex.adapter.jade;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;

import java.util.Iterator;
import java.util.Map;

/**
 *  Message adapter for Jade messages.
 */
public class JadeMessageAdapter implements IMessageAdapter
{
	//-------- attributes --------

	/** The message. */
	protected Object message;
	
	protected IAMS ams;
	
	/** The Jadex agent is used to access the logger. */
	//protected IJadexAgent	jadexagent;

	//-------- constructors --------

	/**
	 *  Create a new message adapter.
	 */
	public JadeMessageAdapter(ACLMessage message, IAMS ams)//, IJadexAgent agent)
	{
		this.message = message;
		this.ams = ams;
		//this.jadexagent	= agent;
	}

	//-------- methods --------

	/**
	 *  Get the message type.
	 *  @return The message type. 
	 */
	public MessageType getMessageType()
	{
		// todo:!!!
		return null; 
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

		if(name.equals(SFipa.ENCODING))
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
			ret = SJade.convertAIDtoFipa(message.getSender(), ams);
		}
		else if(name.equals(SFipa.REPLY_TO))
		{
			Iterator	it	= message.getAllReplyTo();
			if(it.hasNext())
				ret	= SJade.convertAIDtoFipa((AID)it.next(), ams);
			if(it.hasNext())
				System.out.println("Ignoring additional reply tos of message: "+message);
				//jadexagent.getLogger().warning("Ignoring additional reply tos of message: "+message);
		}
		else if(name.equals(SFipa.RECEIVERS))
		{
			final Iterator	it	= message.getAllReplyTo();
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
						return SJade.convertAIDtoFipa((AID)it.next(), ams);
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
		return null;
	}


	/**
	 *  Get the value for a parameter, or the values
	 *  for a parameter set from a native message.
	 *  Parameter set values can be provided as array, collection,
	 *  iterator or enumeration.
	 * /
	public Object	getValue(String name, RCapability scope)
	{
		Object ret = super.getValue(name, scope);

		// Treat action-class as parameter of jade message.
		if(name.equals(SFipa.ACTION_CLASS) && ret instanceof Action)
			ret = ((Action)ret).getAction().getClass();

		return ret;
	}*/

	/**
	 *  Get the value for a parameter, or the values
	 *  for a parameter set from a native message.
	 *  Parameter set values can be provided as array, collection,
	 *  iterator or enumeration.
	 * /
	public Object	getValue(String name)
	{
		Object val;
		// Todo: implement generically for all message types.
		if(SFipa.CONTENT.equals(name))
		{
			if(content!=null)
			{
				val	= content;
			}
			else if((val=getRawValue(name)) != null)
			{
				String lang = (String)getValue(SFipa.LANGUAGE);
				String onto = (String)getValue(SFipa.ONTOLOGY);
				if(lang!=null && onto!=null)
				{
					try
					{
						Properties props = new Properties();
						props.setProperty(SFipa.LANGUAGE, lang);
						props.setProperty(SFipa.ONTOLOGY, onto);

						IContentCodec codec = agent.getContentCodec(props);
						val = codec.decode((String)val);
					}
					catch(Exception e)
					{
						try
						{
							val = jadeagent.getContentManager().extractContent((ACLMessage)getMessage());
						}
						catch(Exception e2)
						{
							val = new ContentException(e2.getMessage()+"\n"+getMessage(), e2);
						}
					}
					content	= val;
				}
			}
		}
		else
		{
			val = getRawValue(name);
		}

		return val;
	}*/

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
