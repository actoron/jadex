package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.InternalAccessAdapter;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.RequiredServicesFeatureAdapter;

/**
 *  Wrapper providing BDI methods to the user.
 */
public class CapabilityWrapper implements ICapability
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess	agent;
	
	/** The pojo capability object. */
	protected Object	pojo;
	
	/** The fully qualified capability name (or null for agent). */
	protected String	capa;
	
	//-------- constructors --------
	
	/**
	 *  Create a capability wrapper.
	 */
	public CapabilityWrapper(IInternalAccess agent, Object pojo, String capa)
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
	public <T> void addBeliefListener(String name, final IBeliefListener<T> listener)
	{
		name = capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+name: name;
		IBDIAgentFeature bdif = agent.getComponentFeature0(IBDIAgentFeature.class);
		if(bdif!=null)
		{
			bdif.addBeliefListener(name, listener);
		}
		else
		{
			IBDIXAgentFeature bdixf = agent.getComponentFeature0(IBDIXAgentFeature.class);
			if(bdixf.getBeliefbase().containsBelief(name))
			{
				bdixf.getBeliefbase().getBelief(name).addBeliefListener(listener);
			}
			else
			{
				bdixf.getBeliefbase().getBeliefSet(name).addBeliefSetListener(listener);
			}
		}
	}
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public <T> void removeBeliefListener(String name, IBeliefListener<T> listener)
	{
		name = capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+name: name;
		IBDIAgentFeature bdif = agent.getComponentFeature0(IBDIAgentFeature.class);
		if(bdif!=null)
		{
			bdif.removeBeliefListener(capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+name : name, listener);
		}
		else
		{
			IBDIXAgentFeature bdixf = agent.getComponentFeature0(IBDIXAgentFeature.class);
			if(bdixf.getBeliefbase().containsBelief(name))
			{
				bdixf.getBeliefbase().getBelief(name).removeBeliefListener(listener);
			}
			else
			{
				bdixf.getBeliefbase().getBeliefSet(name).removeBeliefSetListener(listener);
			}
		}
	}

	/**
	 *  Get the agent.
	 *  
	 *  Overridden to save the capability context within the used internal access.
	 */
	public IInternalAccess	getAgent()
	{
		return new InternalAccessAdapter(agent)
		{
			public <T> T getComponentFeature(Class<? extends T> type)
			{
				if(type.equals(IRequiredServicesFeature.class))
				{
					return (T)new RequiredServicesFeatureAdapter((IRequiredServicesFeature)super.getComponentFeature(type))
					{
						public String rename(String name)
						{
							return capa!=null? capa+MElement.CAPABILITY_SEPARATOR+name: name;
						}
					};
				}
				else
				{
					return super.getComponentFeature(type);
				}
			}
		};
//		return agent;
	}
	
//	/**
//	 *  Get the service container of the capability.
//	 */
//	public IServiceContainer	getServiceContainer()
//	{
//		return new ServiceContainerProxy(getInterpreter(), capa);
//	}

	/**
	 *  Get the pojo capability object.
	 */
	public Object	getPojoCapability()
	{
		if(pojo==null)
		{
			throw new UnsupportedOperationException("No pojo capability for XML agents.");
		}
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
	
//	/**
//	 *  Get the interpreter.
//	 */
//	protected BDIAgentInterpreter getInterpreter()
//	{
//		return (BDIAgentInterpreter)agent.getInterpreter();
//	}
}
