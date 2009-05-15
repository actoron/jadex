package jadex.adapter.base.contextservice;

import jadex.bridge.IAgentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  The base context provides a simple grouping mechanism for agents.
 */
public class BaseContext	implements IContext
{
	//-------- attributes --------
	
	/** The parent of the context (if any). * /
	protected IContext	parent;*/

	/** The sub contexts of the context (if any). * /
	protected List	subcontexts;*/

	/** The name of the context. */
	protected String	name;

	/** The properties of the context. */
	protected Map	properties;
	
	/** The agents in the context. */
	protected Set	agents;
	
	/** The contained spaces. */
	protected Map spaces;
	
	/** Flag to indicate that the context is about to be deleted
	 * (no more agents can be added). */
	protected boolean	terminating;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public BaseContext(String name, /*IContext parent,*/ Map properties)
	{
		this.name	= name;
//		this.parent	= parent;
		this.properties	= properties!=null ? new HashMap(properties) : null;
		
		System.out.println("Created: "+this);
	}

	//-------- IContext interface --------
	
	/**
	 *  Get the name of the context.
	 */
	public String	getName()
	{
		return name;
	}

	/**
	 *  Get the parent of the context (if any).
	 * /
	public IContext	getParentContext()
	{
		return parent;
	}
	
	/**
	 *  Add a sub context.
	 * /
	public synchronized void	addSubContext(IContext context)
	{
		if(subcontexts==null)
			subcontexts	= new ArrayList();
		
		subcontexts.add(context);
	}
	
	/**
	 *  Remove a sub context.
	 * /
	public synchronized void	removeSubContext(IContext context)
	{
		if(subcontexts!=null)
		{
			subcontexts.remove(context);
			if(subcontexts.isEmpty())
			{
				subcontexts	= null;
			}
		}
	}

	/**
	 *  Get the sub contexts of the context (if any).
	 * /
	public synchronized IContext[]	getSubContexts()
	{
		return subcontexts==null ? null :
			(IContext[])subcontexts.toArray(new IContext[subcontexts.size()]);
	}*/
	
	/**
	 *  Add an agent to a context.
	 */
	// Cannot be synchronized due to deadlock with space (uses context.getAgentType()).
	public /*synchronized*/ void	addAgent(IAgentIdentifier agent)
	{
		ISpace[]	aspaces	= null;
		synchronized(this)
		{
			if(agents==null)
				agents	= new HashSet();
			
			agents.add(agent);
			
			if(spaces!=null)
			{
				aspaces	= (ISpace[])spaces.values().toArray(new ISpace[spaces.size()]);
			}
			
			System.out.println("Added agent: "+this);
		}

		if(aspaces!=null)
		{
			for(int i=0; i<aspaces.length; i++)
			{
				aspaces[i].agentAdded(agent);
			}
		}
	}
	
	/**
	 *  Remove an agent from a context.
	 */
	public synchronized void	removeAgent(IAgentIdentifier agent)
	{
		if(agents!=null)
		{
			agents.remove(agent);
			if(agents.isEmpty())
			{
				agents	= null;
			}
		}

		if(spaces!=null)
		{
			for(Iterator it=spaces.values().iterator(); it.hasNext(); )
			{
				((ISpace)it.next()).agentRemoved(agent);
			}
		}

		System.out.println("Removed agent: "+this);
	}
	
	/**
	 *  Add a space to the context.
	 *  @param space The space.
	 */
	public synchronized void addSpace(ISpace space)
	{
		if(spaces==null)
			spaces = new HashMap();
		
		spaces.put(space.getName(), space);

		if(agents!=null)
		{
			for(Iterator it=agents.iterator(); it.hasNext(); )
			{
				space.agentAdded((IAgentIdentifier)it.next());
			}
		}
		
		System.out.println("Added space: "+space);
	}
	
	/**
	 *  Add a space to the context.
	 *  @param name The space name.
	 */
	public synchronized void removeSpace(String name)
	{
		if(spaces!=null)
		{
			spaces.remove(name);
			if(spaces.isEmpty())
			{
				spaces = null;
			}
		}

		System.out.println("Removed space: "+name);
	}
	
	/**
	 *  Get a space by name.
	 *  @param name The name.
	 *  @return The space.
	 */
	public synchronized ISpace getSpace(String name)
	{
		return spaces==null? null: (ISpace)spaces.get(name);
	}

	/**
	 *  Test if an agent is contained in a context.
	 */
	public synchronized boolean	containsAgent(IAgentIdentifier agent)
	{
		return agents!=null && agents.contains(agent);
	}

	/**
	 *  Get all agents directly contained in the context (if any).
	 */
	public synchronized IAgentIdentifier[]	getAgents()
	{
		return agents==null ? null :
			(IAgentIdentifier[])agents.toArray(new IAgentIdentifier[agents.size()]);
	}
	
	//-------- template methods --------

	/**
	 *  Delete a context. Called from context service before a context is
	 *  removed from the platform. Default context behavior is to do nothing.
	 *  @param context	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void	deleteContext(IResultListener listener)
	{
		System.out.println("Deleted: "+this);

		if(listener!=null)
			listener.resultAvailable(this);
	}

	/**
	 *  Called by AMS when an agent has been created by another agent in this context.
	 *  @param creator	The creator of the new agent.
	 *  @param newagent	The newly created agent.
	 */
	public void	agentCreated(IAgentIdentifier creator, IAgentIdentifier newagent)
	{
	}

	/**
	 *  Called by AMS when an agent has been created by another agent in this context.
	 *  @param agent	The destroyed agent.
	 */
	public void	agentDestroyed(IAgentIdentifier agent)
	{
		removeAgent(agent);
	}
	
	/**
	 *  Add an agent property. 
	 *  @param agent The agent.
	 *  @param key The key.
	 *  @param prop The property.
	 */
	public synchronized void addProperty(IAgentIdentifier agent, String key, Object prop)
	{
		if(!containsAgent(agent))
			throw new RuntimeException("Agent not contained in context: "+agent+" "+this);
			
		Map agentprops = (Map)properties.get(agent);
		if(agentprops==null)
		{
			agentprops = new HashMap();
			properties.put(agent, agentprops);
		}
		
		agentprops.put(key, prop);
	}
	
	/**
	 *  Get agent property. 
	 *  @param agent The agent.
	 *  @param key The key.
	 *  @return The property. 
	 */
	public synchronized Object getProperty(IAgentIdentifier agent, String key)
	{
		Object ret = null;
		
		if(!containsAgent(agent))
			throw new RuntimeException("Agent not contained in context: "+agent+" "+this);
			
		Map agentprops = (Map)properties.get(agent);
		if(agentprops!=null)
			ret = agentprops.get(key);
		
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Get a string representation of the context.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(SReflect.getInnerClassName(getClass()));
		ret.append("(name=");
		ret.append(getName());
//		ret.append(", parent=");
//		ret.append(getParentContext());
		IAgentIdentifier[]	aids	= getAgents(); 
		if(aids!=null)
		{
			ret.append(", agents=");
			ret.append(SUtil.arrayToString(aids));
		}
//		IContext[]	subcs	= getSubContexts(); 
//		if(subcs!=null)
//		{
//			ret.append(", subcontexts=");
//			ret.append(SUtil.arrayToString(subcs));
//		}
		ret.append(")");
		return ret.toString();
	}
}
