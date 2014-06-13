package jadex.bdiv3.runtime.impl;

import java.util.Collection;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3.runtime.IGoal;
import jadex.bridge.service.IServiceContainer;

/**
 *  Wrapper providing BDI methods to the user.
 */
public class CapabilityWrapper implements ICapability
{
	//-------- attributes --------
	
	/** The agent. */
	protected BDIAgent	agent;
	
	/** The pojo capability object. */
	protected Object	pojo;
	
	/** The fully qualified capability name (or null for agent). */
	protected String	capa;
	
	//-------- constructors --------
	
	/**
	 *  Create a capability wrapper.
	 */
	public CapabilityWrapper(BDIAgent agent, Object pojo, String capa)
	{
		this.agent	= agent;
		this.pojo	= pojo;
		this.capa	= capa;
	}
	
	//-------- ICapability interface --------
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(final String name, final IBeliefListener listener)
	{
		agent.addBeliefListener(capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+name : name, listener);
	}
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(String name, IBeliefListener listener)
	{
		agent.removeBeliefListener(capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+name : name, listener);
	}

	/**
	 *  Get the agent.
	 */
	public BDIAgent	getAgent()
	{
		return agent;
	}
	
	/**
	 *  Get the service container of the capability.
	 */
	public IServiceContainer	getServiceContainer()
	{
		return new ServiceContainerProxy(getInterpreter(), capa);
		
	}

	/**
	 *  Get the pojo capability object.
	 */
	public Object	getPojoCapability()
	{
		return pojo;
	}

//	/**
//	 *  Get the goals.
//	 *  @return The goals.
//	 */
//	public Collection<IGoal> getGoals()
//	{
//		return (Collection<IGoal>)getInterpreter().getCapability().getGoals();
//	}
	
	/**
	 *  Get the interpreter.
	 */
	protected BDIAgentInterpreter getInterpreter()
	{
		return (BDIAgentInterpreter)agent.getInterpreter();
	}
}
