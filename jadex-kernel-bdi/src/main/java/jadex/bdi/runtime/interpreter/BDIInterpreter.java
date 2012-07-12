package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.OAVCapabilityModel;
import jadex.bdi.runtime.IBDIExternalAccess;
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
import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.ISynchronizator;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.kernelbase.StatelessAbstractInterpreter;
import jadex.rules.rulesystem.Activation;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.PriorityAgenda;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 *  Main entry point for the reasoning engine
 *  holding the relevant agent data structure
 *  and performing the agent execution when
 *  being called from the platform.
 */
public class BDIInterpreter	extends StatelessAbstractInterpreter
{
	//-------- static part --------
	
	/** The interpreters, one per agent (ragent -> interpreter). */
	// Hack e.g. for fetching agent-dependent plan executors
	public static Map interpreters;
	
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
	
	/** The platform adapter for the agent. */
	protected IComponentAdapter	adapter;
	
	/** The parent of the agent (if any). */
	protected IExternalAccess	parent;
	
	/** The kernel properties. */
	protected Map kernelprops;
	
	/** The extensions. */
	protected Map<String, IExtensionInstance> extensions;
	
	/** The properties. */
	protected Map<String, Object> properties;
	
	/** The arguments. */
	protected Map<String, Object> arguments;
	
	/** The results. */
	protected Map<String, Object> results;

	/** The external service bindings. */
	protected RequiredServiceBinding[] bindings;
	
	//-------- recreate on init (no state) --------
	
	/** The event reificator creates changeevent objects for relevant state changes. */
	protected EventReificator reificator;
	
	/** The clock service. */
	//hack???
	protected IClockService	clockservice;
	
	/** The component management service. */
	//hack???
	protected IComponentManagementService	cms;
	
	/** The message service. */
	//hack???
	protected IMessageService	msgservice;
	
	//-------- null on init --------
	
	/** The plan thread currently executing (null for none). */
	protected transient Thread planthread;
	
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
	
	protected static Set stacacheelems;
	
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
		stacacheelems.add(IBDIExternalAccess.class);
		stacacheelems.add(IBelief.class);
		stacacheelems.add(IBeliefSet.class);
	}
	
	//-------- refactor --------
	
	/** The plan executor. */
	protected Map planexecutors;
	
	/** The externally synchronized threads to be notified on cleanup. */
	protected Set externalthreads;
	
//	/** The get-external-access listeners to be notified after init. */
//	protected Set eal;
	
	/** The service container. */
	protected IServiceContainer container;
	
	/** The cms future for init return. */
	protected Future inited;
	
	/** The cached external access. */
	protected IExternalAccess ea;
	
	/** The initthread. */
	protected Thread initthread;
	
	/** The currently inited mcapability. */
	protected Object	initcapa;
	
	/** The parameter copy flag. */
	protected boolean copy;

	/** The realtime local timeout flag. */
	protected boolean realtime;

	
	/** The result listener. */
	protected IIntermediateResultListener<Tuple2<String, Object>> resultlistener;
	
	//-------- constructors --------
	
	/**
	 *  Create an agent interpreter for the given agent.
	 *  @param state	The state. 
	 *  @param magent	The reference to the agent model in the state.
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 */
	public BDIInterpreter(IComponentDescription desc, IComponentAdapterFactory factory, final IOAVState state, final OAVAgentModel model, 
		final String config, final Map<String, Object> arguments, final IExternalAccess parent, RequiredServiceBinding[] bindings, 
		final Map kernelprops, boolean copy, boolean realtime, IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> inited)
	{	
		this.initthread = Thread.currentThread();
		
		this.state = state;
		this.model = model;
		this.parent	= parent;
		this.kernelprops = kernelprops;
		this.planexecutors = new HashMap();
		this.volcache = new LRU(0);	// 50
		this.stacache = new LRU(20);
		this.microplansteps = true;
		this.externalthreads	= Collections.synchronizedSet(SCollection.createLinkedHashSet());
		this.copy = copy;
		this.realtime = realtime;
		this.resultlistener = resultlistener;
		this.inited = inited;
		
		// Hack! todo:
		interpreters.put(state, this);
		
//		System.out.println("arguments: "+adapter.getComponentIdentifier().getName()+" "+arguments);
		
		state.setSynchronizator(new ISynchronizator()
		{
			public boolean isExternalThread()
			{
				return BDIInterpreter.this.getComponentAdapter().isExternalThread();
			}
			
			public void invokeSynchronized(Runnable code)
			{
				BDIInterpreter.this.invokeSynchronized(code);
			}
			
			public void invokeLater(Runnable action)
			{
				getAgentAdapter().invokeLater(action);
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
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.capability_has_configuration, config);
		if(arguments!=null && !arguments.isEmpty())
			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments, arguments);
		this.bindings	= bindings;

//		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_INITING0);
		
		reificator	= new EventReificator(state, ragent);
		
		// Initialize rule system.
		rulesystem = new RuleSystem(state, model.getMatcherFunctionality().getRulebase(), model.getMatcherFunctionality(), new PriorityAgenda());
		
		if(kernelprops!=null)
		{
			Boolean mps = (Boolean)kernelprops.get("microplansteps");
			if(mps!=null)
				microplansteps = mps.booleanValue();
		}
		
		// Init the external access
		this.adapter = factory.createComponentAdapter(desc, model.getModelInfo(), this, parent);
		this.container = createServiceContainer();
		this.ea = new ExternalAccessFlyweight(state, ragent);

		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				init(getModel(), config).addResultListener(createResultListener(new DelegationResultListener(inited)));
				return IFuture.DONE;
			}
		});

		this.initthread = null;
	}
	
	/**
	 *  Start the component behavior.
	 */
	public void startBehavior()
	{
		assert !getComponentAdapter().isExternalThread();

		// Use step in case agent is started as suspended.
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("sb start: "+getComponentIdentifier());
				
				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, null);
				
				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE);
				// Remove arguments from state.
				if(state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments)!=null) 
					state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments, null);

				rulesystem.init();
				
//				System.out.println("sb end: "+getComponentIdentifier());
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Extended init procedure including subcapabilities.
	 */
	public IFuture init(IModelInfo model,String config)
	{
//		assert isAgentThread();
		assert !getAgentAdapter().isExternalThread();
		
		return initCapability(this.model, config);
	}
	
	/**
	 *  Init the component portion of a capability.
	 */
	protected IFuture	initCapability(final OAVCapabilityModel oavmodel, final String config)
	{
//		assert isAgentThread();
		assert !getAgentAdapter().isExternalThread();
		
		final Future	ret	= new Future();
		initcapa	= oavmodel.getHandle();
		BDIInterpreter.super.init(oavmodel.getModelInfo(), config, null)
			.addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				initcapa	= null;
				Collection subcaps	= state.getAttributeValues(oavmodel.getHandle(), OAVBDIMetaModel.capability_has_capabilityrefs);
				if(subcaps!=null)
				{
					final Iterator it	= subcaps.iterator();
					IResultListener	lis	= new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
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
								initCapability(submodel, subconf).addResultListener(createResultListener(this));
							}
							else
							{
								super.customResultAvailable(result);
							}
						}
					};
					lis.resultAvailable(null);
				}
				else
				{
					super.customResultAvailable(result);
				}
			}
		}));
		
		return ret;
	}

	/**
	 *  Overridden to init BDI internals before services.
	 */
	public IFuture initServices(final IModelInfo model, final String config)
	{
//		assert isAgentThread();
		assert !getAgentAdapter().isExternalThread();
		
		IFuture	ret;
		if(model==getModel())
		{
			final Future	fut	= new Future();
			ret	= fut;
			// Agent model: init agent stuff first.
			init0().addResultListener(createResultListener(new DelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					init1().addResultListener(createResultListener(new DelegationResultListener(fut)
					{
						public void customResultAvailable(Object result)
						{
							BDIInterpreter.super.initServices(model, config).addResultListener(new DelegationResultListener(fut));
						}
					}));				
				}
			}));
		}
		else
		{
			// Capability model: agent stuff already inited.
			ret	= BDIInterpreter.super.initServices(model, config);
//			ret	= IFuture.DONE;
		}
		return ret;
	}
	
	/**
	 *  Start the services.
	 */
	public IFuture startServiceContainer()
	{
		// Overriden to do nothing when this is called for a capability.
		// Hack!!! Container started, but capability services added later.
		return initcapa==model.getHandle() ? super.startServiceContainer() : IFuture.DONE;
	}

	
	/**
	 *  First init step of agent.
	 */
	protected IFuture	init0()
	{
//		assert isAgentThread();
		assert !getAgentAdapter().isExternalThread();
		
		final Future	ret	= new Future();
		
		// Init the external access
//		ea = new ExternalAccessFlyweight(state, ragent);
		
		// Get the services.
		final boolean services[]	= new boolean[3];
		SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				clockservice	= (IClockService)result;
				boolean	startagent;
				synchronized(services)
				{
					services[0]	= true;
					startagent	= services[0] && services[1] && services[2];// && services[3];
				}
				if(startagent)
				{
					ret.setResult(null);
				}
			}
		}));
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				cms	= (IComponentManagementService)result;
				boolean	startagent;
				synchronized(services)
				{
					services[1]	= true;
					startagent	= services[0] && services[1] && services[2];// && services[3];
				}
				if(startagent)
					ret.setResult(null);
			}
		}));
		SServiceProvider.getService(getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				msgservice	= (IMessageService)result;
				boolean	startagent;
				synchronized(services)
				{
					services[2]	= true;
					startagent	= services[0] && services[1] && services[2];// && services[3];
				}
				if(startagent)
					ret.setResult(null);
			}
		}));

		// Previously done in createStartAgentRule
		Map parents = new HashMap(); 
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_initparents, parents);
		AgentRules.createCapabilityInstance(state, ragent, parents);
		
		return ret;
	}
	
	/**
	 *  Second init step of agent.
	 */
	protected IFuture	init1()
	{
//		assert isAgentThread();
		assert !getAgentAdapter().isExternalThread();
		
		return AgentRules.initializeCapabilityInstance(state, ragent);
	}
	
	//-------- IKernelAgent interface --------
	
	//	Lock lock = new ReentrantLock(); 
	/**
	 *  Main method to perform agent execution.
	 *  Whenever this method is called, the agent executes.
	 *  The platform can provide different execution models for agents
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked. 
	 *  @return True, when there are more steps waiting to be executed. 
	 */
	public boolean executeStep()
	{
		try
		{
			// check st!=null is not enough as agent could be terminated during init.
			// In this case the rulesystem may never has been inited.
//			String st = (String)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state);
			if(rulesystem.isInited())
			{
				// Hack!!! platform should inform about ext entries to update agenda.
				Activation	act	= rulesystem.getAgenda().getLastActivation();
				state.getProfiler().start(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
				state.expungeStaleObjects();
				state.notifyEventListeners();
				state.getProfiler().stop(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
	
				if(!rulesystem.getAgenda().isEmpty())
				{
					notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION,
							IComponentChangeEvent.SOURCE_CATEGORY_EXECUTION, null, null, getComponentIdentifier(), getComponentDescription().getCreationTime(), null));
					rulesystem.getAgenda().fireRule();
	
					act	= rulesystem.getAgenda().getLastActivation();
		//			System.err.println("here: "+act+", "+rulesystem.getAgenda().getActivations());
					state.getProfiler().start(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
					state.expungeStaleObjects();
					state.notifyEventListeners();
					state.getProfiler().stop(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
					notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL,
							IComponentChangeEvent.SOURCE_CATEGORY_EXECUTION, null, null, getComponentIdentifier(), getComponentDescription().getCreationTime(), null));
				}
	
				return !rulesystem.getAgenda().isEmpty();
			}
			else
			{
				// still in init
				return false;
			}
			
		}
		catch(Throwable e)
		{
			// Catch fatal error and cleanup before propagating error to platform.
			cleanup();
			if(e instanceof RuntimeException)
				throw (RuntimeException)e;
			else if(e instanceof Error)
				throw (Error)e;
			else // Shouldn't happen!? 
				throw new RuntimeException(e);
		}
	}
	
	long last;
	int lastmax;
	int lastmin;
	String	lastmsg;

	/**
	 *  Inform the agent that a message has arrived.
	 *  @param message The message that arrived.
	 */
	public void messageArrived(final IMessageAdapter message)
	{
//		System.out.println("messageArrived: "+getAgentAdapter().getComponentIdentifier().getLocalName()+", "+message);
		// Notify/ask tools that we are about to receive a message.
//		boolean	toolmsg	= false;
//		for(int i=0; !toolmsg && i<tooladapters.length; i++)
//			toolmsg	= tooladapters[i].messageReceived(message);

		// Handle normal messages.
//		if(!toolmsg)
//		{
			getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					state.addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_inbox, message);
//					System.out.println("message moved to inbox: "+getAgentAdapter().getComponentIdentifier().getLocalName()
//						+"("+state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state)+")"+", "+message);
				}
			});
//		}
	}
	
	/**
	 *  Inform the agent that a message has arrived.
	 *  @param message The message that arrived.
	 */
	public void streamArrived(final IConnection con)
	{
//		System.out.println("messageArrived: "+getAgentAdapter().getComponentIdentifier().getLocalName()+", "+message);
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put(SFipa.CONTENT, con);
		msg.put(SFipa.SENDER, con.getInitiator());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{con.getParticipant()});
		messageArrived(new DefaultMessageAdapter(msg, SFipa.FIPA_MESSAGE_TYPE));
	}

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
			ret.setException(new RuntimeException("Component not running: "+getComponentIdentifier().getName()));
		}
		
		return ret;
	}
	
	/**
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 */
	public IExternalAccess getExternalAccess()
	{
		return ea;
//		return new ExternalAccessFlyweight(state, ragent);
		
//		final Future ret = new Future();
//		
//		getAgentAdapter().invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING.equals(state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state)))
//				{
//					if(eal==null)
//						eal	= new HashSet();
//					eal.add(new DelegationResultListener(ret));
//				}
//				else
//				{
//					ret.setResult(new ExternalAccessFlyweight(state, ragent));
//				}
//			}
//		});
//		
//		return ret;
	}

	/**
	 *  Get the class loader of the agent.
	 *  The agent class loader is required to avoid incompatible class issues,
	 *  when changing the platform class loader while agents are running. 
	 *  This may occur e.g. when decoding messages and instantiating parameter values.
	 *  @return	The agent class loader. 
	 */
	public ClassLoader getClassLoader()
	{
//		return model.getModelInfo().getClassLoader();
		return model.getState().getTypeModel().getClassLoader();
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
//		System.err.println("Cleanup: "+state);

		BDIInterpreter.interpreters.remove(state);
		
		for(Iterator it=externalthreads.iterator(); it.hasNext(); )
		{
			Throwable[]	exception	= (Throwable[])it.next();
			synchronized(exception)
			{
				exception[0] = new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier());
				exception[0].fillInStackTrace();
				exception.notify();
				it.remove();
			}
		}
//		System.out.println(BDIInterpreter.interpreters.size());
	}

	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public IFuture	componentCreated(IComponentDescription desc, IModelInfo model)
	{
		return IFuture.DONE;
	}

	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public IFuture	componentDestroyed(IComponentDescription desc)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
//		assert isAgentThread();
		assert !getAgentAdapter().isExternalThread();
		
		boolean	isatbreakpoint	= false;
		
		Object	cs	= state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state);
		if(cs!=null && !OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED.equals(cs))
		{
			Set	bps	= new HashSet(Arrays.asList(breakpoints));	// Todo: cache set across invocations for speed?
			Iterator	it	= getRuleSystem().getAgenda().getActivations().iterator();
			while(!isatbreakpoint && it.hasNext())
			{
				IRule	rule	= ((Activation)it.next()).getRule();
				isatbreakpoint	= bps.contains(rule.getName());
			}
		}
		// else still in init
		
		return isatbreakpoint;
	}

	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger(Object rcapa)
	{
		Logger ret = adapter.getLogger();
		
		if(ragent!=rcapa)
		{
			// get logger with unique capability name
			// todo: implement getDetailName()
			//String name = getDetailName();
			
			List path = new ArrayList();
			findSubcapability(ragent, rcapa, path);
			StringBuffer buf = new StringBuffer();
			buf.append(ret.getName()).append(".");
			for(int i=0; i<path.size(); i++)
			{
				Object caparef = path.get(i);
				String name = (String)state.getAttributeValue(caparef, OAVBDIRuntimeModel.capabilityreference_has_name);
				buf.append(name);
				if(i+1<path.size())
					buf.append(".");
			}
			String name = buf.toString();
			ret = LogManager.getLogManager().getLogger(name);
			
			// if logger does not already exists, create it
			if(ret==null)
			{
				// Hack!!! Might throw exception in applet / webstart.
				try
				{
					ret = Logger.getLogger(name);
					initLogger(path, ret);
					//System.out.println(logger.getParent().getLevel());
				}
				catch(SecurityException e)
				{
					// Hack!!! For applets / webstart use anonymous logger.
					ret	= Logger.getAnonymousLogger();
					initLogger(path, ret);
				}
			}
		}
		
		return ret;
	}
	
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
	
	/**
	 *  Init the logger with capability settings.
	 *  @param path The list of capability references from agent to subcapability.
	 *  @param logger The logger.
	 */
	protected void initLogger(List path, Logger logger)
	{
		// Outer settings overwrite inner settings.
		Level	level	= null;
		Boolean	useparent	= null;
		Level	addconsole	= null;
		String	logfile	= null;
		Object	handlers = null;
		
		for(int i=-1; i<path.size(); i++)
		{
			Object	rcapa	= i==-1 ? ragent
				: state.getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_capability);
			if(level==null)
			{
				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.level");
				level	= prop!=null ? (Level)prop : null;
			}
			if(useparent==null)
			{
				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.useParentHandlers");
				useparent	= prop!=null ? (Boolean)prop : null;
			}
			if(addconsole==null)
			{
				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.addConsoleHandler");
				addconsole	= prop!=null ? (Level)prop : null;
			}
			if(logfile==null)
			{
				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.file");
				logfile	= prop!=null ? (String)prop : null;
			}
			if(logfile==null)
			{
				Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.handlers");
				handlers	= prop!=null ? prop : null;
			}
		}
		
		// the level of the logger
		logger.setLevel(level==null? Level.WARNING: level);

		// if logger should use Handlers of parent (global) logger
		// the global logger has a ConsoleHandler(Level:INFO) by default
		if(useparent!=null)
		{
			logger.setUseParentHandlers(useparent.booleanValue());
		}
			
		// add a ConsoleHandler to the logger to print out
        // logs to the console. Set Level to given property value
		if(addconsole!=null)
		{
			Handler console;
			/*if[android]
			console = new jadex.commons.android.AndroidHandler();
			 else[android]*/
			console = new ConsoleHandler();
			/* end[android]*/
            console.setLevel(addconsole);
            logger.addHandler(console);
        }
		
		// Code adapted from code by Ed Komp: http://sourceforge.net/forum/message.php?msg_id=6442905
		// if logger should add a filehandler to capture log data in a file. 
		// The user specifies the directory to contain the log file.
		// $scope.getAgentName() can be used to have agent-specific log files 
		//
		// The directory name can use special patterns defined in the
		// class, java.util.logging.FileHandler, 
		// such as "%h" for the user's home directory.
		// 
		if(logfile!=null)
		{
		    try
		    {
			    Handler fh	= new FileHandler(logfile);
		    	fh.setFormatter(new SimpleFormatter());
		    	logger.addHandler(fh);
		    }
		    catch (IOException e)
		    {
		    	System.err.println("I/O Error attempting to create logfile: "
		    		+ logfile + "\n" + e.getMessage());
		    }
		}
		
		if(handlers!=null)
		{
			if(handlers instanceof Handler)
			{
				logger.addHandler((Handler)handlers);
			}
			else if(SReflect.isIterable(handlers))
			{
				for(Iterator it=SReflect.getIterator(handlers); it.hasNext(); )
				{
					Object obj = it.next();
					if(obj instanceof Handler)
					{
						logger.addHandler((Handler)obj);
					}
					else
					{
						logger.warning("Property is not a logging handler: "+obj);
					}
				}
			}
			else
			{
				logger.warning("Property 'logging.handlers' must be Handler or list of handlers: "+handlers);
			}
		}
	}
	
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

			synchronized(gexecutors)
			{
				// Initialize for each kernel one time.
				Map	global	= (Map)gexecutors.get(getKernelProperties());
				if(global==null)
				{
					global	= new HashMap();
					gexecutors.put(getKernelProperties(), global);
					
//					Property[] props = getKernelProperties().getProperties("planexecutor");

//					// claas: commented because its not used anywhere 
//					SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
//					fetcher.setValue("$platformname", getAgentAdapter().getPlatform().getName());
					
					Iterator it = getKernelProperties().keySet().iterator();
					while(it.hasNext())
					{
						String key = (String)it.next();
						if(key.startsWith("planexecutor"))
						{
							String tmp = key.substring(13);
//							System.out.println("PE:"+tmp);
							global.put(tmp, getKernelProperties().get(key));
						}
					}
					
					/*for(int i=0; i<props.length; i++)
					{
						global.put(props[i].getName(), props[i].getJavaObject(fetcher));
					}*/
				}
				ret	= (IPlanExecutor)global.get(name);
				if(ret!=null)
					planexecutors.put(name, ret);
				else
					System.out.println("Warning: No plan executor for plan type '"+name+"'.");
			}
		}
		return ret;
	}
	
	/** global plan executors, cached for speed. */
	// hack!!!
	protected static Map gexecutors	= new HashMap();
	
	/**
	 *  Get the adapter agent.
	 *  @return The adapter agent.
	 */
	public IComponentAdapter getAgentAdapter()
	{
		return this.adapter;
	}
	
	/**
	 *  Get the agent state.
	 *  @return The agent state.
	 */
	public IOAVState getState()
	{
		return this.state;
	}

	/**
	 *  Get the cached clock service.
	 */
	// hack!!! to avoid dealing with futures.
	public IClockService	getClockService()
	{
		return clockservice;
	}

	/**
	 *  Get the cached component management service.
	 */
	// hack!!! to avoid dealing with futures.
	public IComponentManagementService	getCMS()
	{
		return cms;
	}

	/**
	 *  Get the cached message service.
	 */
	// hack!!! to avoid dealing with futures.
	public IMessageService	getMessageService()
	{
		return msgservice;
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
		if(getComponentAdapter().isExternalThread())
		{
			System.err.println("Unsynchronized internal thread.");
			Thread.dumpStack();

			final boolean[] notified = new boolean[1];
			final Throwable[] exception = new Throwable[1];
			externalthreads.add(exception);
			
			// Add external will throw exception if action execution cannot be done.
//			System.err.println("invokeSynchonized("+code+"): adding");
			getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						code.run();

//						// Assert for testing state consistency (slow -> comment out for release!)
//						assert rulesystem.getState().getUnreferencedObjects().isEmpty()
//							: getAgentAdapter().getComponentIdentifier().getLocalName()
//							+ ", " + code
//							+ ", " + rulesystem.getState().getUnreferencedObjects();
					}
					catch(Throwable e)
					{
						exception[0]	= e;
					}
					
					synchronized(exception)
					{
						exception.notify();
						notified[0] = true;
						externalthreads.remove(exception);
					}
				}
				
				public String	toString()
				{
					return code.toString();
				}
			});
			
			try
			{
//				System.err.println("invokeSynchonized("+code+"): waiting");
				synchronized(exception)
				{
					if(!notified[0])
					{
						if(BDIInterpreter.getInterpreter(state)!=null)
						{
//							System.err.println("Waiting: "+state);
							getAgentAdapter().getLogger().warning("Executing synchronized code (might lead to deadlocks): "+code);
							exception.wait();
//							System.err.println("Continued: "+state);
						}
						else
						{
							throw new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier());
						}
					}
				}
//				System.err.println("invokeSynchonized("+code+"): returned");
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			if(exception[0] instanceof RuntimeException)
			{
				throw (RuntimeException)exception[0];
			}
			else if(exception[0]!=null)
			{
				throw new RuntimeException(exception[0]);
			}
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
		return scheduleStep(step, ragent);
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public <T> IFuture<T> scheduleStep(final Object step, final Object scope)
	{
		final Future<T> ret = new Future<T>();
		
		if(getComponentAdapter().isExternalThread()
			|| getState().getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state)==null)
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						if(state.containsObject(ragent))
						{
							if(getState().getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state)==null)
							{
								// Hack!!! During init phase execute directly as rule engine isn't running.
								try
								{
									((IComponentStep)step).execute(getInternalAccess())
										.addResultListener(new DelegationResultListener(ret));
								}
								catch(Exception e)
								{
									ret.setException(e);
								}
							}
							else
							{
								getState().addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_actions, new Object[]{step, ret, scope});
							}
						}
						else
						{
							ret.setException(new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier()));
						}
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			if(getState().containsObject(ragent))
			{
				getState().addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_actions, new Object[]{step, ret, scope});
			}
			else
			{
				// Happens when timeout listener should be added on cleanup.
				ret.setException(new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier()));				
			}
		}

		return ret;		
	}
	
	/**
	 *  Execute some code on the component's thread.
	 *  Unlike scheduleStep(), the action will also be executed
	 *  while the component is suspended.
	 *  @param action	Code to be executed on the component's thread.
	 *  @return The result of the step.
	 */
	public IFuture scheduleImmediate(final IComponentStep step, final Object scope)
	{
		final Future ret = new Future();
		
		try
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					try
					{
						step.execute(new CapabilityFlyweight(state, scope))
							.addResultListener(new DelegationResultListener(ret));
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Set the current plan thread.
	 *  @param planthread The planthread.
	 */ 
	public void setPlanThread(Thread planthread)
	{
		this.planthread = planthread;
	}
	
//	/**
//	 *  Check if the agent thread is accessing.
//	 *  @return True, if access is ok.
//	 */ 
//	public boolean isAgentThread()
//	{
//		return initthread==Thread.currentThread();
//	}
	
	/**
	 *  Check if the agent thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isPlanThread()
	{
		return planthread==Thread.currentThread();
	}
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isExternalThread()
	{
		return initthread!=Thread.currentThread() && !isPlanThread();
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
	protected void setCurrentPlan(Object currentplan)
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
	
	/**
	 *  Get the kernel properties.
	 *  @return The kernel properties.
	 */
	public Map getKernelProperties()
	{
		return kernelprops;
	}
	
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
	
	/**
	 *  Get the parent of the agent.
	 *  @return The external access of the parent.
	 */
	public IExternalAccess getParent()
	{
		return parent;
	}
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
//		assert container!=null;
		if(container==null)
			container = createServiceContainer();
		return container;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the interpreter for an agent object.
	 */
	public static BDIInterpreter	getInterpreter(IOAVState state)
	{
		return (BDIInterpreter)interpreters.get(state);
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

	/**
	 *  Get the component adapter.
	 *  @return The component adapter.
	 */
	public IComponentAdapter	getComponentAdapter()
	{
		return adapter;
	}

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
	 *  Init the arguments and results.
	 */
	public IFuture initArguments(IModelInfo model, final String config, Map arguments)
	{
		// Do nothing: args and results are inited as beliefs.
		return IFuture.DONE;
	}
	
	/**
	 *  Add a default value for an argument (if not already present).
	 *  Called once for each argument during init.
	 *  @param name	The argument name.
	 *  @param value	The argument value.
	 */
	public boolean	addArgument(String name, Object value)
	{
		// Not supported by BDI XML schema -> Called during belief init to make arguments available to outside.
		if(arguments==null)
			arguments = new HashMap();
		return arguments.put(name, value) == null;
	}

	/**
	 *  Add a default value for a result (if not already present).
	 *  Called once for each result during init.
	 *  @param name	The result name.
	 *  @param value	The result value.
	 */
	public void	addDefaultResult(String name, Object value)
	{
		// Not supported by BDI XML schema -> Shouldn't be called
		throw new UnsupportedOperationException();		
	}

	/**
	 *  Get the internal access.
	 *  @return The internal access.
	 */
	public IInternalAccess	getInternalAccess()
	{
		return new CapabilityFlyweight(state, initcapa!=null ? findSubcapability(initcapa) : ragent);
	}
		
	/**
	 *  Get the component listeners.
	 *  @return The component listeners.
	 */
	public IComponentListener[]	getComponentListeners()
	{
		Collection	coll	= getState().getAttributeValues(ragent, OAVBDIRuntimeModel.agent_has_componentlisteners);
		return coll!=null ? (IComponentListener[])coll.toArray(new IComponentListener[coll.size()]) : new IComponentListener[0];
	}
	
	/**
	 *  Remove component listener.
	 */
	public IFuture	removeComponentListener(IComponentListener listener)
	{
		getState().removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_componentlisteners, listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Add component listener.
	 */
	public IFuture	addComponentListener(IComponentListener listener)
	{
//		System.out.println("Added: "+listener+", "+getState().getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state));
		getState().addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_componentlisteners, listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Add an extension instance.
	 *  @param extension The extension instance.
	 */
	public void	addExtension(String name, IExtensionInstance ext)
	{
		if(extensions==null)
		{
			extensions = new HashMap();
		}
		extensions.put(name, ext);
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IExtensionInstance getExtension(String name)
	{
		return extensions==null? null: (IExtensionInstance)extensions.get(name);
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IExtensionInstance[] getExtensions()
	{
		return extensions==null? new IExtensionInstance[0]: 
			(IExtensionInstance[])extensions.values().toArray(new IExtensionInstance[extensions.size()]);
	}
	
	/**
	 *  Get the properties.
	 *  @return the properties.
	 */
	public Map getProperties()
	{
		return properties;
	}
	
	/**
	 *  Get the properties for a subcapability.
	 *  @return the properties.
	 */
	public Map getProperties(Object rcapa)
	{
		// Todo
		return properties==null? Collections.EMPTY_MAP: properties;
	}
	
	/**
	 *  Add a property value.
	 *  @param name The name.
	 *  @param val The value.
	 */
	public void addProperty(String name, Object val)
	{
		if(properties==null)
			properties = new HashMap();
		properties.put(name, val);
	}

	/**
	 *  Get the arguments.
	 */
	public Map getArguments()
	{
		return arguments==null? Collections.EMPTY_MAP: arguments;
	}
	
	/**
	 *  Get the component listeners.
	 *  @return The component listeners.
	 */
	public Collection getInternalComponentListeners()
	{
		// Todo: support this!?
		return Collections.EMPTY_LIST;
	}
	
	/**
	 *  Get the service prefix.
	 *  @return The prefix for required services.
	 */
	public String getServicePrefix()
	{
		return findServicePrefix(initcapa!=null ? findSubcapability(initcapa) : ragent);
	}
	
	/**
	 *  Get the bindings.
	 *  @return The bindings.
	 */
	public RequiredServiceBinding[]	getBindings()
	{
		return bindings;
	}

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

	/**
	 *  Get the copy.
	 *  @return the copy.
	 */
	public boolean isCopy()
	{
		return copy;
	}
	
	/**
	 *  Create the service container.
	 *  @return The service conainer.
	 */
	public IServiceContainer createServiceContainer()
	{
		assert container==null;
		return new ComponentServiceContainer(adapter, getComponentAdapter().getDescription().getType(), getInternalAccess());
	}
	
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
		assert !getComponentAdapter().isExternalThread();
		
		// todo: store results only within listener?!
		if(results==null)
			results	= new HashMap<String, Object>();
		results.put(name, value);
		
		resultlistener.intermediateResultAvailable(new Tuple2<String, Object>(name, value));
	}

	/**
	 *  Get the realtime.
	 *  @return The realtime.
	 */
	public boolean isRealtime()
	{
		return realtime;
	}
	
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
	
}
