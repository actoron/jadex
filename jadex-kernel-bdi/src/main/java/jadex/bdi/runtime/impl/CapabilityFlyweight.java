package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.AgentRules;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IExpressionbase;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.IPropertybase;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IContext;
import jadex.bridge.IContextService;
import jadex.rules.state.IOAVState;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;

import java.util.logging.Logger;

/**
 *  Flyweight for a capability.
 */
public class CapabilityFlyweight extends ElementFlyweight implements ICapability
{
	//-------- attributes --------
	
	/** The agent handle. */
	protected Object agent;
	
	/** The agent adapter. */
	protected IComponentAdapter adapter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new capability flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param agent	The agent handle.
	 *  @param adapter	The agent adapter.
	 */
	public CapabilityFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
		this.agent = getInterpreter().getAgent();
		this.adapter = getInterpreter().getComponentAdapter();
	}
	
	//-------- methods concerning beliefs --------
	
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IExternalAccess getExternalAccess()
	{
		return new ExternalAccessFlyweight(getState(), getScope());
	}

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IBeliefbase getBeliefbase()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = BeliefbaseFlyweight.getBeliefbaseFlyweight(getState(), getScope());
				}
			};
			return (IBeliefbase)invoc.object;
		}
		else
		{
			return BeliefbaseFlyweight.getBeliefbaseFlyweight(getState(), getScope());
		}
	}

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IGoalbase getGoalbase()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = GoalbaseFlyweight.getGoalbaseFlyweight(getState(), getScope());
				}
			};
			return (IGoalbase)invoc.object;
		}
		else
		{
			return GoalbaseFlyweight.getGoalbaseFlyweight(getState(), getScope());
		}
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IPlanbase getPlanbase()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = PlanbaseFlyweight.getPlanbaseFlyweight(getState(), getScope());
				}
			};
			return (IPlanbase)invoc.object;
		}
		else
		{
			return PlanbaseFlyweight.getPlanbaseFlyweight(getState(), getScope());
		}
	}

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEventbase getEventbase()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = EventbaseFlyweight.getEventbaseFlyweight(getState(), getScope());
				}
			};
			return (IEventbase)invoc.object;
		}
		else
		{
			return EventbaseFlyweight.getEventbaseFlyweight(getState(), getScope());
		}
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IExpressionbase getExpressionbase()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = ExpressionbaseFlyweight.getExpressionbaseFlyweight(getState(), getScope());
				}
			};
			return (IExpressionbase)invoc.object;
		}
		else
		{
			return ExpressionbaseFlyweight.getExpressionbaseFlyweight(getState(), getScope());
		}
	}

	/**
	 *  Get the property base.
	 *  @return The property base.
	 */
	public IPropertybase getPropertybase()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = PropertybaseFlyweight.getPropertybaseFlyweight(getState(), getScope());
				}
			};
			return (IPropertybase)invoc.object;
		}
		else
		{
			return PropertybaseFlyweight.getPropertybaseFlyweight(getState(), getScope());
		}
	}

	/**
	 *  Register a subcapability.
	 *  @param subcap	The subcapability.
	 * /
	public void	registerSubcapability(IMCapabilityReference subcap)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Deregister a subcapability.
	 *  @param subcap	The subcapability.
	 * /
	public void	deregisterSubcapability(IMCapabilityReference subcap)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = AgentRules.getLogger(getState(), getScope());
				}
			};
			return (Logger)invoc.object;
		}
		else
		{
			return AgentRules.getLogger(getState(), getScope());
		}
	}

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public String getAgentName()
	{
		return adapter.getComponentIdentifier().getLocalName();
	}

	/**
	 * Get the configuration name.
	 * @return The configuration name.
	 */
	public String getConfigurationName()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = adapter.getComponentIdentifier();
				}
			};
			return (IComponentIdentifier)invoc.object;
		}
		else
		{
			return adapter.getComponentIdentifier();
		}
	}

	/**
	 *  Get the adapter agent.
	 *  @return The adapter agent.
	 */
	public IComponentAdapter	getAgentAdapter()
	{
		return adapter;
	}
	
	/**
	 *  Get the platform specific agent object.
	 *  Allows to do platform specific things.
	 *  @return The agent object.
	 */
	public Object	getPlatformComponent()
	{
		return adapter;
	}

	/**
	 *  Get the agent platform
	 *  @return The agent platform.
	 */
	public IServiceContainer	getServiceContainer()
	{
		return adapter.getServiceContainer();
	}
	
	/**
	 *  Get the current time.
	 *  The time unit depends on the currently running clock implementation.
	 *  For the default system clock, the time value adheres to the time
	 *  representation as used by {@link System#currentTimeMillis()}, i.e.,
	 *  the value of milliseconds passed since 0:00 'o clock, January 1st, 1970, UTC.
	 *  For custom simulation clocks, arbitrary representations can be used.
	 *  @return The current time.
	 */
	public long getTime()
	{
		// changed *.class to *.TYPE due to javaflow bug
		//		return ((IClockService)getAgentAdapter().getPlatform().getService(IClockService.class)).getTime();
		return ((IClockService)getAgentAdapter().getServiceContainer().getService(IClockService.TYPE)).getTime();
	}

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return getState().getTypeModel().getClassLoader();
	}
	
	/**
	 *  Kill the agent.
	 */
	public void killAgent()
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object cs = getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_state);
					if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING.equals(cs) 
						|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
					{
						getInterpreter().getComponentAdapter().killComponent();
					}
				}
			};
		}
		else
		{
			Object cs = getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_state);
			if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING.equals(cs) 
				|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
			{
				//	System.out.println("set to terminating");
				getInterpreter().startMonitorConsequences();
				getInterpreter().getComponentAdapter().killComponent();
				getInterpreter().endMonitorConsequences();
			}
		}
	}

	/**
	 *  Add an agent listener
	 *  @param listener The listener.
	 */
	public void addAgentListener(IAgentListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(listener)
			{
				public void run()
				{
					addEventListener(arg, agent);
				}
			};
		}
		else
		{
			addEventListener(listener, agent);
		}
	}
	/**
	 *  Add an agent listener
	 *  @param listener The listener.
	 */
	// changed signature for javaflow, removed final
	//public void removeAgentListener(final IAgentListener listener)
	public void removeAgentListener(IAgentListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(listener)
			{
				public void run()
				{
					addEventListener(arg, agent);
				}
			};
		}
		else
		{
			addEventListener(listener, agent);
		}
	}
	
	/**
	 *  Get the application context.
	 *  @return The application context (or null).
	 */
	public IApplicationContext getApplicationContext()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IContextService cs = (IContextService)adapter.getServiceContainer().getService(IContextService.class);
					if(cs!=null)
					{
						IContext[] tmp = cs.getContexts(getComponentIdentifier(), IApplicationContext.class);
						if(tmp!=null && tmp.length==1)
							object = tmp[0];
					}
				}
			};
			return (IApplicationContext)invoc.object;
		}
		else
		{
			IApplicationContext ret = null;
			IContextService cs = (IContextService)adapter.getServiceContainer().getService(IContextService.class);
			if(cs!=null)
			{
				IContext[] tmp = cs.getContexts(getComponentIdentifier(), IApplicationContext.class);
				if(tmp!=null && tmp.length==1)
					ret = (IApplicationContext)tmp[0];
			}
			return ret;
		}
	}
	
	//-------- element methods --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 * /
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MCapabilityFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MCapabilityFlyweight(getState(), mscope);
		}
	}*/
}
