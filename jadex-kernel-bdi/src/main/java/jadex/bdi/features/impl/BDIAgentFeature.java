package jadex.bdi.features.impl;


import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.OAVCapabilityModel;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IExpressionbase;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.IPropertybase;
import jadex.bdi.runtime.impl.GoalDelegationHandler;
import jadex.bdi.runtime.impl.JavaStandardPlanExecutor;
import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BeliefInfo;
import jadex.bdi.runtime.interpreter.EventProcessingRules;
import jadex.bdi.runtime.interpreter.EventReificator;
import jadex.bdi.runtime.interpreter.ExternalAccessRules;
import jadex.bdi.runtime.interpreter.GoalDeliberationRules;
import jadex.bdi.runtime.interpreter.GoalInfo;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.GoalProcessingRules;
import jadex.bdi.runtime.interpreter.ListenerRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanInfo;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.bridge.BulkMonitoringEvent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.SFuture;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.ServiceGetter;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.ISynchronizator;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.PriorityAgenda;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


/**
 *  Main entry point for the reasoning engine
 *  holding the relevant agent data structure
 *  and performing the agent execution when
 *  being called from the platform.
 */
public class BDIAgentFeature extends AbstractComponentFeature implements IBDIAgentFeature, IInternalBDIAgentFeature
{
	//-------- static part --------
	
	/** The interpreters, one per agent (ragent -> interpreter). */
	// Hack e.g. for fetching agent-dependent plan executors
	public static final Map<IOAVState, IInternalAccess> interpreters;
	
	//-------- attributes --------
	
	//-------- reinit on init --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The reference to the agent instance in the state.*/
	protected Object ragent;
	
	/** The agent model. */
	protected OAVAgentModel model;
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
//	/** The platform adapter for the agent. */
//	protected IComponentAdapter	adapter;
	
	/** The parent of the agent (if any). */
//	protected IExternalAccess	parent;
	
//	/** The kernel properties. */
//	protected Map kernelprops;
	
//	/** The extensions. */
//	protected Map<String, IExtensionInstance> extensions;
	
//	/** The properties. */
//	protected Map<String, Object> properties;
	
	/** The arguments. */
	protected Map<String, Object> arguments;
	
	/** The results. */
	protected Map<String, Object> results;

//	/** The external service bindings. */
//	protected RequiredServiceBinding[] bindings;
	
//	/** The external service infos. */
//	protected ProvidedServiceInfo[] pinfos;
	
	//-------- recreate on init (no state) --------
	
	/** The event reificator creates changeevent objects for relevant state changes. */
	protected EventReificator reificator;
	
	/** The clock service. */
	//hack???
//	protected IClockService	clockservice;
	
	/** The component management service. */
	//hack???
//	protected IComponentManagementService	cms;
	
	/** The message service. */
	//hack???
//	protected IMessageService	msgservice;
	
	/** The subscriptions (subscription future -> subscription info). */
	protected Map<SubscriptionIntermediateFuture<IMonitoringEvent>, Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel>> subscriptions;
	
	/** The result listener. */
//	protected IIntermediateResultListener<Tuple2<String, Object>> resultlistener;
	protected SubscriptionIntermediateFuture<Tuple2<String, Object>> cmssub;
	protected List<SubscriptionIntermediateFuture<Tuple2<String, Object>>> resultsubscriptions;
	
	/** The event emit level for subscriptions. */
	protected PublishEventLevel emitlevelsub;
	
	//-------- null on init --------
	
	/** The atomic state. */
	protected transient boolean atomic;
	
	/** The currently executed plan (or null for none). */
	protected transient Object currentplan;
	
	/** The flag indicating whether agenda changes are monitored. */
	protected transient int monitor_consequences;
	
	/** The agenda state when monitoring was started. */
	protected transient int agenda_state;
	
	/** The flag for microplansteps. */
	protected transient boolean microplansteps;
		
	/** The map of flyweights (original element -> flyweight). */
	protected Map volcache;
	protected Map stacache;
	
	protected static final Set stacacheelems;
	
	static
	{
		stacacheelems = new HashSet();
		stacacheelems.add(IBeliefbase.class);
		stacacheelems.add(IGoalbase.class);
		stacacheelems.add(IPlanbase.class);
		stacacheelems.add(IEventbase.class);
		stacacheelems.add(IExpressionbase.class);
		stacacheelems.add(IPropertybase.class);
		stacacheelems.add(ICapability.class);
		stacacheelems.add(IExternalAccess.class);
		stacacheelems.add(IBelief.class);
		stacacheelems.add(IBeliefSet.class);
	}
	
	/** The nf property providers for required services. */
	protected Map<IServiceIdentifier, INFMixedPropertyProvider> reqserprops;
	
	//-------- refactor --------
	
	/** The plan executor. */
	protected Map planexecutors;
	
	/** The service container. */
	protected IInternalAccess container;
	
//	/** The cms future for init return. */
//	protected Future inited;
	
	/** The cached external access. */
	protected IExternalAccess ea;
	
	/** The currently inited mcapability. */
	protected Object	initcapa;
	
//	/** The parameter copy flag. */
//	protected boolean copy;

//	/** The realtime local timeout flag. */
//	protected boolean realtime;

	/** The monitoring service getter. */
	protected ServiceGetter<IMonitoringService> getter;
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDIAgentFeature(IInternalAccess component, final ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		
		// Create type model for agent instance (e.g. holding dynamically loaded java classes).
		this.model	= (OAVAgentModel)getComponent().getModel().getRawModel();
		OAVTypeModel tmodel	= new OAVTypeModel(getComponent().getComponentDescription().getName().getLocalName()+"_typemodel", model.getState().getTypeModel().getClassLoader());
		tmodel.addTypeModel(model.getState().getTypeModel());
		tmodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
		this.state	= OAVStateFactory.createOAVState(tmodel); 
		state.addSubstate(model.getState());
			
//		this.parent	= parent;
//		this.kernelprops = kernelprops;
		this.planexecutors = new HashMap();
		this.volcache = new LRU(0);	// 50
		this.stacache = new LRU(20);
		this.microplansteps = true;
//		this.copy = copy;
//		this.realtime = realtime;
//		this.resultlistener = resultlistener;
//		this.inited = inited;
		this.emitlevelsub = PublishEventLevel.OFF;
		
		// Hack! todo:
		interpreters.put(state, getComponent());
		
//		System.out.println("arguments: "+adapter.getComponentIdentifier().getName()+" "+arguments);
		
		state.setSynchronizator(new ISynchronizator()
		{
			public boolean isExternalThread()
			{
				return !getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
			}
			
			public void invokeSynchronized(Runnable code)
			{
				BDIAgentFeature.this.invokeSynchronized(code);
			}
			
			public void invokeLater(final Runnable action)
			{
				getComponent().getComponentFeature(IExecutionFeature.class)
					.scheduleStep(new ImmediateComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						action.run();
						return IFuture.DONE;
					}
				});
			}
		});
		
//		// Evaluate arguments if necessary
//		// Hack! use constant
//		if(arguments!=null && arguments.get("evaluation_language")!=null)
//		{
//			arguments.remove("evaluation_language");
//			// todo: support more than Java language parsers
//			JavaCCExpressionParser parser = new JavaCCExpressionParser();
//			for(Iterator it=arguments.keySet().iterator(); it.hasNext(); )
//			{
//				Object key = it.next();
//				try
//				{
//					IParsedExpression pex = parser.parseExpression((String)arguments.get(key), null, null, state.getTypeModel().getClassLoader());
//					Object val = pex.getValue(null);
//					arguments.put(key, val);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//					throw new RuntimeException("Could not evaluate argument: "+key);
//				}
//			}
//		}
		
		// Set up initial state of agent
		ragent = state.createRootObject(OAVBDIRuntimeModel.agent_type);
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model, model.getHandle());
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.capability_has_configuration, cinfo.getConfiguration());
		if(cinfo.getArguments()!=null && !cinfo.getArguments().isEmpty())
			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments, cinfo.getArguments());
//		this.bindings	= bindings;
//		this.pinfos	= pinfos;

//		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING0);
		
		reificator	= new EventReificator(state, ragent);
		
		// Initialize rule system.
		rulesystem = new RuleSystem(state, model.getMatcherFunctionality().getRulebase(), model.getMatcherFunctionality(), new PriorityAgenda());		
	}
	
	/**
	 *  Init the agent.
	 */
	public IFuture<Void> init()
	{
		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
		
//		if(kernelprops!=null)
//		{
//			Boolean mps = (Boolean)kernelprops.get("microplansteps");
//			if(mps!=null)
//				microplansteps = mps.booleanValue();
//		}
		microplansteps = true;
		
		// Init the external access
//		this.adapter = factory.createComponentAdapter(desc, model.getModelInfo(), this, parent);
//		this.container = createServiceContainer();
		this.ea = new ExternalAccessFlyweight(state, ragent);

		getter = new ServiceGetter<IMonitoringService>(getInternalAccess(), IMonitoringService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		
		return initCapability(this.model,cinfo.getConfiguration());
	}
	
	/**
	 *  Start the component behavior.
	 */
	public IFuture<Void> body()
	{
		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();

		Future<Void> ret = new Future<Void>();
		
//		System.out.println("sb start: "+getComponentIdentifier());
		
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, null);
		
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE);

		rulesystem.init();
		
		Map parents = new HashMap(); 
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, parents);
		AgentRules.createCapabilityInstance(state, ragent, parents);
		
		AgentRules.initializeCapabilityInstance(state, ragent).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// Remove arguments from state.
				if(state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments)!=null) 
					state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments, null);
				super.customResultAvailable(result);
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Called before blocking the component thread.
//	 */
//	public void	beforeBlock()
//	{
//	}
//	
//	/**
//	 *  Called after unblocking the component thread.
//	 */
//	public void	afterBlock()
//	{
//	}
	
	/**
	 *  Init the component portion of a capability.
	 */
	protected IFuture<Void>	initCapability(final OAVCapabilityModel oavmodel, final String config)
	{
		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
		
		final Future<Void>	ret	= new Future<Void>();
//		initcapa	= oavmodel.getHandle();
//		BDIInterpreter.super.init(oavmodel.getModelInfo(), config, null)
//			.addResultListener(createResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				initcapa	= null;
				Collection subcaps	= state.getAttributeValues(oavmodel.getHandle(), OAVBDIMetaModel.capability_has_capabilityrefs);
				if(subcaps!=null)
				{
					final Iterator it	= subcaps.iterator();
					IResultListener<Void>	lis	= new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result)
						{
							if(it.hasNext())
							{
								Object	mcaparef	= it.next();
								String	subconf	= null;
								if(config!=null)
								{
									Object	mconfig	= state.getAttributeValue(oavmodel.getHandle(), OAVBDIMetaModel.capability_has_configurations, config);
									Object	inicap	= AgentRules.getInitialCapability(state, oavmodel.getHandle(), mconfig, mcaparef);
									if(inicap!=null)
									{
										subconf	= (String) state.getAttributeValue(inicap, OAVBDIMetaModel.initialcapability_has_configuration);
									}
								}
								Object	mcapa	= state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability); 
								OAVCapabilityModel	submodel	= oavmodel.getSubcapabilityModel(mcapa);
								initCapability(submodel, subconf).addResultListener(getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(this));
							}
							else
							{
								super.customResultAvailable(null);
							}
						}
					};
					lis.resultAvailable(null);
				}
				else
				{
					ret.setResult(null);
				}
//			}
//		}));
//		
		return ret;
	}

//	/**
//	 *  Overridden to init BDI internals before services.
//	 */
//	public IFuture initProvidedServices(final IModelInfo model, final String config)
//	{
////		assert isAgentThread();
//		assert !getAgentAdapter().isExternalThread();
//		
//		IFuture	ret;
//		if(model==getModel())
//		{
//			final Future	fut	= new Future();
//			ret	= fut;
//			// Agent model: init agent stuff first.
//			init0().addResultListener(createResultListener(new DelegationResultListener(fut)
//			{
//				public void customResultAvailable(Object result)
//				{
//					init1().addResultListener(createResultListener(new DelegationResultListener(fut)
//					{
//						public void customResultAvailable(Object result)
//						{
//							BDIInterpreter.super.initProvidedServices(model, config).addResultListener(new DelegationResultListener(fut));
//						}
//					}));				
//				}
//			}));
//		}
//		else
//		{
//			// Capability model: agent stuff already inited.
//			ret	= BDIInterpreter.super.initProvidedServices(model, config);
////			ret	= IFuture.DONE;
//		}
//		return ret;
//	}
	
//	/**
//	 *  Start the services.
//	 */
//	public IFuture startServiceContainer()
//	{
//		// Overriden to do nothing when this is called for a capability.
//		// Hack!!! Container started, but capability services added later.
//		return initcapa==model.getHandle() ? super.startServiceContainer() : IFuture.DONE;
//	}

	
//	/**
//	 *  First init step of agent.
//	 */
//	protected IFuture	init0()
//	{
////		assert isAgentThread();
//		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
//		
//		final Future	ret	= new Future();
//		
//		// Init the external access
////		ea = new ExternalAccessFlyweight(state, ragent);
//		
//		// Get the services.
////		final boolean services[]	= new boolean[3];
////		SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
////			.addResultListener(createResultListener(new DefaultResultListener()
////		{
////			public void resultAvailable(Object result)
////			{
////				clockservice	= (IClockService)result;
////				boolean	startagent;
////				synchronized(services)
////				{
////					services[0]	= true;
////					startagent	= services[0] && services[1] && services[2];// && services[3];
////				}
////				if(startagent)
////				{
////					ret.setResult(null);
////				}
////			}
////		}));
////		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
////			.addResultListener(createResultListener(new DefaultResultListener()
////		{
////			public void resultAvailable(Object result)
////			{
////				cms	= (IComponentManagementService)result;
////				boolean	startagent;
////				synchronized(services)
////				{
////					services[1]	= true;
////					startagent	= services[0] && services[1] && services[2];// && services[3];
////				}
////				if(startagent)
////					ret.setResult(null);
////			}
////		}));
////		SServiceProvider.getService(getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
////			.addResultListener(createResultListener(new DefaultResultListener()
////		{
////			public void resultAvailable(Object result)
////			{
////				msgservice	= (IMessageService)result;
////				boolean	startagent;
////				synchronized(services)
////				{
////					services[2]	= true;
////					startagent	= services[0] && services[1] && services[2];// && services[3];
////				}
////				if(startagent)
////					ret.setResult(null);
////			}
////		}));
//
//		// Previously done in createStartAgentRule
//		Map parents = new HashMap(); 
//		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, parents);
//		AgentRules.createCapabilityInstance(state, ragent, parents);
//		
//		return ret;
//	}
//	
//	/**
//	 *  Second init step of agent.
//	 */
//	protected IFuture	init1()
//	{
//		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
//		
//		return AgentRules.initializeCapabilityInstance(state, ragent);
//	}
	
	//-------- IKernelAgent interface --------
	
	//	Lock lock = new ReentrantLock(); 
	
	/**
	 *  Request agent to kill itself.
	 */
	public IFuture<Void> startEndSteps()
	{
		final Future<Void> ret = new Future<Void>();
		
		Object cs = state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state);
		if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
		{
			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_killfuture, ret);
			AgentRules.startTerminating(state, ragent);
		}
		else if(cs==null)
		{
			// Killed after init (behavior not started)
			// -> Call cleanup directly as rule engine is not running and end state is not executed.
			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED);
			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_killfuture, ret);
			AgentRules.cleanupAgent(state, ragent);						
		}
		else
		{
			// Killed after termination.
			ret.setException(new RuntimeException("Component not running: "+getComponent().getComponentIdentifier().getName()));
		}
		
		return ret;
	}
	
//	/**
//	 *  Get the results of the component (considering it as a functionality).
//	 *  @return The results map (name -> value). 
//	 */
//	public Map getResults()
//	{
//		Map	res	= (Map)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_results);
//		return res!=null ? Collections.unmodifiableMap(res) : Collections.EMPTY_MAP;
//	}

	/**
	 *  Called when the agent is removed from the platform.
	 */
	public void cleanup()
	{
//		System.err.println("Cleanup: "+getComponentIdentifier());

		interpreters.remove(state);
		
//		System.out.println(BDIInterpreter.interpreters.size());
	}

//	/**
//	 *  Test if the component's execution is currently at one of the
//	 *  given breakpoints. If yes, the component will be suspended by
//	 *  the platform.
//	 *  @param breakpoints	An array of breakpoints.
//	 *  @return True, when some breakpoint is triggered.
//	 */
//	public boolean isAtBreakpoint(String[] breakpoints)
//	{
//		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
//		
//		boolean	isatbreakpoint	= false;
//		
//		Object	cs	= state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state);
//		if(cs!=null && !OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED.equals(cs))
//		{
//			Set	bps	= new HashSet(Arrays.asList(breakpoints));	// Todo: cache set across invocations for speed?
//			Iterator	it	= getRuleSystem().getAgenda().getActivations().iterator();
//			while(!isatbreakpoint && it.hasNext())
//			{
//				IRule	rule	= ((Activation)it.next()).getRule();
//				isatbreakpoint	= bps.contains(rule.getName());
//			}
//		}
//		// else still in init
//		
//		return isatbreakpoint;
//	}

//	/**
//	 *  Get the logger.
//	 *  @return The logger.
//	 */
//	public Logger getLogger(Object rcapa)
//	{
//		Logger ret = adapter.getLogger();
//		
//		if(ragent!=rcapa)
//		{
//			// get logger with unique capability name
//			// todo: implement getDetailName()
//			//String name = getDetailName();
//			
//			List path = new ArrayList();
//			findSubcapability(ragent, rcapa, path);
//			StringBuffer buf = new StringBuffer();
//			buf.append(ret.getName()).append(".");
//			for(int i=0; i<path.size(); i++)
//			{
//				Object caparef = path.get(i);
//				String name = (String)state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_name);
//				buf.append(name);
//				if(i+1<path.size())
//					buf.append(".");
//			}
//			String name = buf.toString();
//			ret = LogManager.getLogManager().getLogger(name);
//			
//			// if logger does not already exists, create it
//			if(ret==null)
//			{
//				// Hack!!! Might throw exception in applet / webstart.
//				try
//				{
//					ret = Logger.getLogger(name);
//					initLogger(path, ret);
//					//System.out.println(logger.getParent().getLevel());
//				}
//				catch(SecurityException e)
//				{
//					// Hack!!! For applets / webstart use anonymous logger.
//					ret	= Logger.getAnonymousLogger();
//					initLogger(path, ret);
//				}
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Find the path to a subcapability.
	 *  @param rcapa The start capability.
	 *  @param targetcapa The target capability.
	 *  @param path The result path as list of capas.
	 *  @return True if found.
	 */
	public boolean findSubcapability(Object rcapa, Object targetcapa, List path)
	{
		boolean ret = false;
		
		Collection coll = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext() && !ret; )
			{
				Object caparef = it.next();
				path.add(caparef);
				Object subcapa = state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_capability);
				if(targetcapa==subcapa)
				{
					ret = true;
				}
				else
				{
					ret = findSubcapability(subcapa, targetcapa, path);
					if(!ret)
						path.remove(path.size()-1);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Find the runtime element to a capability model.
	 *  @param rcapa The start capability.
	 *  @param targetcapa The target capability.
	 *  @param path The result path as list of capas.
	 *  @return True if found.
	 */
	protected Object	findSubcapability(Object mcapa)
	{
		Object	ret	= null;
		List	capas = SCollection.createArrayList();
		capas.add(ragent);
		for(int i=0; ret==null && i<capas.size(); i++)
		{
			Object	rcapa	= capas.get(i);
			if(state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model)==mcapa)
			{
				ret	= rcapa;
			}
			else
			{
				Collection	subcaps	= state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
				if(subcaps!=null)
				{
					for(Iterator it=subcaps.iterator(); it.hasNext(); )
					{
						capas.add(state.getAttributeValue(it.next(), OAVBDIRuntimeModel.capabilityreference_has_capability));
					}
				}
			}
		}
		
		return ret;
	}
	
	//-------- other methods --------
	
//	/**
//	 *  Init the logger with capability settings.
//	 *  @param path The list of capability references from agent to subcapability.
//	 *  @param logger The logger.
//	 */
//	protected void initLogger(List path, Logger logger)
//	{
//		// Outer settings overwrite inner settings.
//		Level	level	= null;
//		Boolean	useparent	= null;
//		Level	addconsole	= null;
//		String	logfile	= null;
//		Object	handlers = null;
//		
////		for(int i=-1; i<path.size(); i++)
////		{
////			Object	rcapa	= i==-1 ? ragent
////				: state.getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_capability);
////			if(level==null)
////			{
////				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.level");
////				level	= prop!=null ? (Level)prop : null;
////			}
////			if(useparent==null)
////			{
////				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.useParentHandlers");
////				useparent	= prop!=null ? (Boolean)prop : null;
////			}
////			if(addconsole==null)
////			{
////				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.addConsoleHandler");
////				addconsole	= prop!=null ? (Level)prop : null;
////			}
////			if(logfile==null)
////			{
////				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.file");
////				logfile	= prop!=null ? (String)prop : null;
////			}
////			if(logfile==null)
////			{
////				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.handlers");
////				handlers	= prop!=null ? prop : null;
////			}
////		}
//		
//		// the level of the logger
//		logger.setLevel(level==null? Level.WARNING: level);
//
//		// if logger should use Handlers of parent (global) logger
//		// the global logger has a ConsoleHandler(Level:INFO) by default
//		if(useparent!=null)
//		{
//			logger.setUseParentHandlers(useparent.booleanValue());
//		}
//			
//		// add a ConsoleHandler to the logger to print out
//        // logs to the console. Set Level to given property value
//		if(addconsole!=null)
//		{
//			Handler console;
//			/*if[android]
//			console = new jadex.commons.android.AndroidHandler();
//			 else[android]*/
//			console = new ConsoleHandler();
//			/* end[android]*/
//            console.setLevel(addconsole);
//            logger.addHandler(console);
//        }
//		
//		// Code adapted from code by Ed Komp: http://sourceforge.net/forum/message.php?msg_id=6442905
//		// if logger should add a filehandler to capture log data in a file. 
//		// The user specifies the directory to contain the log file.
//		// $scope.getAgentName() can be used to have agent-specific log files 
//		//
//		// The directory name can use special patterns defined in the
//		// class, java.util.logging.FileHandler, 
//		// such as "%h" for the user's home directory.
//		// 
//		if(logfile!=null)
//		{
//		    try
//		    {
//			    Handler fh	= new FileHandler(logfile);
//		    	fh.setFormatter(new SimpleFormatter());
//		    	logger.addHandler(fh);
//		    }
//		    catch (IOException e)
//		    {
//		    	System.err.println("I/O Error attempting to create logfile: "
//		    		+ logfile + "\n" + e.getMessage());
//		    }
//		}
//		
//		if(handlers!=null)
//		{
//			if(handlers instanceof Handler)
//			{
//				logger.addHandler((Handler)handlers);
//			}
//			else if(SReflect.isIterable(handlers))
//			{
//				for(Iterator it=SReflect.getIterator(handlers); it.hasNext(); )
//				{
//					Object obj = it.next();
//					if(obj instanceof Handler)
//					{
//						logger.addHandler((Handler)obj);
//					}
//					else
//					{
//						logger.warning("Property is not a logging handler: "+obj);
//					}
//				}
//			}
//			else
//			{
//				logger.warning("Property 'logging.handlers' must be Handler or list of handlers: "+handlers);
//			}
//		}
//	}
	
	/**
	 *  Get the agent instance reference.
	 *  @return The agent.
	 */
	public Object	getAgent()
	{
		return ragent;
	}
	
	/**
	 *  Get the rule system.
	 */
	// Hack!!! Used for debugging.
	public RuleSystem	getRuleSystem()
	{
		return rulesystem;
	}
	
	/**
	 *  Get a plan executor by name.
	 *  @param rplan The rplan.
	 *  @return The plan executor.
	 */
	public IPlanExecutor getPlanExecutor(Object rplan)
	{
		Object mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		Object mbody = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_body);
		String name = (String)state.getAttributeValue(mbody, OAVBDIMetaModel.body_has_type);
		
		// Hack?
		if(name==null)
			name = "standard";
		
		IPlanExecutor	ret	= (IPlanExecutor)planexecutors.get(name);
		if(ret==null)
		{
			// Todo: search agent properties for custom plan executors.

//			synchronized(gexecutors)
			{
				// Initialize for each kernel one time.
//				Map	global	= (Map)gexecutors.get(getKernelProperties());
//				if(global==null)
//				{
//					global	= new HashMap();
//					gexecutors.put(getKernelProperties(), global);
					
//					Property[] props = getKernelProperties().getProperties("planexecutor");

//					// claas: commented because its not used anywhere 
//					SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
//					fetcher.setValue("$platformname", getAgentAdapter().getPlatform().getName());
					
					ret = JavaStandardPlanExecutor.createPlanExecutor(getComponent());
				
//					Iterator it = getKernelProperties().keySet().iterator();
//					while(it.hasNext())
//					{
//						String key = (String)it.next();
//						if(key.startsWith("planexecutor"))
//						{
//							String tmp = key.substring(13);
////							System.out.println("PE:"+tmp);
////							global.put(tmp, getKernelProperties().get(key));
//							if(tmp.equals(name))
//							{
//								ret	= (IPlanExecutor)getKernelProperties().get(key);
//							}
//						}
//					}
					
					/*for(int i=0; i<props.length; i++)
					{
						global.put(props[i].getName(), props[i].getJavaObject(fetcher));
					}*/
//				}
//				ret	= (IPlanExecutor)global.get(name);
				if(ret!=null)
					planexecutors.put(name, ret);
				else
					System.out.println("Warning: No plan executor for plan type '"+name+"'.");
			}
		}
		return ret;
	}
	
//	/** global plan executors, cached for speed. */
//	// hack!!!
//	protected static Map gexecutors	= new HashMap();
	
//	/**
//	 *  Get the adapter agent.
//	 *  @return The adapter agent.
//	 */
//	public IComponentAdapter getAgentAdapter()
//	{
//		return this.adapter;
//	}
	
	/**
	 *  Get the agent state.
	 *  @return The agent state.
	 */
	public IOAVState getState()
	{
		return this.state;
	}

	/**
	 *  Invoke some code with agent behaviour synchronized on the agent.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed on the agent thread.
	 *  If the agent does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 *  Note: 1.4 compliant code.
	 *  Problem: Deadlocks cannot be detected and no exception is thrown.
	 */
	public void invokeSynchronized(final Runnable code)
	{
		if(!getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			System.err.println("Unsynchronized internal thread.");
			Thread.dumpStack();

			IFuture<Void>	fut	= getComponent().getComponentFeature(IExecutionFeature.class)
				.scheduleStep(new ImmediateComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					code.run();
					return IFuture.DONE;
				}
			});
			fut.get();
		}
		else
		{
			System.err.println("Method called from internal agent thread.");
			Thread.dumpStack();
			code.run();
		}
	}
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step)
	{
		return scheduleStep(step, null);
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public <T> IFuture<T> scheduleStep(final IComponentStep<T> step, final Object scope)
	{
		return getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				return step.execute(getInternalAccess(scope));
			}
		});
	}
	
	/**
	 *  Execute some code on the component's thread.
	 *  Unlike scheduleStep(), the action will also be executed
	 *  while the component is suspended.
	 *  @param action	Code to be executed on the component's thread.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleImmediate(final IComponentStep<T> step, final Object scope)
	{
		return getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				return step.execute(getInternalAccess(scope));
			}
		});
	}
	
	/**
	 *  Get the currently executed plan.
	 *  @return The currently executed plan.
	 */
	public Object getCurrentPlan()
	{
		return currentplan;
	}

	/**
	 *  Sets the plan currently executed by this agent.
	 *  @param currentplan The current plan.
	 */
	public void setCurrentPlan(Object currentplan)
	{
		this.currentplan = currentplan;
	}
	
	/**
	 *  Get the event reificator for generating change events.
	 */
	public EventReificator	getEventReificator()
	{
		return reificator; 
	}
	
	/**
	 *  Start monitoring the consequences.
	 */
	public void startMonitorConsequences()
	{
		if(microplansteps && getRuleSystem().isInited())
		{
			monitor_consequences++;
			if(monitor_consequences==1)
				agenda_state = getRuleSystem().getAgenda().getState();
		}
	}

	/**
	 *  Checks if consequences have been produced and
	 *  interrupts the executing plan accordingly.
	 */
	public void endMonitorConsequences()
	{
		if(microplansteps && getRuleSystem().isInited())
		{
			assert monitor_consequences>0;
		
			monitor_consequences--;
			// Interrupt only when not in atomic block.
			if(monitor_consequences==0 && !isAtomic())
			{
				// When consequences pause plan (micro step)
				// to allow consequences being executed.
				// todo: only when actions are added this is of importance
				state.notifyEventListeners();
				if(agenda_state!=getRuleSystem().getAgenda().getState())
				{
					Object rplan = getCurrentPlan();
					if(rplan!=null)
					{
	//					rplan.interruptPlanStep();
						// todo: support different plan executors
						getPlanExecutor(rplan).interruptPlanStep(rplan);
					}
				}
			}
		}
	}
	
	/**
	 *  Start an atomic transaction.
	 *  All possible side-effects (i.e. triggered conditions)
	 *  of internal changes (e.g. belief changes)
	 *  will be delayed and evaluated after endAtomic() has been called.
	 *  @see #endAtomic()
	 */
	public void	startAtomic()
	{
		assert !atomic: "Is already atomic.";
		atomic	= true;
	}

//RuntimeException	st;

	/**
	 *  End an atomic transaction.
	 *  Side-effects (i.e. triggered conditions)
	 *  of all internal changes (e.g. belief changes)
	 *  performed after the last call to startAtomic()
	 *  will now be evaluated and performed.
	 *  @see #startAtomic()
	 */
	public void	endAtomic()
	{
//		if(!atomic)
//		{
//			System.err.println("hier1: "+currentplan);
//			st.printStackTrace(System.err);
//			System.err.println("hier2: "+currentplan);
//			throw new RuntimeException("second end atomic: "+currentplan);
//		}
//		try
//		{
//			throw new RuntimeException("first end atomic: "+currentplan);
//		}
//		catch(RuntimeException e)
//		{
//			st	=e;
//		}

		assert atomic;
		atomic	= false;

		// Hack!!! Do not use processTransactionConsequences() directly,
		// as info events need to know if transaction committing is in progress.
//		startSystemEventTransaction();
//		commitSystemEventTransaction();
	}
	
	/**
	 *  Check if atomic state is enabled.
	 *  @return True, if is atomic.
	 */
	public boolean	isAtomic()
	{
		return atomic;
	}
	
//	/**
//	 *  Get the kernel properties.
//	 *  @return The kernel properties.
//	 */
//	public Map getKernelProperties()
//	{
//		return kernelprops;
//	}
	
	/**
	 *  Put an element into the cache.
	 */
	public void putFlyweightCache(Class type, Object key, Object flyweight)
	{
//		if(isExternalThread())
//			System.out.println("wrong thread");
		
		Map cache = stacacheelems.contains(type)? stacache: volcache;
		cache.put(key, flyweight);
	}
	
	/**
	 *  Get an element from the cache.
	 */
	public Object getFlyweightCache(Class type, Object key)
	{
//		if(isExternalThread())
//			System.out.println("wrong thread");
		
		Map cache = stacacheelems.contains(type)? stacache: volcache;
		return cache.get(key);
	}
	
//	/**
//	 *  Get the parent of the agent.
//	 *  @return The external access of the parent.
//	 */
//	public IExternalAccess getParent()
//	{
//		return parent;
//	}
	
//	/**
//	 *  Create the service container.
//	 *  @return The service container.
//	 */
//	public IInternalAccess getServiceContainer()
//	{
////		assert container!=null;
//		if(container==null)
//			container = createServiceContainer();
//		return container;
//	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the interpreter for an agent object.
	 */
//	public static BDIInterpreter	getInterpreter(IOAVState state)
	public static IInternalBDIAgentFeature getInterpreter(IOAVState state)
	{
		return (IInternalBDIAgentFeature)((IInternalAccess)interpreters.get(state)).getComponentFeature(IBDIAgentFeature.class);
	}
	
	public static IInternalAccess getInternalAccess(IOAVState state)
	{
		return (IInternalAccess)interpreters.get(state);
	}

	/** The (global) rulebase. */
	public static final IRulebase RULEBASE;

	static
	{
		interpreters = Collections.synchronizedMap(new HashMap());
		
		RULEBASE = new Rulebase();
		
		// Agent rules.
//		RULEBASE.addRule(AgentRules.createInit0AgentRule());
//		RULEBASE.addRule(AgentRules.createInit1AgentRule());
		RULEBASE.addRule(AgentRules.createTerminatingEndAgentRule());
		RULEBASE.addRule(AgentRules.createTerminateAgentRule());
		RULEBASE.addRule(AgentRules.createRemoveChangeEventRule());
		RULEBASE.addRule(AgentRules.createExecuteActionRule());
//		RULEBASE.addRule(AgentRules.createTerminatingStartAgentRule());

		// Listener rules.
		RULEBASE.addRule(ListenerRules.createInternalEventListenerRule());
		RULEBASE.addRule(ListenerRules.createMessageEventListenerRule());
		RULEBASE.addRule(ListenerRules.createBeliefChangedListenerRule());
		RULEBASE.addRule(ListenerRules.createBeliefSetListenerRule());
		RULEBASE.addRule(ListenerRules.createGoalListenerRule());
		RULEBASE.addRule(ListenerRules.createPlanListenerRule());
		//RULEBASE.addRule(ListenerRules.createAgentTerminationListenerRule());
		
		// Message event rules.
		RULEBASE.addRule(MessageEventRules.createMessageMatchingRule());
		RULEBASE.addRule(MessageEventRules.createMessageConversationMatchingRule());
		RULEBASE.addRule(MessageEventRules.createMessageNoMatchRule());
//		RULEBASE.addRule(MessageEventRules.createMessageArrivedRule());
		RULEBASE.addRule(MessageEventRules.createSendMessageRule());
		
		// Event processing rules.
//		RULEBASE.addRule(EventProcessingRules.createDispatchGoalFromWaitqueueRule());
		RULEBASE.addRule(EventProcessingRules.createDispatchMessageEventFromWaitqueueRule());
		RULEBASE.addRule(EventProcessingRules.createDispatchInternalEventFromWaitqueueRule());
		RULEBASE.addRule(EventProcessingRules.createDispatchFactAddedFromWaitqueueRule());
		RULEBASE.addRule(EventProcessingRules.createDispatchFactRemovedFromWaitqueueRule());
		RULEBASE.addRule(EventProcessingRules.createDispatchFactChangedFromWaitqueueRule());
		Rule[]	aplrules = EventProcessingRules.createBuildRPlanAPLRules();
		for(int i=0; i<aplrules.length; i++)
			RULEBASE.addRule(aplrules[i]);
//		RULEBASE.addRule(EventProcessingRules.createMakeAPLAvailableRule());
		RULEBASE.addRule(EventProcessingRules.createMetaLevelReasoningForGoalRule());
		RULEBASE.addRule(EventProcessingRules.createMetaLevelReasoningForInternalEventRule());
		RULEBASE.addRule(EventProcessingRules.createMetaLevelReasoningForMessageEventRule());
		RULEBASE.addRule(EventProcessingRules.createMetaLevelReasoningFinishedRule());
		RULEBASE.addRule(EventProcessingRules.createSelectCandidatesForGoalRule());
		RULEBASE.addRule(EventProcessingRules.createSelectCandidatesForInternalEventRule());
		RULEBASE.addRule(EventProcessingRules.createSelectCandidatesForMessageEventRule());
//		RULEBASE.addRule(EventProcessingRules.createRemoveEventprocessingArtifactRule());  // todo: remove
		
		// Goal deliberation rules.
		RULEBASE.addRule(GoalDeliberationRules.createGoalExitActiveStateRule());
		
//		RULEBASE.addRule(GoalDeliberationRules.createDeliberateGoalActivationRule());
//		RULEBASE.addRule(GoalDeliberationRules.createDeliberateGoalDeactivationRule());
		
		RULEBASE.addRule(GoalDeliberationRules.createAddTypeInhibitionLinkRule());
		RULEBASE.addRule(GoalDeliberationRules.createRemoveTypeInhibitionLinkRule());
		RULEBASE.addRule(GoalDeliberationRules.createActivateGoalRule());
		RULEBASE.addRule(GoalDeliberationRules.createDeactivateGoalRule());

		// Goal lifecycle rules.
		RULEBASE.addRule(GoalLifecycleRules.createGoalDroppingRule());
		RULEBASE.addRule(GoalLifecycleRules.createGoalDropRule());
		
		// Goal processing rules.
		RULEBASE.addRule(GoalProcessingRules.createGoalFailedRule());
		RULEBASE.addRule(GoalProcessingRules.createGoalRetryRule());
		RULEBASE.addRule(GoalProcessingRules.createGoalRecurRule());
		RULEBASE.addRule(GoalProcessingRules.createPerformgoalProcessingRule());
		RULEBASE.addRule(GoalProcessingRules.createPerformgoalFinishedRule());
		RULEBASE.addRule(GoalProcessingRules.createAchievegoalProcessingRule());
		RULEBASE.addRule(GoalProcessingRules.createAchievegoalRetryRule());
		RULEBASE.addRule(GoalProcessingRules.createAchievegoalSucceededRule());
		RULEBASE.addRule(GoalProcessingRules.createAchievegoalFailedRule());
		RULEBASE.addRule(GoalProcessingRules.createQuerygoalProcessingRule());
		RULEBASE.addRule(GoalProcessingRules.createQuerygoalSucceededRule());
		RULEBASE.addRule(GoalProcessingRules.createQuerygoalFailedRule());
		RULEBASE.addRule(GoalProcessingRules.createMaintaingoalFailedRule());
		
		// Plan rules. 
//		RULEBASE.addRule(PlanRules.createPlanBodyRule());
		RULEBASE.addRule(PlanRules.createPlanBodyExecutionRule());
		RULEBASE.addRule(PlanRules.createPlanPassedExecutionRule());
		RULEBASE.addRule(PlanRules.createPlanFailedExecutionRule());
		RULEBASE.addRule(PlanRules.createPlanAbortedExecutionRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceFactChangedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceFactAddedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceFactRemovedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceExternalConditionTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanWaitqueueFactAddedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanWaitqueueFactRemovedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanWaitqueueFactChangedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanFactChangedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanFactAddedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanFactRemovedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanGoalFinishedTriggerRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceAbortRule());
		RULEBASE.addRule(PlanRules.createPlanRemovalRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceCleanupFinishedRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceGoalFinishedRule());
		RULEBASE.addRule(PlanRules.createPlanInstanceMaintainGoalFinishedRule());
		RULEBASE.addRule(PlanRules.createPlanWaitqueueGoalFinishedRule());
		
		// External access rules.
		RULEBASE.addRule(ExternalAccessRules.createExternalAccessGoalTriggeredRule());
		RULEBASE.addRule(ExternalAccessRules.createExternalAccessMessageEventTriggeredRule());
		RULEBASE.addRule(ExternalAccessRules.createExternalAccessEventTriggeredRule());
		RULEBASE.addRule(ExternalAccessRules.createExternalAccessFactChangedTriggeredRule());
		RULEBASE.addRule(ExternalAccessRules.createExternalAccessFactAddedTriggeredRule());
		RULEBASE.addRule(ExternalAccessRules.createExternalAccessFactRemovedTriggeredRule());
	}

//	/**
//	 *  Get the component adapter.
//	 *  @return The component adapter.
//	 */
//	public IComponentAdapter	getComponentAdapter()
//	{
//		return adapter;
//	}

	/**
	 *  Get the model info.
	 *  @return The model info.
	 */
	public IModelInfo	getModel()
	{
		return model.getModelInfo();
	}
	
	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return (String)getState().getAttributeValue(getAgent(), OAVBDIRuntimeModel.capability_has_configuration);
	}
	
	/**
	 *  Get the model info of a capability
	 *  @param rcapa	The capability.
	 *  @return The model info.
	 */
	public IModelInfo	getModel(Object rcapa)
	{
		Object	mcapa	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		return model.getSubcapabilityModel(mcapa).getModelInfo();
	}
	
	/**
	 *  Get the value fetcher.
	 *  @return The value fetcher.
	 */
	public IValueFetcher	getFetcher()
	{
		return new OAVBDIFetcher(state, initcapa!=null ? findSubcapability(initcapa) : ragent);
	}
	
	/**
	 *  Get the internal access.
	 *  @return The internal access.
	 */
	public IInternalAccess	getInternalAccess()
	{
		return getInternalAccess(null);
	}
	
	/**
	 *  Get the internal access.
	 *  @return The internal access.
	 */
	public IInternalAccess	getInternalAccess(Object scope)
	{
		return new CapabilityFlyweight(state, scope!=null ? scope : initcapa!=null ? findSubcapability(initcapa) : ragent);
	}
		
//	/**
//	 *  Get the component listeners.
//	 *  @return The component listeners.
//	 */
//	public IComponentListener[]	getComponentListeners()
//	{
//		Collection	coll	= getState().getAttributeValues(ragent, OAVBDIRuntimeModel.agent_has_componentlisteners);
//		return coll!=null ? (IComponentListener[])coll.toArray(new IComponentListener[coll.size()]) : new IComponentListener[0];
//	}
//	
//	/**
//	 *  Remove component listener.
//	 */
//	public IFuture<Void>	removeComponentListener(IComponentListener listener)
//	{
//		getState().removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_componentlisteners, listener);
//		return IFuture.DONE;
//	}
//	
//	/**
//	 *  Add component listener.
//	 */
//	public IFuture<Void>	addComponentListener(IComponentListener listener)
//	{
////		System.out.println("Added: "+listener+", "+getState().getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state));
//		getState().addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_componentlisteners, listener);
//		return IFuture.DONE;
//	}
	
//	/**
//	 *  Add an extension instance.
//	 *  @param extension The extension instance.
//	 */
//	public void	addExtension(String name, IExtensionInstance ext)
//	{
//		if(extensions==null)
//		{
//			extensions = new HashMap();
//		}
//		extensions.put(name, ext);
//	}
//	
//	/**
//	 *  Get a space of the application.
//	 *  @param name	The name of the space.
//	 *  @return	The space.
//	 */
//	public IExtensionInstance getExtension(String name)
//	{
//		return extensions==null? null: (IExtensionInstance)extensions.get(name);
//	}
//	
//	/**
//	 *  Get a space of the application.
//	 *  @param name	The name of the space.
//	 *  @return	The space.
//	 */
//	public IExtensionInstance[] getExtensions()
//	{
//		return extensions==null? new IExtensionInstance[0]: 
//			(IExtensionInstance[])extensions.values().toArray(new IExtensionInstance[extensions.size()]);
//	}
	
//	/**
//	 *  Get the properties.
//	 *  @return the properties.
//	 */
//	public Map getProperties()
//	{
//		return properties;
//	}
//	
//	/**
//	 *  Get the properties for a subcapability.
//	 *  @return the properties.
//	 */
//	public Map getProperties(Object rcapa)
//	{
//		// Todo
//		return properties==null? Collections.EMPTY_MAP: properties;
//	}
//	
//	/**
//	 *  Add a property value.
//	 *  @param name The name.
//	 *  @param val The value.
//	 */
//	public void addProperty(String name, Object val)
//	{
//		if(properties==null)
//			properties = new HashMap();
//		properties.put(name, val);
//	}

	/**
	 *  Get the arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return arguments==null? Collections.EMPTY_MAP: arguments;
	}
	
//	/**
//	 *  Get the component listeners.
//	 *  @return The component listeners.
//	 */
//	public Collection<IComponentListener> getInternalComponentListeners()
//	{
//		// Todo: support this!?
//		return Collections.EMPTY_LIST;
//	}
	
	/**
	 *  Get the service prefix.
	 *  @return The prefix for required services.
	 */
	public String getServicePrefix()
	{
		return findServicePrefix(initcapa!=null ? findSubcapability(initcapa) : ragent);
	}
	
//	/**
//	 *  Get the bindings.
//	 *  @return The bindings.
//	 */
//	public RequiredServiceBinding[]	getBindings()
//	{
//		return bindings;
//	}
//
//	/**
//	 *  Get the service infos.
//	 *  @return The service infos.
//	 */
//	public ProvidedServiceInfo[]	getProvidedServiceInfos()
//	{
//		return pinfos;
//	}

	/**
	 *  The prefix is the name of the capability starting from the agent.
	 */
	public String	findServicePrefix(Object scope)
	{
		List	path	= new ArrayList();
		findSubcapability(getAgent(), scope, path);
		String prefix	= "";
		for(int i=0; i<path.size(); i++)
		{
			prefix	+= getState().getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_name)+ ".";
		}
		return prefix;
	}

//	/**
//	 *  Get the copy.
//	 *  @return the copy.
//	 */
//	public boolean isCopy()
//	{
//		return copy;
//	}
	
//	/**
//	 *  Create the service container.
//	 *  @return The service conainer.
//	 */
//	public IInternalAccess createServiceContainer()
//	{
//		assert container==null;
//		return new ComponentServiceContainer(adapter, getComponentAdapter().getDescription().getType(), getInternalAccess(), isRealtime(), getServiceRegistry());
//	}
	
	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchrnonous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	public Map<String, Object> getResults()
	{
		return results!=null? Collections.unmodifiableMap(results): Collections.EMPTY_MAP;
	}
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value)
	{
		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
		
		// todo: store results only within listener?!
		if(results==null)
			results	= new HashMap<String, Object>();
		results.put(name, value);
		
		if(resultsubscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<Tuple2<String, Object>> fut: resultsubscriptions.toArray(new SubscriptionIntermediateFuture[resultsubscriptions.size()]))
			{
				if(!fut.addIntermediateResultIfUndone(new Tuple2<String, Object>(name, value)))
				{
					resultsubscriptions.remove(fut);
				}
			}
		}
	}

//	/**
//	 *  Get the realtime.
//	 *  @return The realtime.
//	 */
//	public boolean isRealtime()
//	{
//		return realtime;
//	}
	
	/**
	 *  Create a wrapper service implementation based on 
	 */
//	public static Object createServiceImplementation(IBDIInternalAccess agent, Class<?> type, Map<String, String> goalnames)
	public static Object createServiceImplementation(IBDIInternalAccess agent, Class<?> type, String[] methodnames, String[] goalnames)
	{
//		if(methodnames==null || methodnames.length==0)
//			throw new IllegalArgumentException("At least one method-goal mapping must be given.");
		Map<String, String> gn = new HashMap<String, String>();
		for(int i=0; i<methodnames.length; i++)
		{
			gn.put(methodnames[i], goalnames[i]);
		}
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new GoalDelegationHandler(agent, gn));
	}
	
	/**
	 *  Get the monitoring service getter.
	 *  @return The monitoring service getter.
	 */
	public ServiceGetter<IMonitoringService> getMonitoringServiceGetter()
	{
		if(getter==null)
			getter = new ServiceGetter<IMonitoringService>(getInternalAccess(), IMonitoringService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		return getter;
	}
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel emitlevel)
	{
		final SubscriptionIntermediateFuture<IMonitoringEvent> ret = (SubscriptionIntermediateFuture<IMonitoringEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, getInternalAccess());
			
		ITerminationCommand tcom = new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removeSubscription(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		};
		ret.setTerminationCommand(tcom);
		
		// Signal that subscription has been done
		MonitoringEvent	subscribed	= new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), 
			IMonitoringEvent.TYPE_SUBSCRIPTION_START, System.currentTimeMillis(), PublishEventLevel.COARSE);
		boolean	post = false;
		try
		{
			post = filter==null || filter.filter(subscribed);
		}
		catch(Exception e)
		{
		}
		if(post)
		{
			ret.addIntermediateResult(subscribed);
		}

		addSubscription(ret, filter, emitlevel);
		
		if(initial)
		{
			List<IMonitoringEvent> evs = getCurrentStateEvents();
			if(evs!=null && evs.size()>0)
			{
				BulkMonitoringEvent bme = new BulkMonitoringEvent(evs.toArray(new IMonitoringEvent[evs.size()]));
				ret.addIntermediateResult(bme);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults()
	{
		final SubscriptionIntermediateFuture<Tuple2<String, Object>> ret = (SubscriptionIntermediateFuture<Tuple2<String, Object>>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, getInternalAccess());
		if(resultsubscriptions==null)
		{
			resultsubscriptions = new ArrayList<SubscriptionIntermediateFuture<Tuple2<String, Object>>>();
		}
		resultsubscriptions.add(ret);
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				resultsubscriptions.remove(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		
		// Notify caller that subscription is done
		ret.addIntermediateResult(null);
		
		// Notify about results
		if(results!=null)
		{
			for(Map.Entry<String, Object> res: results.entrySet())
			{
				ret.addIntermediateResult(new Tuple2<String, Object>(res.getKey(), res.getValue()));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Forward event to all currently registered subscribers.
	 */
	public void publishLocalEvent(IMonitoringEvent event)
	{
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<IMonitoringEvent> sub: subscriptions.keySet().toArray(new SubscriptionIntermediateFuture[0]))
			{
				publishLocalEvent(event, sub);
			}
		}
	}
	
	/**
	 *  Forward event to one subscribers.
	 */
	protected void publishLocalEvent(IMonitoringEvent event, SubscriptionIntermediateFuture<IMonitoringEvent> sub)
	{
		Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel> tup = subscriptions.get(sub);
		try
		{
			PublishEventLevel el = tup.getSecondEntity();
			if(event.getLevel().getLevel()<=el.getLevel())
			{
				IFilter<IMonitoringEvent> fil = tup.getFirstEntity();
				if(fil==null || fil.filter(event))
				{
	//				System.out.println("forward to: "+event+" "+sub);
					if(!sub.addIntermediateResultIfUndone(event))
					{
						subscriptions.remove(sub);
					}
				}
			}
		}
		catch(Exception e)
		{
			// catch filter exceptions
			e.printStackTrace();
		}
	}
	
	/**
	 *  Add a new subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> future, IFilter<IMonitoringEvent> filter, PublishEventLevel emitlevel)
	{
		if(subscriptions==null)
			subscriptions = new LinkedHashMap<SubscriptionIntermediateFuture<IMonitoringEvent>, Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel>>();
		if(emitlevel.getLevel()>emitlevelsub.getLevel())
			emitlevelsub = emitlevel;
		subscriptions.put(future, new Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel>(filter, emitlevel));
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param fut The subscription future to remove.
	 */
	protected void removeSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> fut)
	{
		if(subscriptions==null || !subscriptions.containsKey(fut))
			throw new RuntimeException("Subscriber not known: "+fut);
		subscriptions.remove(fut);
		emitlevelsub = PublishEventLevel.OFF;
		for(Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel> tup: subscriptions.values())
		{
			if(tup.getSecondEntity().getLevel()>emitlevelsub.getLevel())
				emitlevelsub = tup.getSecondEntity();
			if(PublishEventLevel.FINE.equals(emitlevelsub))
				break;
		}
	}
	
	/**
	 *  Generate added events for the current goals
	 */
	public List<IMonitoringEvent> getCurrentStateEvents()
	{
		List<IMonitoringEvent> events = new ArrayList<IMonitoringEvent>();
		getCurrentStateEvents(getInternalAccess(), state, getAgent(), events);
		return events;
	}
	
	/**
	 *  Generate added events for the current goals
	 */
	public void getCurrentStateEvents(IInternalAccess ia, IOAVState state, Object capa, List<IMonitoringEvent> events)
	{
		// Beliefs of this capability.
		Collection	beliefs	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_beliefs);
		if(beliefs!=null)
		{
			for(Iterator it=beliefs.iterator(); it.hasNext(); )
			{
				Object	belief	= it.next();
				BeliefInfo	info = BeliefInfo.createBeliefInfo(state, belief, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_FACT, info.getType(), belief.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(belief.toString());
				ev.setProperty("details", info);
				events.add(ev);
			}
		}
		
		// Belief sets of this capability.
		Collection	beliefsets	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_beliefsets);
		if(beliefsets!=null)
		{
			for(Iterator it=beliefsets.iterator(); it.hasNext(); )
			{
				Object	beliefset	= it.next();
				BeliefInfo	info = BeliefInfo.createBeliefInfo(state, beliefset, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_FACT, info.getType(), beliefset.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(beliefset.toString());
				ev.setProperty("details", info);
				events.add(ev);
			}
		}
		
		// Goals of this capability.
		Collection	goals	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_goals);
		if(goals!=null)
		{
			for(Iterator it=goals.iterator(); it.hasNext(); )
			{
				Object	goal	= it.next();
				GoalInfo	info = GoalInfo.createGoalInfo(state, goal, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_GOAL, info.getType(), goal.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_GOAL, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(goal.toString());
				ev.setProperty("details", info);
				events.add(ev);
			}
		}
		
		// Plans of this capability.
		Collection	plans	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_plans);
		if(plans!=null)
		{
			for(Iterator it=plans.iterator(); it.hasNext(); )
			{
				Object	plan	= it.next();
				PlanInfo	info = PlanInfo.createPlanInfo(state, plan, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_PLAN, info.getType(), plan.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_PLAN, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(plan.toString());
				ev.setProperty("details", info);
				events.add(ev);
			}
		}
		
		// Recurse for sub capabilities.
		Collection	capas	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(capas!=null)
		{
			for(Iterator it=capas.iterator(); it.hasNext(); )
			{
				getCurrentStateEvents(ia, state, state.getAttributeValue(it.next(), OAVBDIRuntimeModel.capabilityreference_has_capability), events);
			}
		}
	}

	@Override
	public Logger getLogger(Object rcapa)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
//	/**
//	 *  Get the required service property provider for a service.
//	 */
//	public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid)
//	{
//		INFMixedPropertyProvider ret = null;
//		if(reqserprops==null)
//			reqserprops = new HashMap<IServiceIdentifier, INFMixedPropertyProvider>(); // use LRU?
//		ret = reqserprops.get(sid);
//		if(ret==null)
//		{
//			ret = new NFMethodPropertyProvider(null)
//			{
//				public IInternalAccess getInternalAccess() 
//				{
//					return BDIInterpreter.this.getInternalAccess();
//				}
//			}; // parent of required service property?
//			reqserprops.put(sid, ret);
//		}
//		return ret;
//	}
	
//	/**
//	 *  Has the service a property provider.
//	 */
//	public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid)
//	{
//		return reqserprops!=null? reqserprops.get(sid)!=null: false;
//	}
//	
//
//	/**
//	 *  Check if event targets exist.
//	 */
//	public boolean hasEventTargets(PublishTarget pt, PublishEventLevel pi)
//	{
//		boolean ret = false;
//		
//		if(pi.getLevel()<=getPublishEmitLevelSubscriptions().getLevel() 
//			&& (PublishTarget.TOALL.equals(pt) || PublishTarget.TOSUBSCRIBERS.equals(pt)))
//		{
//			ret = subscriptions!=null && !subscriptions.isEmpty();
//		}
//		if(!ret && pi.getLevel()<=getPublishEmitLevelMonitoring().getLevel()
//			&& (PublishTarget.TOALL.equals(pt) || PublishTarget.TOMONITORING.equals(pt)))
//		{
//			ret = true;
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the monitoring event emit level for subscriptions.
//	 *  Is the maximum level of all subscriptions (cached for speed).
//	 */
//	public PublishEventLevel getPublishEmitLevelSubscriptions()
//	{
//		return emitlevelsub;
//	}
	
//	/**
//	 *  Get the monitoring event emit level.
//	 */
//	public PublishEventLevel getPublishEmitLevelMonitoring()
//	{
//		return getComponent().getComponentDescription().getMonitoring();
//	}
	
//	/**
//	 *  Terminate the result subscribers.
//	 */
//	public void terminateResultSubscribers()
//	{
//		if(resultsubscriptions!=null)
//		{
//			// terminate all but the first (cms) subscriptions
//			for(SubscriptionIntermediateFuture<Tuple2<String, Object>> sub: resultsubscriptions)
//			{
//				if(sub.equals(cmssub))
//				{
//					continue;
//				}
//				sub.setFinishedIfUndone();
//			}
//		}
//	}
//
//	/**
//	 *  Invalidate the external access.
//	 */
//	public void invalidateAccess(boolean terminate)
//	{
//		// Todo...
//	}
//	
//	/**
//	 *  Get the service registry.
//	 *  @return The service registry.
//	 */
//	public PlatformServiceRegistry getServiceRegistry() 
//	{
//		return registry;
//	}
	
	/**
	 *  The feature can inject parameters for expression evaluation
	 *  by providing an optional value fetcher. The fetch order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IValueFetcher	getValueFetcher()
	{
		// todo: capabilities
		return new OAVBDIFetcher(getState(), ragent);
	}
}
