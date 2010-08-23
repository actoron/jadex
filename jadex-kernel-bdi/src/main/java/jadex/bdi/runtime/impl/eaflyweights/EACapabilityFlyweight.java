package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MCapabilityFlyweight;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEABeliefbase;
import jadex.bdi.runtime.IEACapability;
import jadex.bdi.runtime.IEAEventbase;
import jadex.bdi.runtime.IEAExpressionbase;
import jadex.bdi.runtime.IEAGoalbase;
import jadex.bdi.runtime.IEAPlanbase;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.rules.state.IOAVState;

import java.util.logging.Logger;

/**
 *  Flyweight for a capability.
 */
public abstract class EACapabilityFlyweight extends ElementFlyweight implements IEACapability
{
	//-------- attributes --------
	
	/** The agent handle. */
	protected Object agent;
	
	/** The agent adapter. */
	protected IComponentAdapter adapter;
	
	/** The belief base. */
	protected IEABeliefbase	beliefbase;
	
	/** The goal base. */
	protected IEAGoalbase	goalbase;
	
	/** The plan base. */
	protected IEAPlanbase	planbase;
	
	/** The event base. */
	protected IEAEventbase	eventbase;
	
	/** The expression base. */
	protected IEAExpressionbase	expressionbase;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The component identifier. */
	protected IComponentIdentifier	cid;
	
	/** The service container. */
	protected IServiceProvider provider;
	
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
		this.beliefbase	= EABeliefbaseFlyweight.getBeliefbaseFlyweight(getState(), getScope());
		this.goalbase	= EAGoalbaseFlyweight.getGoalbaseFlyweight(getState(), getScope());
		this.planbase	= EAPlanbaseFlyweight.getPlanbaseFlyweight(getState(), getScope());
		this.eventbase	= EAEventbaseFlyweight.getEventbaseFlyweight(getState(), getScope());
		this.expressionbase	= EAExpressionbaseFlyweight.getExpressionbaseFlyweight(getState(), getScope());
		this.logger	= BDIInterpreter.getInterpreter(getState()).getLogger(getScope());
		this.cid	= adapter.getComponentIdentifier();
		this.provider = getInterpreter().getServiceProvider();
	}
	
	//-------- methods concerning beliefs --------
	
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IBDIExternalAccess	getExternalAccess()
	{
		return (IBDIExternalAccess)this;
	}

	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IComponentIdentifier	getParent()
	{
		return getInterpreter().getParent().getComponentIdentifier();
	}

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IEABeliefbase	getBeliefbase()
	{
		return beliefbase;
	}

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IEAGoalbase getGoalbase()
	{
		return goalbase;
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IEAPlanbase	getPlanbase()
	{
		return planbase;
	}

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEAEventbase	getEventbase()
	{
		return eventbase;
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IEAExpressionbase getExpressionbase()
	{
		return expressionbase;
	}

	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return logger;
	}

	/**
	 * Get the component name.
	 * @return The component name.
	 */
	public String	getComponentName()
	{
		return getComponentIdentifier().getLocalName();
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
	public IComponentIdentifier	getComponentIdentifier()
	{
		return cid;
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
	public IServiceProvider getServiceProvider()
	{
		return provider;
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
					ret.setResult(getInterpreter().getClockService().getTime());
				}
			});
		}
		else
		{
			ret.setResult(getInterpreter().getClockService().getTime());
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
	
	// todo: remove, duplicate method since killComponent is in external access
	/**
	 *  Kill the agent.
	 */
	public IFuture killAgent()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					Object cs = getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_state);
					if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING0.equals(cs) 
						|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1.equals(cs)
						|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
					{
						getInterpreter().killAgent().addResultListener(new DelegationResultListener(ret));
					}
				}
			});
		}
		else
		{
			Object cs = getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_state);
			if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING0.equals(cs) 
				|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1.equals(cs)
				|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
			{
				//	System.out.println("set to terminating");
				getInterpreter().startMonitorConsequences();
				getInterpreter().killAgent().addResultListener(new DelegationResultListener(ret));
				getInterpreter().endMonitorConsequences();
			}
		}
		
		return ret;
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
	
	//-------- element methods --------
	
	/**
	 *  Get the adapter agent.
	 *  @return The adapter agent.
	 */
	public IComponentAdapter getAgentAdapter()
	{
		return adapter;
	}
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					object = new MCapabilityFlyweight(getState(), me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return new MCapabilityFlyweight(getState(), me);
		}
	}
}
