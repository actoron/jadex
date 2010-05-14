package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBDIExternalAccess;
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
import jadex.bdi.runtime.impl.ExternalAccessFlyweight;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageAdapter;
import jadex.commons.collection.LRU;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.ISynchronizator;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
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
public class BDIInterpreter implements IComponentInstance //, ISynchronizator
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
	
	//-------- recreate on init (no state) --------
	
	/** The event reificator creates changeevent objects for relevant state changes. */
	protected EventReificator reificator;
	
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
	protected Set	externalthreads;
	
	/** The get-external-access listeners to be notified after init. */
	protected Set	eal;
	
	//-------- constructors --------
	
	/**
	 *  Create an agent interpreter for the given agent.
	 *  @param state	The state. 
	 *  @param magent	The reference to the agent model in the state.
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 */
	public BDIInterpreter(IComponentAdapter adapter, IOAVState state, OAVAgentModel model, 
		String config, Map arguments, IExternalAccess parent, Map kernelprops)
	{	
		this.adapter = adapter;
		this.state = state;
		this.model = model;
		this.parent	= parent;
		this.kernelprops = kernelprops;
		this.planexecutors = new HashMap();
		this.volcache = new LRU(0);	// 50
		this.stacache = new LRU(20);
		this.microplansteps = true;
		this.externalthreads	= Collections.synchronizedSet(SCollection.createLinkedHashSet());

		state.setSynchronizator(new ISynchronizator()
		{
			public boolean isExternalThread()
			{
				return BDIInterpreter.this.isExternalThread();
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
				
		// Hack! todo:
		interpreters.put(state, this);
		
		// Evaluate arguments if necessary
		// Hack! use constant
		if(arguments!=null && arguments.get("evaluation_language")!=null)
		{
			arguments.remove("evaluation_language");
			// todo: support more than Java language parsers
			JavaCCExpressionParser parser = new JavaCCExpressionParser();
			for(Iterator it=arguments.keySet().iterator(); it.hasNext(); )
			{
				Object key = it.next();
				try
				{
					IParsedExpression pex = parser.parseExpression((String)arguments.get(key), null, null, state.getTypeModel().getClassLoader());
					Object val = pex.getValue(null);
					arguments.put(key, val);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException("Could not evaluate argument: "+key);
				}
			}
		}
		
		// Set up initial state of agent
		ragent = state.createRootObject(OAVBDIRuntimeModel.agent_type);
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_name, adapter.getComponentIdentifier().getName());
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_localname, adapter.getComponentIdentifier().getLocalName());
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.element_has_model, model.getHandle());
		state.setAttributeValue(ragent, OAVBDIRuntimeModel.capability_has_configuration, config);
		if(arguments!=null && !arguments.isEmpty())
			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_arguments, arguments);
		
		this.reificator	= new EventReificator(state, ragent);
		
		// Initialize rule system.
		rulesystem = new RuleSystem(state, model.getMatcherFunctionality().getRulebase(), model.getMatcherFunctionality(), new PriorityAgenda());
		rulesystem.init();
		
		if(kernelprops!=null)
		{
			Boolean mps = (Boolean)kernelprops.get("microplansteps");
			if(mps!=null)
				this.microplansteps = mps.booleanValue();
		}		
		
		// This is the clean way to init the logger, but since 
		// Java 7 the LogManager is a memory leak
		// Also in Java 7 the memory leak exists :-(
		// So only access logger if really necessary
//		Logger logger = adapter.getLogger();
//		initLogger(ragent, logger);
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
			// Hack!!! platform should inform about ext entries to update agenda.
			Activation	act	= rulesystem.getAgenda().getLastActivation();
//			System.out.println("here: "+act);
			state.getProfiler().start(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
			state.expungeStaleObjects();
			state.notifyEventListeners();
			state.getProfiler().stop(IProfiler.TYPE_RULE, act!=null?act.getRule():null);

			rulesystem.getAgenda().fireRule();

			// Necessary because in step mode state changes may happen and 
			// event listeners need to be notified!
			/*Activation*/	act	= rulesystem.getAgenda().getLastActivation();
//			System.out.println("here: "+act);
			state.getProfiler().start(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
			state.expungeStaleObjects();
			state.notifyEventListeners();
			state.getProfiler().stop(IProfiler.TYPE_RULE, act!=null?act.getRule():null);

			return !rulesystem.getAgenda().isEmpty(); 
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
	 *  Request agent to kill itself.
	 */
	public void killComponent(final IResultListener listener)
	{
		getAgentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{
				if(listener!=null)
					state.addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_killlisteners, listener);
				Object cs = state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state);
				if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING.equals(cs) 
					|| OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
				{
					AgentRules.startTerminating(state, ragent);
				}
			}
		});
	}
	
	/**
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 */
	public void getExternalAccess(final IResultListener listener)
	{
		getAgentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{
				if(listener!=null)
				{
					if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_CREATING.equals(state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state)))
					{
						if(eal==null)
							eal	= new HashSet();
						eal.add(listener);
					}
					else
					{
						listener.resultAvailable(getAgentAdapter().getComponentIdentifier(), new ExternalAccessFlyweight(state, ragent));
					}
				}
			}
		});
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
		return getModel().getTypeModel().getClassLoader();
	}
	
	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  @return The results map (name -> value). 
	 */
	public Map getResults()
	{
		Map	res	= (Map)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_results);
		return res!=null ? Collections.unmodifiableMap(res) : Collections.EMPTY_MAP;
	}

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
				exception[0] = new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier().getName());
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
	public void	componentCreated(IComponentDescription desc, ILoadableComponentModel model)
	{
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public void	componentDestroyed(IComponentDescription desc)
	{
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
		boolean	isatbreakpoint	= false;
		Set	bps	= new HashSet(Arrays.asList(breakpoints));	// Todo: cache set across invocations for speed?
		Iterator	it	= getRuleSystem().getAgenda().getActivations().iterator();
		while(!isatbreakpoint && it.hasNext())
		{
			IRule	rule	= ((Activation)it.next()).getRule();
			isatbreakpoint	= bps.contains(rule.getName());
		}
		
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
					initLogger(rcapa, ret);
					//System.out.println(logger.getParent().getLevel());
				}
				catch(SecurityException e)
				{
					// Hack!!! For applets / webstart use anonymous logger.
					ret	= Logger.getAnonymousLogger();
					initLogger(rcapa, ret);
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
	protected boolean findSubcapability(Object rcapa, Object targetcapa, List path)
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
	
	//-------- other methods --------
	
	/**
	 *  Init the logger with capability settings.
	 *  @param logger The logger.
	 */
	protected void initLogger(Object rcapa, Logger logger)
	{
		// get logging properties (from ADF)
		// the level of the logger
		// can be Integer or Level
		
		Object prop = AgentRules.getPropertyValue(state, rcapa, "logging.level");
		Level level = prop==null? Level.SEVERE: (Level)prop;
		logger.setLevel(level);

		// if logger should use Handlers of parent (global) logger
		// the global logger has a ConsoleHandler(Level:INFO) by default
		prop = AgentRules.getPropertyValue(state, rcapa, "logging.useParentHandlers");
		if(prop!=null)
		{
			logger.setUseParentHandlers(((Boolean)prop).booleanValue());
		}
			
		// add a ConsoleHandler to the logger to print out
        // logs to the console. Set Level to given property value
		prop = AgentRules.getPropertyValue(state, rcapa, "addConsoleHandler");
		if(prop!=null)
		{
            ConsoleHandler console = new ConsoleHandler();
            console.setLevel(Level.parse(prop.toString()));
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
		String logfile =	(String)AgentRules.getPropertyValue(state, rcapa, "logging.file");
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
	 *  Get the model.
	 *  @return The model.
	 */
	public OAVAgentModel getModel()
	{
		return model;
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
	 *  Kill the component.
	 */
	public void	killAgent()
	{
		((IComponentManagementService)adapter.getServiceContainer()
			.getService(IComponentManagementService.class))
			.destroyComponent(adapter.getComponentIdentifier());
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
		if(isExternalThread())
		{
//			System.err.println("Unsynchronized internal thread.");
//			Thread.dumpStack();

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
							exception.wait();
//							System.err.println("Continued: "+state);
						}
						else
						{
							throw new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier().getName());
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
	 *  Add an action from external thread.
	 *  The contract of this method is as follows:
	 *  The agent ensures the execution of the external action, otherwise
	 *  the method will throw a agent terminated exception.
	 *  @param action The action.
	 * /
	public void invokeLater(Runnable action)
	{
		synchronized(ext_entries)
		{
			if(ext_forbidden)
			{
				throw new ComponentTerminatedException("External actions cannot be accepted " +
					"due to terminated agent state: "+ragent);
			}
			ext_entries.add(action);
		}
		adapter.wakeup();
	}*/
	
	/**
	 *  Set the current plan thread.
	 *  @param planthread The planthread.
	 */ 
	public void setPlanThread(Thread planthread)
	{
		this.planthread = planthread;
	}
	
	/**
	 *  Check if the agent thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isAgentThread()
	{
		return !adapter.isExternalThread();
	}
	
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
		return !isAgentThread() && !isPlanThread();
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
	 * @return
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
		if(microplansteps)
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
		if(microplansteps)
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
	 *  Get the tool adapters.
	 *  @return The tool adapters.
	 * /
	public IToolAdapter[] getToolAdapters()
	{
		return tooladapters;
	}*/
	
	/**
	 *  Get the flyweight cache.
	 *  @return The flyweight cache.
	 */
	public Map getFlyweightCache(Class type)
	{
//		System.out.println("stacache: "+stacache.size());
//		System.out.println("volcache: "+volcache.size());
		return stacacheelems.contains(type)? stacache: volcache;
	}
	
	/**
	 *  Get the parent of the agent.
	 */
	public IExternalAccess getParent()
	{
		return parent;
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
		RULEBASE.addRule(AgentRules.createStartAgentRule());
		RULEBASE.addRule(AgentRules.createTerminatingEndAgentRule());
		RULEBASE.addRule(AgentRules.createTerminateAgentRule());
		RULEBASE.addRule(AgentRules.createRemoveChangeEventRule());
//		RULEBASE.addRule(AgentRules.createExecuteActionRule());
//		RULEBASE.addRule(AgentRules.createTerminatingStartAgentRule());

		// Listener rules.
		RULEBASE.addRule(ListenerRules.createInternalEventListenerRule());
		RULEBASE.addRule(ListenerRules.createMessageEventListenerRule());
		RULEBASE.addRule(ListenerRules.createBeliefChangedListenerRule());
		RULEBASE.addRule(ListenerRules.createBeliefSetListenerRule());
		RULEBASE.addRule(ListenerRules.createGoalListenerRule());
		RULEBASE.addRule(ListenerRules.createPlanListenerRule());
		RULEBASE.addRule(ListenerRules.createAgentTerminationListenerRule());
		
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
}
