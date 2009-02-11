package jadex.adapter.base.contextservice;

import jadex.adapter.base.IContext;
import jadex.bridge.IAgentIdentifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  A context represents an abstract grouping of agents.
 */
public class Context	implements IContext
{
	//-------- attributes --------
	
	/** The parent of the context (if any). */
	protected IContext	parent;

	/** The children of the context (if any). */
	protected List	children;

	/** The name of the context. */
	protected String	name;

	/** The type of the context. */
	protected String	type;

	/** The properties of the context. */
	protected Map	properties;
	
	/** The agents in the context. */
	protected Set	agents;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public Context(String name, String type, IContext parent)
	{
		this.name	= name;
		this.type	= type;
		this.parent	= parent;
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
	 *  Get the type of the context.
	 */
	public String	getType()
	{
		return type;
	}

	/**
	 *  Get the parent of the context (if any).
	 */
	public IContext	getParent()
	{
		return parent;
	}
	
	/**
	 *  Get the children of the context (if any).
	 */
	public synchronized IContext[]	getChildren()
	{
		return children==null ? null :
			(IContext[])children.toArray(new IContext[children.size()]);
	}
	
	/**
	 *  Add an agent to a context.
	 */
	public synchronized void	addAgent(IAgentIdentifier agent)
	{
		if(agents==null)
			agents	= new HashSet();
		
		agents.add(agent);
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
	
	//-------- helper methods --------
	
	/**
	 *  Add a sub context.
	 */
	protected synchronized void	addChild(IContext context)
	{
		if(children==null)
			children	= new ArrayList();
		
		children.add(context);
	}
	
	/**
	 *  Remove a sub context.
	 */
	protected synchronized void	removeChild(IContext context)
	{
		if(children!=null)
		{
			children.remove(context);
			if(children.isEmpty())
			{
				children	= null;
			}
		}
	}
}
