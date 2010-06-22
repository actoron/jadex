package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IEACapability;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentAdapter;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;
import jadex.service.clock.IClockService;

/**
 *  Flyweight for a capability.
 */
public class EACapabilityFlyweight extends ElementFlyweight implements IEACapability
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
	public EACapabilityFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
		this.agent = getInterpreter().getAgent();
		this.adapter = getInterpreter().getAgentAdapter();
	}
	
	//-------- methods concerning beliefs --------
	
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IFuture getExternalAccess()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(new ExternalAccessFlyweight(getState(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(new ExternalAccessFlyweight(getState(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IFuture getParent()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getInterpreter().getParent());
				}
			});
		}
		else
		{
			ret.setResult(getInterpreter().getParent());
		}
		
		return ret;
	}

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IFuture getBeliefbase()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(EABeliefbaseFlyweight.getBeliefbaseFlyweight(getState(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(EABeliefbaseFlyweight.getBeliefbaseFlyweight(getState(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IFuture getGoalbase()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(EAGoalbaseFlyweight.getGoalbaseFlyweight(getState(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(EAGoalbaseFlyweight.getGoalbaseFlyweight(getState(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IFuture getPlanbase()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(EAPlanbaseFlyweight.getPlanbaseFlyweight(getState(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(EAPlanbaseFlyweight.getPlanbaseFlyweight(getState(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IFuture getEventbase()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(EAEventbaseFlyweight.getEventbaseFlyweight(getState(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(EAEventbaseFlyweight.getEventbaseFlyweight(getState(), getScope()));
		}
		
		return ret;
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IFuture getExpressionbase()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(EAExpressionbaseFlyweight.getExpressionbaseFlyweight(getState(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(EAExpressionbaseFlyweight.getExpressionbaseFlyweight(getState(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Get the property base.
	 *  @return The property base.
	 */
	public IFuture getPropertybase()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(EAPropertybaseFlyweight.getPropertybaseFlyweight(getState(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(EAPropertybaseFlyweight.getPropertybaseFlyweight(getState(), getScope()));
		}
		
		return ret;
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
	public IFuture getLogger()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(BDIInterpreter.getInterpreter(getState()).getLogger(getScope()));
				}
			});
		}
		else
		{
			ret.setResult(BDIInterpreter.getInterpreter(getState()).getLogger(getScope()));
		}
		
		return ret;
	}

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public IFuture getAgentName()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(adapter.getComponentIdentifier().getLocalName());
				}
			});
		}
		else
		{
			ret.setResult(adapter.getComponentIdentifier().getLocalName());
		}
		
		return ret;
	}

	/**
	 * Get the configuration name.
	 * @return The configuration name.
	 */
	public IFuture getConfigurationName()
	{
		throw new UnsupportedOperationException();
		
//		final Future ret = new Future();
//		
//		if(getInterpreter().isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.capability_has_configuration));
//				}
//			};
//		}
//		else
//		{
//			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.capability_has_configuration));
//		}
//		
//		return ret;
	}

	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IFuture getComponentIdentifier()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(adapter.getComponentIdentifier());
				}
			});
		}
		else
		{
			ret.setResult(adapter.getComponentIdentifier());
		}
		
		return ret;
	}
	
	/**
	 *  Get the platform specific agent object.
	 *  Allows to do platform specific things.
	 *  @return The agent object.
	 */
	public IFuture getPlatformComponent()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(adapter);
				}
			});
		}
		else
		{
			ret.setResult(adapter);
		}
		
		return ret;
	}

	/**
	 *  Get the agent platform
	 *  @return The agent platform.
	 */
	public IFuture getServiceContainer()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(adapter.getServiceContainer());
				}
			});
		}
		else
		{
			ret.setResult(adapter.getServiceContainer());
		}
		
		return ret;
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
	public IFuture getTime()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(((IClockService)getAgentAdapter().getServiceContainer().getService(IClockService.TYPE)).getTime());
				}
			});
		}
		else
		{
			ret.setResult(((IClockService)getAgentAdapter().getServiceContainer().getService(IClockService.TYPE)).getTime());
		}
		
		return ret;
	}

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public IFuture getClassLoader()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getTypeModel().getClassLoader());
				}
			});
		}
		else
		{
			ret.setResult(getState().getTypeModel().getClassLoader());
		}
		
		return ret;
	}
	
	/**
	 *  Kill the agent.
	 */
	public void killAgent()
	{
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					Object cs = getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_state);
					if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING.equals(cs) 
						|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
					{
						getInterpreter().killAgent();
					}
				}
			});
		}
		else
		{
			Object cs = getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_state);
			if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING.equals(cs) 
				|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
			{
				//	System.out.println("set to terminating");
				getInterpreter().startMonitorConsequences();
				getInterpreter().killAgent();
				getInterpreter().endMonitorConsequences();
			}
		}
	}

	/**
	 *  Add an agent listener
	 *  @param listener The listener.
	 */
	public void addAgentListener(final IAgentListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					addEventListener(listener, agent);
				}
			});
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
	public void removeAgentListener(final IAgentListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					addEventListener(listener, agent);
				}
			});
		}
		else
		{
			addEventListener(listener, agent);
		}
	}
	
//	/**
//	 *  Get the application context.
//	 *  @return The application context (or null).
//	 */
//	public IApplicationContext getApplicationContext()
//	{
//		if(getInterpreter().isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					IContextService cs = (IContextService)adapter.getServiceContainer().getService(IContextService.class);
//					if(cs!=null)
//					{
//						IContext[] tmp = cs.getContexts(getComponentIdentifier(), IApplicationContext.class);
//						if(tmp!=null && tmp.length==1)
//							object = tmp[0];
//					}
//				}
//			};
//			return (IApplicationContext)invoc.object;
//		}
//		else
//		{
//			IApplicationContext ret = null;
//			IContextService cs = (IContextService)adapter.getServiceContainer().getService(IContextService.class);
//			if(cs!=null)
//			{
//				IContext[] tmp = cs.getContexts(getComponentIdentifier(), IApplicationContext.class);
//				if(tmp!=null && tmp.length==1)
//					ret = (IApplicationContext)tmp[0];
//			}
//			return ret;
//		}
//	}
	
	//-------- element methods --------
	
	/**
	 *  Get the adapter agent.
	 *  @return The adapter agent.
	 */
	public IComponentAdapter getAgentAdapter()
	{
		return adapter;
	}
	
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
