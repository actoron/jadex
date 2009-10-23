package eis.jadex;

import eis.EnvironmentInterfaceStandard;
import eis.exceptions.AgentException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IContext;
import jadex.bridge.ISpace;

/**
 * 
 */
public class EisSpace implements ISpace
{
	//-------- attributes --------
	
	/** The application context. */
	protected IApplicationContext context;
	
	/** The name of the space. */
	protected String name;
	
	/** The environment interface standard. */
	protected EnvironmentInterfaceStandard eis;
	
	//-------- constructors --------
	
	/**
	 *  Create a new EIS space.
	 */
	public EisSpace(String name, IApplicationContext context, EnvironmentInterfaceStandard eis)
	{
		this.name = name;
		this.context = context;
		this.eis = eis;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the space name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Get the context.
	 *  @return The context.
	 */
	public IContext getContext()
	{
		return context;
	}
	
	/**
	 *  Called from application context, when an agent was added.
	 *  Also called once for all agents in the context, when a space
	 *  is newly added to the context.
	 *  @param aid	The id of the added agent.
	 */
	public synchronized void agentAdded(IComponentIdentifier aid)
	{
		try
		{
			eis.registerAgent(aid.getName());
		}
		catch(AgentException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Called from application context, when an agent was removed.
	 *  @param aid	The id of the removed agent.
	 */
	public void	agentRemoved(IComponentIdentifier aid)
	{
		try
		{
			eis.unregisterAgent(aid.getName());
		}
		catch(AgentException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Get the environment interface standard.
	 *  @return The eis.
	 */
	public EnvironmentInterfaceStandard getEis()
	{
		return eis;
	}
	
	/**
	 *  Terminate the space.
	 */
	public void terminate()
	{
		// nothing to do.
	}

}
