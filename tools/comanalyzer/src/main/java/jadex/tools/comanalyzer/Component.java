package jadex.tools.comanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.ComposedFilter;
import jadex.commons.IFilter;


/**
 * The Agent object.
 */
public class Component extends ParameterElement
{	
	/** The dummy agent */
	public static final Component DUMMY_COMPONENT = new Component();

	/** The name for the dummy agent */
	public static final String DUMMY_NAME = "Dummy";

	// Names of the parameters for an agent
	public static final String STATE = "state";
	
	/** The id of the element */
	public static final String ID = "id";

	public static final String AID = "aid";

	public static final String DESCRIPTION = "desc";

	public static final String MESSAGE_COUNT = "message_count";

	public static final String MESSAGE_VISIBLE = "message_visible";

	public static final int NO_MESSAGES = 0;

	// possible states for an agent
	public static final String STATE_DEAD = "dead";

	public static final String STATE_OBSERVED = "observed";

	public static final String STATE_IGNORED = "ignored";

	public static final String STATE_DUMMY = "dummy";

	public static final String STATE_REMOTE = "remote";

	/** The allowed message attributes. */
	public static final List AGENT_ATTRIBUTES;

	static
	{
		AGENT_ATTRIBUTES = new ArrayList();
		AGENT_ATTRIBUTES.add(ID);
		AGENT_ATTRIBUTES.add(AID);
		AGENT_ATTRIBUTES.add(DESCRIPTION);
		AGENT_ATTRIBUTES.add(NAME);
		AGENT_ATTRIBUTES.add(STATE);
		AGENT_ATTRIBUTES.add(MESSAGE_COUNT);
		AGENT_ATTRIBUTES.add(MESSAGE_VISIBLE);
	}

	/** The agentidentifier */
//	protected IComponentIdentifier aid;

	/** The messages send or received by the agent */
	protected List messages = new ArrayList();

	/**
	 * Constructor for a dummy agent which represents every agent not present on
	 * the canvas.
	 */
	public Component()
	{
		this(null);

		parameters.put(STATE, STATE_DUMMY);

		// dont apply for dummy agent
		parameters.remove(MESSAGE_VISIBLE);
		parameters.remove(MESSAGE_COUNT);
	}

	/**
	 * Constructor for any named agent to be put on the Agent Canvas
	 */
	public Component(IComponentDescription desc)
	{
		this.visible = true;

		parameters.put(ID, desc==null? DUMMY_NAME: desc.getName().getName());
		parameters.put(AID, desc==null? null: desc.getName());
		parameters.put(DESCRIPTION, desc);

		// parameters.put(STATE, STATE_UNKNOWN);
		parameters.put(MESSAGE_COUNT, Integer.valueOf(NO_MESSAGES));
		parameters.put(MESSAGE_VISIBLE, Integer.valueOf(NO_MESSAGES));

		parameters.put(NAME, desc==null? DUMMY_NAME: desc.getName().getName());
		parameters.put(CLASS, Component.class.getSimpleName());
	}

	/**
	 * @return The declared agent attributes.
	 */
	public static final String[] getDeclaredAttributes()
	{
		return (String[])AGENT_ATTRIBUTES.toArray(new String[AGENT_ATTRIBUTES.size()]);
	}

	/**
	 * Returns the parameter map.
	 * 
	 * @return The parameter map.
	 */
	public Map getParameters()
	{
		// calculate message count on access
		// but skip dummy agent, because this filter doesnt apply to it
		if(!this.equals(Component.DUMMY_COMPONENT))
		{
			int msg_visible = NO_MESSAGES;
			List msgs = getMessages();
			for(int i = 0; i < msgs.size(); i++)
			{
				if(((Message)msgs.get(i)).isVisible())
				{
					// check if the messaqe is redirected and dummy is visible
					if(Component.DUMMY_COMPONENT.getMessages().contains(msgs.get(i)))
					{
						if(Component.DUMMY_COMPONENT.isVisible())
						{
							msg_visible++;
						}
					}
					else
					{
						msg_visible++;
					}
				}
			}
			parameters.put(MESSAGE_VISIBLE, Integer.valueOf(msg_visible));
			parameters.put(MESSAGE_COUNT, Integer.valueOf(messages.size()));
		}

		return parameters;
	}

	/**
	 * Returns the parameter with the given name.
	 * 
	 * @param name The parameter name.
	 * @return The parameter object
	 */
	public Object getParameter(String name)
	{
		// to get the current message count
		if(name.equals(MESSAGE_VISIBLE) || name.equals(MESSAGE_COUNT))
		{
			return getParameters().get(name);
		}

		return parameters.get(name);
	}

	/**
	 * @return The component description.
	 */
	public IComponentDescription getDescription()
	{
		return (IComponentDescription)parameters.get(DESCRIPTION);
	}

	/**
	 * @return The state.
	 */
	public String getState()
	{
		return (String)parameters.get(STATE);
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(String state)
	{
		this.parameters.put(STATE, state);
	}

	/**
	 * Applies a filter to the agent and returns if the visibility has changed.
	 * @param filter The array of filters to apply to.
	 * @param zeromessages <code>True</code> if the zero message filter should be applied
	 * @return True if visibility has changed.
	 */
	public boolean applyFilter(ComponentFilter[] filter, boolean zeromessages)
	{
		boolean old = visible;

		ArrayList filters = new ArrayList();
		for(int i = 0; i < filter.length; i++)
		{
			// operator NOT returns false in case the filter has no values
			// do not add empty filter
			if(filter[i].hasValues())
			{
				if (zeromessages) {
					filters.add(filter[i]);
				} else if (!filter[i].containsValue(Component.MESSAGE_VISIBLE, Integer.valueOf(Component.NO_MESSAGES)))  {
					filters.add(filter[i]);
				}
			}
		}

		// combine all filters with OR and then with NOT
		// (e.g. NOT (filter1 OR filter2 ... OR filter9)
		IFilter ret = new ComposedFilter((IFilter[])filters.toArray(new IFilter[filters.size()]), ComposedFilter.OR);
		ret = new ComposedFilter(new IFilter[]{ret}, ComposedFilter.NOT);

		try
		{
			visible = ret.filter(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return old != visible;
	}

	/**
	 * Returns the messages send or received by the agent.
	 * @return The array of messages.
	 */
	public List getMessages()
	{
		return messages;//return (Message[])messages.toArray(new Message[messages.size()]);
	}

	/**
	 * Returns the message list send or received by the agent.
	 * @return The message list.
	 */
//	public List getMessageList()
//	{
//		return messages;
//	}
	
	/**
	 * Set the messages.
	 * @param messages The messages.
	 */
	public void setMessages(List messages)
	{
		this.messages = messages;
	}

	/**
	 * Adds a message to the agent.
	 * @param message The message to add to the agent.
	 */
	public void addMessage(Message message)
	{
		messages.add(message);
	}

	/**
	 * Removes a message from the agent.
	 * @param message The message to remove.
	 */
	public void removeMessage(Message message)
	{
		messages.remove(message);
	}

	/**
	 * Removes all messages send or received by the agent.
	 */
	public void removeAllMessages()
	{
		messages.clear();
	}
	
	/**
	 * @return The id of the element.
	 */
	public String getId()
	{
		return (String)parameters.get(ID);
	}

	/**
	 * Agents are compared by their names. Only the dummy agent is always the
	 * first in line.
	 */
	public int compareTo(Object o)
	{
		Component other = (Component)o;
		// first test if equal
		if(this == other || this.equals(other))
		{
			return 0;
		}
		// dummy agent is always the first
		if(this.equals(Component.DUMMY_COMPONENT))
		{
			return -1;
		}
		if(o.equals(Component.DUMMY_COMPONENT))
		{
			return 1;
		}
		// otherwise compare names
		return this.getId().compareTo(other.getId());
	}

	/**
	 * @return The string representation.
	 */
	public String toString()
	{
		return "Agent(" + "name=" + getId() + ")";
	}

}
