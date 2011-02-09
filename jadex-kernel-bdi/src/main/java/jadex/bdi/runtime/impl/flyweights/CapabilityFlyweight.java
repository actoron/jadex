package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MCapabilityFlyweight;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IExpressionbase;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.IPropertybase;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 *  Flyweight for a capability.
 */
public class CapabilityFlyweight extends ElementFlyweight implements ICapability, IBDIInternalAccess
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
		this.adapter = getInterpreter().getAgentAdapter();
	}
	
	//-------- methods concerning beliefs --------
	
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IBDIExternalAccess getExternalAccess()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new ExternalAccessFlyweight(getState(), getScope());
				}
			};
			return (IBDIExternalAccess)invoc.object;
		}
		else
		{
			return new ExternalAccessFlyweight(getState(), getScope());
		}
	}

	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IExternalAccess getParent()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getParent();
				}
			};
			return (IExternalAccess)invoc.object;
		}
		else
		{
			return getInterpreter().getParent();
		}
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
					object = BDIInterpreter.getInterpreter(getState()).getLogger(getScope());
				}
			};
			return (Logger)invoc.object;
		}
		else
		{
			return BDIInterpreter.getInterpreter(getState()).getLogger(getScope());
		}
	}

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public String getAgentName()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = adapter.getComponentIdentifier().getLocalName();
				}
			};
			return invoc.string;
		}
		else
		{
			return adapter.getComponentIdentifier().getLocalName();
		}
		
	}
	
	/**
	 * Get the agent model.
	 * @return The agent model.
	 */
	public IModelInfo getAgentModel()
	{
		return getInterpreter().getModel().getModelInfo();
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
		// Todo: synchronization across components?
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
	 *  Get the platform specific agent object.
	 *  Allows to do platform specific things.
	 *  @return The agent object.
	 */
	public Object getPlatformComponent()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = adapter;
				}
			};
			return invoc.object;
		}
		else
		{
			return adapter;
		}
	}

	/**
	 *  Get the agent platform
	 *  @return The agent platform.
	 */
	public IServiceProvider getServiceProvider()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getServiceProvider();
				}
			};
			return (IServiceProvider)invoc.object;
		}
		else
		{
			return getInterpreter().getServiceProvider();
		}
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					longint = getInterpreter().getClockService().getTime();
				}
			};
			return invoc.longint;
		}
		else
		{
			return getInterpreter().getClockService().getTime();
		}
	}

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getTypeModel().getClassLoader();
				}
			};
			return (ClassLoader)invoc.object;
		}
		else
		{
			return getState().getTypeModel().getClassLoader();
		}
	}
	
	/**
	 *  Kill the agent.
	 */
	public IFuture killAgent()
	{
		IFuture ret = null;
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object cs = getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_state);
					if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING0.equals(cs) 
						|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING1.equals(cs)
						|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
					{
						object = getInterpreter().killAgent();
					}
				}
			};
			ret = (IFuture)invoc.object;
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
				ret = getInterpreter().killAgent();
				getInterpreter().endMonitorConsequences();
			}
		}
		return ret;
	}

	/**
	 *  Add an component listener
	 *  @param listener The listener.
	 */
	public void addComponentListener(IComponentListener listener)
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
	public void removeComponentListener(IComponentListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(listener)
			{
				public void run()
				{
					removeEventListener(arg, agent, false);
				}
			};
		}
		else
		{
			removeEventListener(listener, agent, false);
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
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return getInterpreter().createResultListener(listener);
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener)
	{
		return getInterpreter().createResultListener(listener);
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
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getAgentAdapter().getChildrenAccesses();
				}
			};
			return (IFuture)invoc.object;
		}
		else
		{
			return getInterpreter().getAgentAdapter().getChildrenAccesses();
		}
	}
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		// todo: return fitting capability model info.
		return getInterpreter().getModel().getModelInfo();
	}

	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		return killAgent();
	}

	/**
	 *  Get subcapability names.
	 *  @return The future with array of subcapability names.
	 */
	public String[]	getSubcapabilityNames()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					String[] res = SUtil.EMPTY_STRING_ARRAY;
					Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_subcapabilities);
					if(coll!=null)
					{
						res = new String[coll.size()];
						int i=0;
						for(Iterator it=coll.iterator(); it.hasNext(); i++)
						{
							Object cref = it.next();
							String name = (String)getState().getAttributeValue(cref, OAVBDIRuntimeModel.capabilityreference_has_name);
							res[i] = name;
						}
					}
					this.sarray	= res;
				}
			};
			return invoc.sarray;
		}
		else
		{
			String[] res = SUtil.EMPTY_STRING_ARRAY;
			Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_subcapabilities);
			if(coll!=null)
			{
				res = new String[coll.size()];
				int i=0;
				for(Iterator it=coll.iterator(); it.hasNext(); i++)
				{
					Object cref = it.next();
					String name = (String)getState().getAttributeValue(cref, OAVBDIRuntimeModel.capabilityreference_has_name);
					res[i] = name;
				}
			}
			return res;
		}
	}

	/**
	 *  Get external access of subcapability.
	 *  @param name The capability name.
	 *  @return The future with external access.
	 */
	public ICapability	getSubcapability(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					StringTokenizer stok = new StringTokenizer(name, ".");
					Object handle = getHandle();
					while(stok.hasMoreTokens())
					{
						String subcapname = stok.nextToken();
						Object subcapref = getState().getAttributeValue(handle, OAVBDIRuntimeModel.capability_has_subcapabilities, subcapname);
						if(subcapref==null)
						{
							throw new RuntimeException("Capability not found: "+subcapname);
						}
						handle = getState().getAttributeValue(subcapref, OAVBDIRuntimeModel.capabilityreference_has_capability);
					}
					this.object	= new CapabilityFlyweight(getState(), handle);
				}
			};
			return (ICapability)invoc.object;
		}
		else
		{
			StringTokenizer stok = new StringTokenizer(name, ".");
			Object handle = getHandle();
			while(stok.hasMoreTokens())
			{
				String subcapname = stok.nextToken();
				Object subcapref = getState().getAttributeValue(handle, OAVBDIRuntimeModel.capability_has_subcapabilities, subcapname);
				if(subcapref==null)
				{
					throw new RuntimeException("Capability not found: "+subcapname);
				}
				handle = getState().getAttributeValue(subcapref, OAVBDIRuntimeModel.capabilityreference_has_capability);
			}
			return new CapabilityFlyweight(getState(), handle);
		}
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name)
	{
		return getRequiredService(name, false);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(String name)
	{
		return getRequiredServices(name, false);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(final String name, final boolean rebind)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
					if(info==null)
					{
						Future ret = new Future();
						ret.setException(new IllegalArgumentException("Info must not null."));
						object = ret;
					}
					else
					{
						object = getInterpreter().getServiceContainer().getRequiredService(info, rebind);
					}
				}
			};
			return (IFuture)invoc.object;
		}
		else
		{
			IFuture ret;
			RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
			if(info==null)
			{
				Future fut = new Future();
				fut.setException(new IllegalArgumentException("Info must not null."));
				ret = fut;
			}
			else
			{
				ret = getInterpreter().getServiceContainer().getRequiredService(info, rebind);
			}
			return ret;
		}
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(final String name, final boolean rebind)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
					if(info==null)
					{
						IntermediateFuture ret = new IntermediateFuture();
						ret.setException(new IllegalArgumentException("Info must not null."));
						object = ret;
					}
					else
					{
						object = getInterpreter().getServiceContainer().getRequiredServices(info, rebind);
					}
				}
			};
			return (IIntermediateFuture)invoc.object;
		}
		else
		{
			IIntermediateFuture ret;
			RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
			if(info==null)
			{
				IntermediateFuture fut = new IntermediateFuture();
				fut.setException(new IllegalArgumentException("Info must not null."));
				ret = fut;
			}
			else
			{
				ret = getInterpreter().getServiceContainer().getRequiredServices(info, rebind);
			}
			return ret;
		}
	}
}
