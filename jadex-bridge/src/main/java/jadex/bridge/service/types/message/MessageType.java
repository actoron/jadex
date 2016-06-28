package jadex.bridge.service.types.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.IMessageAdapter;


/**
 *  Representation of characteristics of a certain type of messages
 *  (e.g. fipa messages).
 */
public abstract class MessageType	implements Serializable //, Cloneable // todo
{
	/** The message types. */
	private static Map<String, MessageType> messagetypes = Collections.synchronizedMap(new HashMap<String, MessageType>());
	
	/**
	 *  Get the message type per name.
	 *  @param type The type name. 
	 *  @return The message type.
	 */
	public static MessageType getMessageType(String type)
	{
		return messagetypes.get(type);
	}
	
	/**
	 *  Add a new message type.
	 *  @param type The message type.
	 */
	public static void addMessageType(MessageType type)
	{
		messagetypes.put(type.getName(), type);
	}
	
	//-------- attributes --------

	/** The name of the message type. */
	protected String	name;

	/** The allowed parameters. */
	protected ParameterSpecification[]	params;

	/** The allowed parameter sets. */
	protected ParameterSpecification[]	paramsets;

	/** The conversation relevant parameters. */
	protected ParameterSpecification[] conversationparams;
	
	/** The parameters by name (name -> parameter spec). */
	protected Map<String, ParameterSpecification> parammap;
	
	//-------- constructors --------

	/**
	 *  Create a new message type.
	 *  @param name	The name of the message type.
	 */
	public MessageType(String name, ParameterSpecification[] params, ParameterSpecification[] paramsets)
	{
		this.name	= name;
		this.params	= params;
		this.paramsets	= paramsets;
		
		this.parammap = new HashMap<String, ParameterSpecification>();
		for(int i=0; i<params.length; i++)
			parammap.put(params[i].getName(), params[i]);
		for(int i=0; i<paramsets.length; i++)
			parammap.put(paramsets[i].getName(), paramsets[i]);
	}

	//-------- methods --------

	/**
	 *  The name of the message type.
	 */
	public String	getName()
	{
		return name;
	}

	/**
	 *  Get the default parameters.
	 */
	public ParameterSpecification[]	getParameters()
	{
		return params;
	}

	/**
	 *  Get the default parameter sets.
	 */
	public ParameterSpecification[]	getParameterSets()
	{
		return paramsets;
	}

	/**
	 *  Get all parameter names.
	 *  @return The parameter names.
	 */
	public String[] getParameterNames()
	{
		String[] names = new String[params.length];
		for(int i=0; i<names.length; i++)
		{
			names[i] = params[i].getName();
		}
		return names;
	}

	/**
	 *  Get all parameter set names.
	 *  @return The parameter set names.
	 */
	public String[] getParameterSetNames()
	{
		String[] names = new String[paramsets.length];
		for(int i=0; i<names.length; i++)
		{
			names[i] = paramsets[i].getName();
		}
		return names;
	}
	
	/**
	 *  Get a parameter specification by name (including parameter sets).
	 *  @return The parameter specification.
	 */
	public ParameterSpecification getParameter(String name)
	{
		return (ParameterSpecification)parammap.get(name);
	}
	
	/**
	 *  Get a parameter specification by name (including parameter sets).
	 *  @return The parameter specification.
	 */
	public ParameterSpecification getParameterSet(String name)
	{
		return (ParameterSpecification)parammap.get(name);
	}
	
	/**
	 *  Get the conversation identifiers.
	 *  @return The conversation identifiers.
	 */
	public ParameterSpecification[] getConversationIdentifiers()
	{
		if(conversationparams==null)
		{
			List<ParameterSpecification> tmp = new ArrayList<ParameterSpecification>();
			for(int i=0; i<params.length; i++)
			{
				if(params[i].isConversationIdentifier())
					tmp.add(params[i]);
			}
			
			for(int i=0; i<paramsets.length; i++)
			{
				if(paramsets[i].isConversationIdentifier())
					tmp.add(paramsets[i]);
			}
			
			conversationparams = (ParameterSpecification[])tmp.toArray(
				new ParameterSpecification[tmp.size()]);
		}
		return conversationparams;
	}

	/**
	 *  Get the identifier for fetching the receivers.
	 *  @return The receiver identifier.
	 */
	public abstract String getReceiverIdentifier();

	/**
	 *  Get the identifier for fetching the sender.
	 *  @return The sender identifier.
	 */
	public abstract String getSenderIdentifier();
	
	/**
	 *  Get the identifier for fetching the message id.
	 *  Support for message identifiers is optional.
	 *  @return The id identifier.
	 */
	public abstract String getIdIdentifier();
	
	/**
	 *  Get the identifier for fetching the send date.
	 *  Support for date is optional.
	 *  @return The send date identifier.
	 */
	public abstract String getTimestampIdentifier();
	
	/**
	 *  Get the identifier for fetching the resource identifier id.
	 *  @return The resource identifier id.
	 */
	public abstract String getResourceIdIdentifier();
	
	/**
	 *  Get the identifier for fetching the non-functional properties.
	 *  @return The non-functional properties.
	 */
	public abstract String getNonFunctionalPropertiesIdentifier();

	/**
	 *  Get the identifier for fetching the resource identifier id.
	 *  @return The resource identifier id.
	 */
	public abstract String getRealReceiverIdentifier();

	/**
	 *  Get the en/decode info (important) for a parameter/set.
	 *  @param The name of the parameter/set.
	 *  @return The en/decode infos.
	 */
	public abstract String[] getCodecInfos(String name);

	/**
	 *  Get a simplified human readable representation of the message content.
	 *  @param The message.
	 *  @return The simplified representation.
	 */
	public abstract String getSimplifiedRepresentation(Map<String, Object> msg);
		
	/**
	 *  Test if two message types are equal (based on the name).
	 */
	public boolean	equals(Object o)
	{
		return o instanceof MessageType && name.equals(((MessageType)o).getName()); 
	}
	
	/**
	 *  Create a reply to a message.
	 *  @param mag	The message.
	 *  @return The reply.
	 */
	public Map<String, Object> createReply(Map<String, Object> msg)
	{
		Map<String, Object> reply = new HashMap<String, Object>();
		
		MessageType.ParameterSpecification[] params	= getParameters();
		for(int i=0; i<params.length; i++)
		{
			String sourcename = params[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					reply.put(params[i].getName(), sourceval);
				}
			}
		}
		
		MessageType.ParameterSpecification[] paramsets = getParameterSets();
		for(int i=0; i<paramsets.length; i++)
		{
			String sourcename = paramsets[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					List<Object> tmp = new ArrayList<Object>();
					tmp.add(sourceval);
					reply.put(paramsets[i].getName(), tmp);	
				}
			}
		}
		
		return reply;
	}	

	//-------- inner classes --------

	/**
	 *  A class representing a parameter or parameter set specification.
	 */
	public static class ParameterSpecification	implements Serializable
	{
		//-------- attributes --------

		/** The parameter(set) name. */
		protected String name;

		/** The parameter(set) class. */
		protected Class<?>	clazz;

		/** Default value expression of the parameter(set), if any. */
//		protected String defaultvalue;

		/** Source parameter when copying reply values (if any). */
		protected String source;

		/** True, if this parameter can be used to identify an ongoing conversation. */
		protected boolean convid;

		/** The flag for indicating if it is a parameter set. */
		protected boolean set;
		
		//-------- constructors --------
	
		/**
		 *  Create a parameter(set) specification.
		 */
		public ParameterSpecification(String name, Class<?> clazz, boolean set)
		{
			this(name, clazz, null, false, set);
		}

		/**
		 *  Create a parameter(set) specification.
		 */
		public ParameterSpecification(String name, Class<?> clazz, String source, boolean convid, boolean set)
		{
			// Conversation identifying parameters must have a source (to match against).
			assert !convid || source!=null;

			this.name	= name;
			this.clazz	= clazz;
//			this.defaultvalue	= defaultvalue;
			this.source	= source;
			this.convid	= convid;
			this.set = set;
		}

		//-------- methods --------

		/**
		 *  Get the name of the parameter(set).
		 */
		public String	getName()
		{
			return name;
		}

		/**
		 *  Get the clazz of the parameter(set).
		 */
		public Class<?>	getClazz()
		{
			return clazz;
		}

		/**
		 *  Get the default value of the parameter(set).
		 * /
		public String	getDefaultValue()
		{
			return defaultvalue;
		}*/

		/**
		 *  Get the source parameter for copying reply values (if any).
		 */
		public String	getSource()
		{
			return source;
		}

		/**
		 *  True, if this parameter can be used to identify an ongoing conversation.
		 */
		public boolean	isConversationIdentifier()
		{
			return convid;
		}

		/**
		 *  Get the set.
		 *  @return The set.
		 */
		public boolean isSet()
		{
			return set;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return name;
		}
	}
}
