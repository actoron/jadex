package jadex.bdi.runtime.impl.flyweights;

import jadex.base.Starter;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MCapabilityFlyweight;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IExpressionbase;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IFilter;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 *  Flyweight for a capability.
 */
public class CapabilityFlyweight extends ElementFlyweight implements ICapability, IBDIInternalAccess
{
	/** The capability model info (cached for synchronous access). */
	protected IModelInfo	model;
	
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
		this.model	= getBDIFeature().getModel(scope);
	}
	
	//-------- methods concerning beliefs --------
	
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IExternalAccess getExternalAccess()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new ExternalAccessFlyweight(getState(), getScope());
				}
			};
			return (IExternalAccess)invoc.object;
		}
		else
		{
			return new ExternalAccessFlyweight(getState(), getScope());
		}
	}

//	/**
//	 *  Get the parent (if any).
//	 *  @return The parent.
//	 */
//	public IExternalAccess getParentAccess()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					object = getInterpreter().getParent();
//				}
//			};
//			return (IExternalAccess)invoc.object;
//		}
//		else
//		{
//			return getInterpreter().getParent();
//		}
//	}

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IBeliefbase getBeliefbase()
	{
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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

//	/**
//	 *  Get the property base.
//	 *  @return The property base.
//	 */
//	public IPropertybase getPropertybase()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					object = PropertybaseFlyweight.getPropertybaseFlyweight(getState(), getScope());
//				}
//			};
//			return (IPropertybase)invoc.object;
//		}
//		else
//		{
//			return PropertybaseFlyweight.getPropertybaseFlyweight(getState(), getScope());
//		}
//	}

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
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = BDIAgentFeature.getInterpreter(getState())!=null? BDIAgentFeature.getInterpreter(getState()).getLogger(getScope()): Logger.getAnonymousLogger();
				}
			};
			return (Logger)invoc.object;
		}
		else
		{
			return BDIAgentFeature.getInterpreter(getState())!=null? BDIAgentFeature.getInterpreter(getState()).getLogger(getScope()): Logger.getAnonymousLogger();
		}
	}

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public String getAgentName()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = getInterpreter().getComponentIdentifier().getLocalName();
				}
			};
			return invoc.string;
		}
		else
		{
			return getInterpreter().getComponentIdentifier().getLocalName();
		}
		
	}
	
	/**
	 * Get the agent model.
	 * @return The agent model.
	 */
	public IModelInfo getAgentModel()
	{
		return getInterpreter().getModel();
	}

	/**
	 * Get the configuration name.
	 * @return The configuration name.
	 */
	public String getConfigurationName()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.capability_has_configuration);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.capability_has_configuration);
		}
	}

	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return getInterpreter().getComponentIdentifier();
	}
	
	/**
	 * Get the component description.
	 * @return The component description.
	 */
	public IComponentDescription	getComponentDescription()
	{
		return getInterpreter().getComponentDescription();
	}
	
	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return getInterpreter().getException();
	}
	
//	/**
//	 *  Get the platform specific agent object.
//	 *  Allows to do platform specific things.
//	 *  @return The agent object.
//	 */
//	public Object getPlatformComponent()
//	{
//		return getInterpreter().getAgentAdapter();
//	}

//	/**
//	 *  Get the service provider
//	 *  @return The service provider.
//	 */
//	public IServiceProvider getServiceProvider()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					object = getInterpreter().getServiceProvider();
//				}
//			};
//			return (IServiceProvider)invoc.object;
//		}
//		else
//		{
//			return getInterpreter().getServiceProvider();
//		}
//	}
	
//	/**
//	 *  Get the service container.
//	 *  @return The service container.
//	 */
//	public IInternalAccess getServiceContainer()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					object = new ServiceContainerProxy(getInterpreter(), getHandle());
//				}
//			};
//			return (IServiceContainer)invoc.object;
//		}
//		else
//		{
//			return new ServiceContainerProxy(getInterpreter(), getHandle());
//		}
//	}
	
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
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IClockService cs = SServiceProvider.getLocalService(getInterpreter(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
					longint = cs.getTime();
				}
			};
			return invoc.longint;
		}
		else
		{
			IClockService cs = SServiceProvider.getLocalService(getInterpreter(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			return cs.getTime();
//			return getInterpreter().getClockService().getTime();
		}
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(final long delay, final IComponentStep<T> step)
	{
		return waitForDelay(delay, step, false);
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(final long delay, final IComponentStep<T> step, final boolean realtime)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getComponentFeature(IExecutionFeature.class).waitForDelay(delay, step, realtime);
				}
			};
			return (IFuture<T>)invoc.object;
		}
		else
		{
			return getInterpreter().getComponentFeature(IExecutionFeature.class).waitForDelay(delay, step, realtime);
		}
	}

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public IFuture<Void> waitForDelay(final long delay)
	{
		return waitForDelay(delay, false);
	}

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public IFuture<Void> waitForDelay(final long delay, final boolean realtime)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getComponentFeature(IExecutionFeature.class).waitForDelay(delay, realtime);
				}
			};
			return (IFuture<Void>)invoc.object;
		}
		else
		{
			return getInterpreter().getComponentFeature(IExecutionFeature.class).waitForDelay(delay, realtime);
		}
	}

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		if(isExternalThread())
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
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object cs = getState().getAttributeValue(getBDIFeature().getAgent(), OAVBDIRuntimeModel.agent_has_state);
					if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
					{
						object = getInterpreter().killComponent();
					}
					else
					{
						object	= new Future(new RuntimeException("Component not running: "+getComponentIdentifier().getName()));
					}
				}
			};
			ret = (IFuture)invoc.object;
		}
		else
		{
			Object cs = getState().getAttributeValue(getBDIFeature().getAgent(), OAVBDIRuntimeModel.agent_has_state);
			if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
			{
				//	System.out.println("set to terminating");
				getBDIFeature().startMonitorConsequences();
				ret = getInterpreter().killComponent();
				getBDIFeature().endMonitorConsequences();
			}
			else
			{
				ret	= new Future(new RuntimeException("Component not running: "+getComponentIdentifier().getName()));
			}
		}
		return ret;
	}

//	/**
//	 *  Add an component listener
//	 *  @param listener The listener.
//	 */
//	public IFuture addComponentListener(IComponentListener listener)
//	{
//		final Future ret = new Future();
//		if(isExternalThread())
//		{
//			new AgentInvocation(listener)
//			{
//				public void run()
//				{
//					getState().addAttributeValue(getInterpreter().getAgent(), OAVBDIRuntimeModel.agent_has_componentlisteners, (IComponentListener) arg);
//					ret.setResult(null);
//				}
//			};
//		}
//		else
//		{
//			getState().addAttributeValue(getInterpreter().getAgent(), OAVBDIRuntimeModel.agent_has_componentlisteners, listener);
//			ret.setResult(null);
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Remove an agent listener
//	 *  @param listener The listener.
//	 */
//	public IFuture removeComponentListener(IComponentListener listener)
//	{
//		final Future ret = new Future();
//		if(isExternalThread())
//		{
//			new AgentInvocation(listener)
//			{
//				public void run()
//				{
//					removeComponentListener((IComponentListener) arg, getInterpreter().getAgent(), getState());
//					ret.setResult(null);
//				}
//			};
//		}
//		else
//		{
//			removeComponentListener(listener, getInterpreter().getAgent(), getState());
//			ret.setResult(null);
//		}
//		return ret;
//	}
//	
//	protected static void removeComponentListener(IComponentListener listener, Object agent, IOAVState state)
//	{
//		Object le = state.getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_componentlisteners, listener);
//		if (le == null)
//			throw new RuntimeException("Listener not found: "+listener);
//		state.removeAttributeValue(agent, OAVBDIRuntimeModel.agent_has_componentlisteners, listener);
//	}
	
	/**
	 *  Subscribe to component events.
	 *  @param filter An optional filter.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(final IFilter<IMonitoringEvent> filter, final boolean initial, final PublishEventLevel elm)
	{
		// No NoTimeoutFuture needed as is already created internally.
		final SubscriptionIntermediateDelegationFuture<IMonitoringEvent> ret = new SubscriptionIntermediateDelegationFuture<IMonitoringEvent>();
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISubscriptionIntermediateFuture<IMonitoringEvent> fut = getInterpreter().getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
						TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
						fut.addResultListener(lis);
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ISubscriptionIntermediateFuture<IMonitoringEvent> fut = getInterpreter().getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
			TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
			fut.addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return getInterpreter().getComponentFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener)
	{
		return getInterpreter().getComponentFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
//	/**
//	 *  Get the application context.
//	 *  @return The application context (or null).
//	 */
//	public IApplicationContext getApplicationContext()
//	{
//		if(isExternalThread())
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
		if(isExternalThread())
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
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		return model;
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
		if(isExternalThread())
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
		if(isExternalThread())
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
	 *  Wait for some time and execute a component step afterwards.
	 */
	public IFuture waitFor(final long delay, final IComponentStep step)
	{
		// todo: remember and cleanup timers in case of component removal.
		
		final Future ret = new Future();
		
		SServiceProvider.getService(getInterpreter(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						getBDIFeature().scheduleStep(step, getHandle()).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		return getInterpreter().getFetcher();
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getComponentFeature(IArgumentsFeature.class).getArguments();
				}
			};
			return (Map)invoc.object;
		}
		else
		{
			return getInterpreter().getComponentFeature(IArgumentsFeature.class).getArguments();
		}
	}
	
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public Map getResults()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getComponentFeature(IArgumentsFeature.class).getResults();
				}
			};
			return (Map)invoc.object;
		}
		else
		{
			return getInterpreter().getComponentFeature(IArgumentsFeature.class).getResults();
		}
	}
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(final String name, final Object value)
	{
		if(!getHandle().equals(getBDIFeature().getAgent()))
			throw new RuntimeException("Set result only allowed in agent, not in capabilities.");
		
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					if(SUtil.arrayToSet(SFlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope())).contains(name))
					{
						 IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name);
						 bel.setFact(value);
					}
					else if(SUtil.arrayToSet(SFlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope())).contains(name))
					{
						IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name);
						belset.removeFacts();
						if(SReflect.isIterable(value))
						{
							for(Iterator it=SReflect.getIterator(value); it.hasNext(); )
							{
								belset.addFact(it.next());
							}
						}
						else
						{
							belset.addFact(value);
						}
					}
					else
					{
						throw new RuntimeException("Unknown belief/set name: "+name);
					}
				}
			};
		}
		else
		{
			if(SUtil.arrayToSet(SFlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope())).contains(name))
			{
				 IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name);
				 bel.setFact(value);
			}
			else if(SUtil.arrayToSet(SFlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope())).contains(name))
			{
				IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name);
				belset.removeFacts();
				if(SReflect.isIterable(value))
				{
					for(Iterator it=SReflect.getIterator(value); it.hasNext(); )
					{
						belset.addFact(it.next());
					}
				}
				else
				{
					belset.addFact(value);
				}
			}
			else
			{
				throw new RuntimeException("Unknown belief/set name: "+name);
			}
		}
	}
	
	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.capability_has_configuration);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.capability_has_configuration);
		}
	}
	
	public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid)
	{
		return getInterpreter().getComponentFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(sid);
	}
	
	/**
	 *  Has the service a property provider.
	 */
	public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid)
	{
		return getInterpreter().getComponentFeature(INFPropertyComponentFeature.class).hasRequiredServicePropertyProvider(sid);
	}
	
	/**
	 *  Test if current thread is the component thread.
	 *  @return True if the current thread is the component thread.
	 */
	public boolean isComponentThread()
	{
		return !isExternalThread();
	}
	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public IFuture getRequiredService(String name)
//	{
//		return getRequiredService(name, false);
//	}
//	
//	/**
//	 *  Get a required services.
//	 *  @return The services.
//	 */
//	public IIntermediateFuture getRequiredServices(String name)
//	{
//		return getRequiredServices(name, false);
//	}
//	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public IFuture getRequiredService(final String name, final boolean rebind)
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
//					RequiredServiceBinding binding = getRequiredServiceBinding(name);
//					if(info==null)
//					{
//						Future ret = new Future();
//						ret.setException(new IllegalArgumentException("Info must not null."));
//						object = ret;
//					}
//					else
//					{
//						object = getInterpreter().getServiceContainer().getRequiredService(info, binding, rebind);
//					}
//				}
//			};
//			return (IFuture)invoc.object;
//		}
//		else
//		{
//			IFuture ret;
//			RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
//			RequiredServiceBinding binding = getRequiredServiceBinding(name);
//			if(info==null)
//			{
//				Future fut = new Future();
//				fut.setException(new IllegalArgumentException("Info must not null."));
//				ret = fut;
//			}
//			else
//			{
//				ret = getInterpreter().getServiceContainer().getRequiredService(info, binding, rebind);
//			}
//			return ret;
//		}
//	}
//	
//	/**
//	 *  Get a required services.
//	 *  @return The services.
//	 */
//	public IIntermediateFuture getRequiredServices(final String name, final boolean rebind)
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
//					RequiredServiceBinding binding = getRequiredServiceBinding(name);
//					if(info==null)
//					{
//						IntermediateFuture ret = new IntermediateFuture();
//						ret.setException(new IllegalArgumentException("Info must not null."));
//						object = ret;
//					}
//					else
//					{
//						object = getInterpreter().getServiceContainer().getRequiredServices(info, binding, rebind);
//					}
//				}
//			};
//			return (IIntermediateFuture)invoc.object;
//		}
//		else
//		{
//			IIntermediateFuture ret;
//			RequiredServiceInfo info = getInterpreter().getModel().getModelInfo().getRequiredService(name);
//			RequiredServiceBinding binding = getRequiredServiceBinding(name);
//			if(info==null)
//			{
//				IntermediateFuture fut = new IntermediateFuture();
//				fut.setException(new IllegalArgumentException("Info must not null."));
//				ret = fut;
//			}
//			else
//			{
//				ret = getInterpreter().getServiceContainer().getRequiredServices(info, binding, rebind);
//			}
//			return ret;
//		}
//	}
//	
//	/**
//	 *  Get the binding info of a service.
//	 *  @param name The required service name.
//	 *  @return The binding info of a service.
//	 */
//	protected RequiredServiceBinding getRequiredServiceBinding(String name)
//	{
//		Object agent = BDIAgentFeature.getInterpreter(getState()).getAgent();
//		Map bindings = (Map)getState().getAttributeValue(agent, OAVBDIRuntimeModel.agent_has_bindings);
//		return bindings!=null? (RequiredServiceBinding)bindings.get(name): null;
//	}
	
	/**
	 *  Publish a monitoring event. This event is automatically send
	 *  to the monitoring service of the platform (if any). 
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event, PublishTarget pt)
	{
		return getInterpreter().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(event, pt);
	}
	
	public boolean hasEventTargets(PublishTarget pt, PublishEventLevel pi) 
	{
		return getInterpreter().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(pt, pi);
	}
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(final String type)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getChildren(type);
				}
			};
			return new Future<IComponentIdentifier[]>((IComponentIdentifier[])invoc.object);
		}
		else
		{
			return getInterpreter().getChildren(type);
		}
	}
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @return The children component identifiers.
	 */
	public <T> T getComponentFeature(final Class< ? extends T> type)
	{
		return getInterpreter().getComponentFeature(type);
	}
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @return The children component identifiers.
	 */
	public <T> T getComponentFeature0(final Class< ? extends T> type)
	{
		return getInterpreter().getComponentFeature0(type);
	}
	
	public IParameterGuesser getParameterGuesser()
	{
		return getInterpreter().getParameterGuesser();
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent(Exception e)
	{
		return getInterpreter().killComponent(e);
	}

	public Object getPlatformComponent()
	{
		return getInterpreter();
	}
}
