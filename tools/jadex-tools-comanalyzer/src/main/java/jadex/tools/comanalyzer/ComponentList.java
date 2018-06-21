package jadex.tools.comanalyzer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;


/**
 * A list of Agents. This class provides methods to notify other classes which
 * are implementing the IAgentListListener interface about adding, changing and
 * removing of agents.
 */
public class ComponentList implements Serializable
{

	//-------- attributes --------

	/** The agents in the agentlist */
	private List elements;

	/** The listener for agent events */
	private List listeners;

	// -------- constructor --------

	/**
	 * Default constructor for the agent list.
	 */
	public ComponentList()
	{
		listeners = new ArrayList();
		elements = new ArrayList();
	}


	//-------- AgentList methods --------

	/**
	 * Adds an agent to the list.
	 * @param agent The agent to add.
	 */
	public void addAgent(Component agent)
	{
		if(elements.contains(agent))
			return;
//			throw new IllegalArgumentException("Agent already in agentlist.");

//		System.out.println("Agent added: "+agent);
		elements.add(agent);
		fireAgentsAdded(new Component[]{agent});
	}

	/**
	 * @param agent The agent to remove.
	 */
	public void removeAgent(Component agent)
	{
		elements.remove(agent);
		fireAgentsRemoved(new Component[]{agent});
	}

	/**
	 * Clears the agent list and notifies all listeners
	 */
	public void removeAllAgents()
	{
		Component[] agents = (Component[])elements.toArray(new Component[elements.size()]);
		elements.clear();
		fireAgentsRemoved(agents);
	}

	/**
	 * @return The list of agents.
	 */
	public List getList()
	{
		return elements;
	}

	/**
	 * @return The array of agents.
	 */
	public Component[] getAgents()
	{
		return (Component[])elements.toArray(new Component[elements.size()]);
	}

	/**
	 * Checks if an agent is contained in the agent list.
	 * @param agent The agent to check.
	 * @return <code>true</code> if a specific agent is in the agentlist.
	 */
	public boolean containsAgent(Component agent)
	{
		return elements.contains(agent);
	}

	/**
	 * Returns the agent from the list with the same identifier
	 * @param aid The agent identifier.
	 * @return The agent or <code>null</code> if there is no agent with the
	 * same identifier in the list.
	 */
	public Component getAgent(IComponentIdentifier aid)
	{
		for(Iterator iter = elements.iterator(); iter.hasNext();)
		{
			Component original = (Component)iter.next();
			if(SUtil.equals(original.getDescription()!=null ? original.getDescription().getName() : null, aid))
			{
				return original;
			}
		}
		return null;
	}

	/**
	 * @return The agent list iterator.
	 */
	public Iterator iterator()
	{
		return elements.iterator();
	}

	/**
	 * @return The size of the agentlist.
	 */
	public int size()
	{
		return elements.size();
	}

	// -------- methods for listeners --------


	/**
	 * Register for agent events.
	 * @param listener A class implementing the IAgentListListener interface.
	 */
	protected void addListener(IComponentListListener listener)
	{
		if(!listeners.contains(listener))
		{
			listeners.add(listener);
//			for(Iterator iter = elements.iterator(); iter.hasNext();)
//			{
//				Agent agent = (Agent)iter.next();
				listener.componentsAdded((Component[])elements.toArray(new Component[elements.size()]));
//			}

		}
	}

	/**
	 * Notifies the listeners about the removal of agents.
	 * @param agents The removed agents.
	 */
	protected void fireAgentsRemoved(Component[] agents)
	{
		if(agents != null)
		{
			for(Iterator iter = listeners.iterator(); iter.hasNext();)
			{
				IMessageListListener listener = (IMessageListListener)iter.next();
				((IComponentListListener)listener).componentsRemoved(agents);
			}
		}
	}

	/**
	 * Notifies the listeners about the adding of agents.
	 * @param agents The added agents.
	 */
	protected void fireAgentsAdded(Component[] agents)
	{
		if(agents != null)
		{
			for(Iterator iter = listeners.iterator(); iter.hasNext();)
			{
				IMessageListListener listener = (IMessageListListener)iter.next();
				((IComponentListListener)listener).componentsAdded(agents);
			}
		}
	}

	/**
	 * Notifies the listeners about agents with changed visibility.
	 * @param agents The changed agents.
	 */
	protected void fireAgentsChanged(Component[] agents)
	{
		if(agents != null)
		{
			for(Iterator iter = listeners.iterator(); iter.hasNext();)
			{
				IMessageListListener listener = (IMessageListListener)iter.next();
				((IComponentListListener)listener).componentsChanged(agents);
			}
		}

	}

}
