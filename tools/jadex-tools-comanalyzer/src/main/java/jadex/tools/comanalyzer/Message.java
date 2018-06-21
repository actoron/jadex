package jadex.tools.comanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.util.Pair;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.commons.ComposedFilter;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;


/**
 *  The message object.
 */
public class Message extends ParameterElement
{
	// -------- constants --------

	// Names of the additional fields of an ACL messages.
	public static final String SEQ_NO = "seq-no";

	public static final String XID = SFipa.X_MESSAGE_ID;

	public static final String DATE = SFipa.X_TIMESTAMP;
	
	public static final String DURATION = "duration";

	// Names of the various fields of an ACL messages defined in SFipa.
	public static final String SENDER = SFipa.SENDER;

	public static final String RECEIVER = "receiver"; // split a multicast in single messages	

	public static final String RECEIVERS = SFipa.RECEIVERS;

	public static final String PERFORMATIVE = SFipa.PERFORMATIVE;

	public static final String CONTENT = SFipa.CONTENT;

	public static final String ONTOLOGY = SFipa.ONTOLOGY;

	public static final String ENCODING = SFipa.ENCODING;

	public static final String IN_REPLY_TO = SFipa.IN_REPLY_TO;

	public static final String LANGUAGE = SFipa.LANGUAGE;

	public static final String PROTOCOL = SFipa.PROTOCOL;

	public static final String REPLY_BY = SFipa.REPLY_BY;

	public static final String REPLY_WITH = SFipa.REPLY_WITH;

	public static final String REPLY_TO = SFipa.REPLY_TO;

	public static final String CONVERSATION_ID = SFipa.CONVERSATION_ID;

	// names for the message parameters used for the filter
	//	public static final String MESSAGE_SENT = "message-sent";

	//	public static final String MESSAGE_RECEIVED = "message-received";

	/** The allowed message attributes. */
	public static final List MESSAGE_ATTRIBUTES;

	static
	{
		MESSAGE_ATTRIBUTES = new ArrayList();
		MESSAGE_ATTRIBUTES.add(SEQ_NO);
		MESSAGE_ATTRIBUTES.add(XID);
		MESSAGE_ATTRIBUTES.add(DATE);
		MESSAGE_ATTRIBUTES.add(DURATION);
		MESSAGE_ATTRIBUTES.add(SENDER);
		MESSAGE_ATTRIBUTES.add(RECEIVER);
		MESSAGE_ATTRIBUTES.add(RECEIVERS);
		MESSAGE_ATTRIBUTES.add(PERFORMATIVE);
		MESSAGE_ATTRIBUTES.add(CONVERSATION_ID);
		MESSAGE_ATTRIBUTES.add(ONTOLOGY);
		MESSAGE_ATTRIBUTES.add(PROTOCOL);
		MESSAGE_ATTRIBUTES.add(CONTENT);
		MESSAGE_ATTRIBUTES.add(ENCODING);
		MESSAGE_ATTRIBUTES.add(IN_REPLY_TO);
		MESSAGE_ATTRIBUTES.add(LANGUAGE);
		MESSAGE_ATTRIBUTES.add(REPLY_BY);
		MESSAGE_ATTRIBUTES.add(REPLY_WITH);
		MESSAGE_ATTRIBUTES.add(REPLY_TO);
	}

	// -------- attributes --------

	/** The sender of the message */
	protected Component sender;

	/** The receiver of the message */
	protected Component receiver;

	/** The unique id (sequence nr) saved for quick access */
	protected int uniqueId;

	/**
	 * For loading from file.
	 * Create a message with given parameters and sequence number.
	 * @param arguments The parameters of the message.
	 * @param sequence The sequence number of the message.
	 */
	public Message()
	{
	}
	
	/**
	 * Create a message with given parameters and sequence number.
	 * @param arguments The parameters of the message.
	 * @param sequence The sequence number of the message.
	 */
	public Message(Object event, int sequence, String xid, IComponentIdentifier sender, IComponentIdentifier receiver, Object body)
	{
		assert receiver != null;

		this.uniqueId = sequence;

		if(body!=null)
		{
			IBeanIntrospector	bi	= BeanIntrospectorFactory.getInstance().getBeanIntrospector();
			Map<String, BeanProperty>	props	= bi.getBeanProperties(body.getClass(), true, false);
			for(Map.Entry<String, BeanProperty> entry: props.entrySet())
			{
				Object	val	= entry.getValue().getPropertyValue(body);
				if(val!=null)
				{
					// Use snake_case for FIPA backwards compatibility (hack?) 
					parameters.put(SUtil.camelToSnakeCase(entry.getKey()), val);
				}
			}
		}

		this.parameters.put(SEQ_NO, Integer.valueOf(sequence));
		//		this.parameters.put(EVENT_DIRECTION, direction);
		//		this.parameters.put(EVENT_TYPE, mt.getName());

		// parameters for element panel
		//		this.parameters.put(NAME, parameters.get(ID));
		this.parameters.put(NAME, xid);
		this.parameters.put(XID, xid);
		this.parameters.put(CLASS, Message.class.getSimpleName());
		this.parameters.put(RECEIVER, receiver);
		this.parameters.put(SENDER, sender);
		
//		public static final String DATE = SFipa.X_TIMESTAMP;
//		public static final String DURATION = "duration";
	}

	/**
	 * Get the user defined parameters. 
	 * Does not return parameters for internal use.
	 * 
	 * @return All defined parameters.
	 */
	public static final String[] getDeclaredParameters()
	{
		return (String[])MESSAGE_ATTRIBUTES.toArray(new String[MESSAGE_ATTRIBUTES.size()]);
	}

	//-------- Message methods --------

	/**
	 * @return The sender.
	 */
	public Component getSender()
	{
		return sender;
	}

	/**
	 * @return The receiver.
	 */
	public Component getReceiver()
	{
		return receiver;
	}

	/**
	 * @return The unique id
	 */
	public int getUniqueId()
	{
		return uniqueId;
	}
	
	/**
	 *  Set the unique id.
	 *  @param uniqueid The unique id.
	 */
	public void setUniqueId(int uniqueId)
	{
		this.uniqueId = uniqueId;
	}

	/**
	 * @return True if the message is part of a multicast.
	 */
	public boolean isMulticast()
	{
		IComponentIdentifier[] aids = (IComponentIdentifier[])getParameter(RECEIVERS);
		return aids.length > 1;
	}

	/**
	 * Helper method for checking the visibility of the message including the state of sender , receiver and dummy agent
	 * Returns a pair of Agents if the message should be displayed or <code>null</code> if not.
	 * 
	 * @return The pair of Agents (either redirected or not) or <code>null</code>
	 * if the message shouldnt be displayed
	 */
	public Pair getEndpoints()
	{
		// obvious
		if(!isVisible())
		{
			return null;
		}

		Component sender = getSender();
		Component receiver = getReceiver();

		// if sender and receiver are visible return without adjusting
		if(sender.isVisible() && receiver.isVisible())
		{
			return new Pair(sender, receiver);
		}

		// if dummy agent is visible and either one of the participants (XOR)
		// adjust redirection
		if(Component.DUMMY_COMPONENT.isVisible() && (sender.isVisible() ^ receiver.isVisible()))
		{
			sender = sender.isVisible() ? sender : Component.DUMMY_COMPONENT;
			receiver = receiver.isVisible() ? receiver : Component.DUMMY_COMPONENT;
			return new Pair(sender, receiver);
		}

		// in other cases dont display
		return null;
	}

	// -------- methods for maintaining the message --------


	/**
	 * Applies a filter on the message and returns if the visibility has changed.
	 * @param filter The filter to be applied on the message.
	 * @return True if the visibility has changed
	 */
	protected boolean applyFilter(MessageFilter[] filter)
	{
		boolean old = visible;

		ArrayList filters = SCollection.createArrayList();
		for(int i = 0; i < filter.length; i++)
		{
			// operator AND returns false in case the filter has no values
			// do not add empty filter
			if(filter[i].hasValues())
			{
				filters.add(filter[i]);
			}
		}

		IFilter ret;
		if(filters.size() == 1)
		{
			ret = (IFilter)filters.get(0);
		}
		else if(filters.size() > 1)
		{
			ret = new ComposedFilter((IFilter[])filters.toArray(new IFilter[filters.size()]), ComposedFilter.AND);
		}
		else
		{
			// no filter -> no invisible message
			visible = true;
			return old != visible;		
		}

		try
		{
			visible = ret.filter(this);
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return old != visible;
	}

	/**
	 * @param agent The sender to set.
	 */
	public void setSender(Component agent)
	{
		sender = agent;
	}

	/**
	 * @param agent The receiver to set.
	 */
	public void setReceiver(Component agent)
	{
		receiver = agent;
	}

	/**
	 * @param duration The duration to set.
	 */
	public void setDuration(long duration)
	{
		setParameter(Message.DURATION, Long.valueOf(duration));
	}

	/**
	 * @return The id of the element.
	 */
	public String getId()
	{
		return (String)getParameter(XID);
	}
	
	/**
	 * Returns a parameter value.
	 * @param name The name of the parameter.
	 * @return The value.
	 */
	public Object getParameter(String name)
	{
		Object ret = super.getParameter(name);
//		if(ret == null)
//		{
////			if(msgmap!=null)
////				ret = (String)msgmap.get(XID);
//			if(message!=null)
//				ret = message.getValue(name);
//		}
		return ret;
	}

	/**
	 * Returns the parameter map.
	 * @return The parameter map.
	 */
	public Map getParameters()
	{
		Map ret = new HashMap();
//		ret.putAll(parameters);
//		if(message!=null)
//			ret.putAll(message.getParameterMap());
		return ret;
	}
	
	/**
	 * Checks if a given parameter is contained by the parameter map.
	 * @param name The name of the parameter.
	 * @return <code>true</code> if the parameter is contained.
	 */
	public boolean hasParameter(String name)
	{
		boolean ret=parameters.containsKey(name);
//		if (ret==false) 
//			ret=message.getParameterMap().containsKey(name);
		return ret;
	}
	
	//-------- Comparable interface --------

	/**
	 * Messages are compared by their unique_id.
	 * That implies that messsages are sorted by their sequence number.
	 */
	public int compareTo(Object o)
	{
		Message other = (Message)o;
		return this.uniqueId - other.uniqueId;
	}

	// -------- Object methods --------

	/** 
	 * @return The hashcode.
	 */
	public int hashCode()
	{
		return uniqueId;
	}

	/** 
	 * Only messages with the same unique id are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Message && this.uniqueId == ((Message)obj).uniqueId;
	}

	/** 
	 * @return The string representation for the message.
	 */
	public String toString()
	{
		return "Message(" + "id=" + getId() + ")";
	}

}
