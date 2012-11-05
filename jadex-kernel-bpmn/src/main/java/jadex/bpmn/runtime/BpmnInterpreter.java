package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.bpmn.runtime.handler.DefaultStepHandler;
import jadex.bpmn.runtime.handler.EventEndErrorActivityHandler;
import jadex.bpmn.runtime.handler.EventEndSignalActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateErrorActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateMessageActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateMultipleActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateNotificationHandler;
import jadex.bpmn.runtime.handler.EventIntermediateTimerActivityHandler;
import jadex.bpmn.runtime.handler.EventMultipleStepHandler;
import jadex.bpmn.runtime.handler.GatewayORActivityHandler;
import jadex.bpmn.runtime.handler.GatewayParallelActivityHandler;
import jadex.bpmn.runtime.handler.GatewayXORActivityHandler;
import jadex.bpmn.runtime.handler.SubProcessActivityHandler;
import jadex.bpmn.runtime.handler.TaskActivityHandler;
import jadex.bpmn.runtime.task.ExecuteStepTask;
import jadex.bpmn.tools.ProcessThreadInfo;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.kernelbase.AbstractInterpreter;
import jadex.kernelbase.InterpreterFetcher;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
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

/**
 *  The bpmn interpreter is able to execute bpmn diagrams.
 *  
 *  Arguments are treated in BPMN as parameters, i.e. the argument
 *  values will be fed into corresponding parameters.
 */
public class BpmnInterpreter extends AbstractInterpreter implements IInternalAccess
{	
	//-------- static part --------
	
	/** Constant for step event. */
	public static final String TYPE_ACTIVITY = "activity";
	
	/** The change event prefix denoting a thread event. */
	public static final String	TYPE_THREAD	= "thread";
	
	/** The activity execution handlers (activity type -> handler). */
	public static final Map DEFAULT_ACTIVITY_HANDLERS;
	
	/** The step execution handlers (activity type -> handler). */
	public static final Map DEFAULT_STEP_HANDLERS;

	/** The flag for all pools. */
	public static final String ALL = "All";
	
	static
	{
		Map stephandlers = new HashMap();
		
		stephandlers.put(IStepHandler.STEP_HANDLER, new DefaultStepHandler());
		stephandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE, new EventMultipleStepHandler());
		
		DEFAULT_STEP_HANDLERS = Collections.unmodifiableMap(stephandlers);
		
		Map activityhandlers = new HashMap();
		
		// Task/Subprocess handler.
		activityhandlers.put(MBpmnModel.TASK, new TaskActivityHandler());
		activityhandlers.put(MBpmnModel.SUBPROCESS, new SubProcessActivityHandler());
	
		// Gateway handler.
		activityhandlers.put(MBpmnModel.GATEWAY_PARALLEL, new GatewayParallelActivityHandler());
		activityhandlers.put(MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE, new GatewayXORActivityHandler());
		activityhandlers.put(MBpmnModel.GATEWAY_DATABASED_INCLUSIVE, new GatewayORActivityHandler());
	
		// Initial events.
		// Options: empty, message, rule, timer, signal, multi, link
		// Missing: link 
		// Note: non-empty start events are currently only supported in subworkflows
		// It is currently not possible to start a top-level workflow using the other event types,
		// i.e. the creation of a workflow is not supported. 
		activityhandlers.put(MBpmnModel.EVENT_START_EMPTY, new DefaultActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_TIMER, new EventIntermediateTimerActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_MESSAGE, new EventIntermediateMessageActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_MULTIPLE, new EventIntermediateMultipleActivityHandler());
//		activityhandlers.put(MBpmnModel.EVENT_START_RULE, new EventIntermediateNotificationHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_RULE, new DefaultActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_SIGNAL, new EventIntermediateNotificationHandler());
			
		// Intermediate events.
		// Options: empty, message, rule, timer, error, signal, multi, link, compensation, cancel
		// Missing: link, compensation, cancel
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_EMPTY, new DefaultActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE, new EventIntermediateMessageActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new EventIntermediateNotificationHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_TIMER, new EventIntermediateTimerActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_ERROR, new EventIntermediateErrorActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE, new EventIntermediateMultipleActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL, new EventIntermediateNotificationHandler());
//		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new UserInteractionActivityHandler());
		
		// End events.
		// Options: empty, message, error, compensation, terminate, signal, multi, cancel, link
		// Missing: link, compensation, cancel, terminate, signal, multi
		activityhandlers.put(MBpmnModel.EVENT_END_EMPTY, new DefaultActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_END_ERROR, new EventEndErrorActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_END_MESSAGE, new EventIntermediateMessageActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_END_SIGNAL, new EventEndSignalActivityHandler());

		DEFAULT_ACTIVITY_HANDLERS = Collections.unmodifiableMap(activityhandlers);
	}
	
	//-------- attributes --------
	
	/** The micro agent model. */
	protected MBpmnModel model;
	
	// todo: allow multiple pools/lanes?
	/** The configuration. */
	protected String pool;
	
	/** The configuration. */
	protected String lane;
	
	/** The activity handlers. */
	protected Map activityhandlers;
	
	/** The step handlers. */
	protected Map stephandlers;

	/** The thread context. */
	protected ThreadContext	context;
	
	/** The context variables. */
	protected Map variables;
	
	/** The finishing flag marker. */
	protected boolean finishing;
	
	/** The messages waitqueue. */
	protected List messages;

	/** The streams waitqueue. */
	protected List streams;

	/** The inited future. */
	protected Future<Void> inited;
	
	/** The started flag. */
	protected boolean started;
	
	/** The thread id counter. */
	protected int idcnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bpmn process.
	 *  @param adapter The adapter.
	 */
	// Constructor for bdi plan interpreter
	public BpmnInterpreter(IComponentAdapter adapter, MBpmnModel model, Map arguments, 
		String config, final IExternalAccess parent, Map activityhandlers, Map stephandlers, 
		IValueFetcher fetcher, IComponentManagementService cms, IClockService cs, IMessageService ms,
		IServiceContainer container)
	{
		super(null, model.getModelInfo(), config, null, parent, null, true, true, null, new Future<Void>());
		construct(model, activityhandlers, stephandlers);		
		this.fetcher = fetcher!=null? new BpmnInstanceFetcher(this, fetcher) :null;
		this.adapter = adapter;
		this.container = container;
		
		variables.put("$cms", cms);
		variables.put("$clock", cs);
		variables.put("$msgservice", ms);
		
		initContextVariables();
		
		// Create initial thread(s). 
		List startevents = model.getStartActivities(null, null);
		for(int i=0; startevents!=null && i<startevents.size(); i++)
		{
			ProcessThread	thread	= new ProcessThread(""+idcnt++, (MActivity)startevents.get(i), context, BpmnInterpreter.this);
			context.addThread(thread);
		}
	}	
		
	/**
	 *  Create a new bpmn process.
	 *  @param adapter The adapter.
	 */
	// Constructor for self-contained bpmn components
	public BpmnInterpreter(IComponentDescription desc, IComponentAdapterFactory factory, MBpmnModel model, final Map arguments, 
		String config, final IExternalAccess parent, Map activityhandlers, Map stephandlers, 
		IValueFetcher fetcher, RequiredServiceBinding[] bindings, boolean copy, boolean realtime,
		IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> inited)
	{
		super(desc, model.getModelInfo(), config, factory, parent, bindings, copy, realtime, resultlistener, inited);
		this.inited = inited;
		this.variables	= new HashMap();
		construct(model, activityhandlers, stephandlers);
		
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				executeInitStep2();
				// Initialize context variables.
				variables.put("$interpreter", this);
				init(getModel(), getConfiguration(), arguments)
					.addResultListener(createResultListener(new DelegationResultListener(inited)
				{
					public void customResultAvailable(Object result)
					{
						// Notify cms that init is finished.
						initContextVariables();

						inited.setResult(null);
					}
				}));
				return IFuture.DONE;
			}
		}).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result){}
			
			public void exceptionOccurred(Exception exception)
			{
				if(inited.isDone())
				{
					// Exception after init finished!?
					exception.printStackTrace();
				}
				else
				{
					inited.setException(exception);
					if(exception instanceof RuntimeException)
					{
						throw (RuntimeException)exception;
					}
					else
					{
						throw new RuntimeException(exception);
					}
				}
			}
		});
	}
	
	/**
	 *  Init method holds constructor code for both implementations.
	 */
	protected void construct(final MBpmnModel model, Map activityhandlers, Map stephandlers)
	{
		this.model = model;
		
		// Extract pool/lane from config.
		String config = getConfiguration();
		if(config==null || ALL.equals(config))
		{
			this.pool	= null;
			this.lane	= null;
		}
		else
		{
			String poollane = model.getPoolLane(config);
			if(poollane!=null && poollane.length()>0)
			{
				int idx	= config.indexOf('.');
				if(idx==-1)
				{
					this.pool	= config;
					this.lane	= null;
				}
				else
				{
					this.pool	= config.substring(0, idx);
					this.lane	= config.substring(idx+1);
				}
			}
		}
		
		this.activityhandlers = activityhandlers!=null? activityhandlers: DEFAULT_ACTIVITY_HANDLERS;
		this.stephandlers = stephandlers!=null? stephandlers: DEFAULT_STEP_HANDLERS;
		this.context = new ThreadContext(model);
		this.messages = new ArrayList();
		this.streams = new ArrayList();
		this.variables	= getArguments()!=null ? new HashMap(getArguments()) : new HashMap();
	}
	
	//-------- IComponentInstance interface --------
	
	/**
	 *  Can be called on the component thread only.
	 * 
	 *  Main method to perform component execution.
	 *  Whenever this method is called, the component performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for components
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeStep() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeStep()
	{
		boolean ret = false;
		
		try
		{
			if(!isFinished(pool, lane) && isReady(pool, lane))
			{
				notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION,
						IComponentChangeEvent.SOURCE_CATEGORY_EXECUTION, null, null, getComponentIdentifier(), getComponentDescription().getCreationTime(), null));
				executeStep(pool, lane);
				notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL,
						IComponentChangeEvent.SOURCE_CATEGORY_EXECUTION, null, null, getComponentIdentifier(), getComponentDescription().getCreationTime(), null));
			}
			
//			System.out.println("After step: "+this.getComponentAdapter().getComponentIdentifier().getName()+" "+isFinished(pool, lane));
			
			if(!finishing && isFinished(pool, lane) && !model.isKeepAlive() && started)
			{
//				System.out.println("terminating: "+getComponentIdentifier());
				finishing = true;
//				((IComponentManagementService)variables.get("$cms")).destroyComponent(adapter.getComponentIdentifier());
				
				SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class)
					.addResultListener(createResultListener(new DefaultResultListener<IComponentManagementService>()
				{
					public void resultAvailable(IComponentManagementService cms)
					{
						cms.destroyComponent(adapter.getComponentIdentifier());
					}
					public void exceptionOccurred(Exception exception)
					{
						if(!(exception instanceof ComponentTerminatedException))
						{
							super.exceptionOccurred(exception);
						}
					}
				}));
			}
			
//			System.out.println("Process wants: "+this.getComponentAdapter().getComponentIdentifier().getLocalName()+" "+!isFinished(null, null)+" "+isReady(null, null));
			
			ret = !isFinished(pool, lane) && isReady(pool, lane);
		}
		catch(ComponentTerminatedException ate)
		{
			// Todo: fix kernel bug.
			ate.printStackTrace();
		}
		
		return ret;
	}

	/**
	 *  Execute the init step 1.
	 * /
	protected void executeInitStep1()
	{
		// Fetch and cache services, then init service container.
		// Note: It is very tricky to call createResultListener() in the constructor as this
		// indirectly calls adapter.wakeup() but the component shouldn't run automatically!
		
		// todo: remove this hack of caching services
		final boolean services[] = new boolean[3];
		SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				variables.put("$cms", result);
				boolean init2;
				synchronized(services)
				{
					services[0]	= true;
					init2 = services[0] && services[1] && services[2];
				}
				if(init2)
				{
					executeInitStep2();
				}
			}
			public void exceptionOccurred(Exception exception)
			{
				inited.setException(exception);
			}
		}));
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				variables.put("$clock", result);
				boolean init2;
				synchronized(services)
				{
					services[1]	= true;
					init2 = services[0] && services[1] && services[2];
				}
				if(init2)
				{
					executeInitStep2();
				}
			}
			public void exceptionOccurred(Exception exception)
			{
				inited.setException(exception);
			}
		}));
		SServiceProvider.getService(getServiceContainer(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				variables.put("$msgservice", result);
				boolean init2;
				synchronized(services)
				{
					services[2]	= true;
					init2 = services[0] && services[1] && services[2];
				}
				if(init2)
				{
					executeInitStep2();
				}
			} 
			public void exceptionOccurred(Exception exception)
			{
				inited.setException(exception);
			}
		}));
	}*/
	
//	/**
//	 *  Execute the init step 2.
//	 */
//	protected void executeInitStep2()
//	{
//		// Initialize context variables.
//		variables.put("$interpreter", this);
//		
//		init(getModel(), getConfiguration(), arguments)
//			.addResultListener(createResultListener(new DelegationResultListener(inited)
//		{
//			public void customResultAvailable(Object result)
//			{
//				// Notify cms that init is finished.
//				initContextVariables();
//
//				inited.setResult(new Object[]{BpmnInterpreter.this, adapter});
//			}
//		}));
//	}

	/**
	 *  Start the component behavior.
	 */
	public void startBehavior()
	{
		assert !adapter.isExternalThread();
		
		// Start initial steps (if any).
		super.startBehavior();
		
		// Check if triggered by external event
		// eventtype, mactname, event
        Tuple3<String, String, Object> trigger = (Tuple3<String, String, Object>)getArguments().get(MBpmnModel.TRIGGER);
        // Create initial thread(s).
        List startevents = model.getStartActivities(null, null);
        for(int i=0; startevents!=null && i<startevents.size(); i++)
        {
            MActivity mact = (MActivity)startevents.get(i);
            if(trigger!=null && MBpmnModel.EVENT_START_TIMER.equals(trigger.getFirstEntity()))
            {
            	// Must perform time pattern check via reflection to avoid dependency
                Boolean b = Boolean.TRUE;
                try
                {
                    String val = (String)mact.getParsedPropertyValue("duration");
                    Class<?> cl = SReflect.findClass("jadex.platform.service.cron.TimePatternFilter", null, getClassLoader());
                    Object o = cl.newInstance();
                    Method m = cl.getMethod("setPattern", new Class[]{String.class});
                    m.invoke(o, new Object[]{val});
                    m = cl.getMethod("filter", new Class[]{Object.class});
                    b = (Boolean)m.invoke(o, new Object[]{trigger.getThirdEntity()});
                }
                catch(Exception e)
                {
                }
                if(b.booleanValue())
                {
                    ProcessThread    thread    = new ProcessThread(""+idcnt++, (MActivity)startevents.get(i), context, BpmnInterpreter.this);
                    thread.setParameterValue("$event", trigger.getThirdEntity());
                    context.addThread(thread);
                }
            }
            else if(trigger!=null && MBpmnModel.EVENT_START_RULE.equals(trigger.getFirstEntity()))
            {
            	// Was rule for that start event?
            	if(trigger.getSecondEntity().endsWith(mact.getName()))
            	{
            		ProcessThread thread = new ProcessThread(""+idcnt++, (MActivity)startevents.get(i), context, BpmnInterpreter.this);
            		thread.setParameterValue("$event", trigger.getThirdEntity());
            		context.addThread(thread);
            	}
            }	
            else //if(MBpmnModel.EVENT_START_EMPTY.equals(mact.getActivityType())
                //|| MBpmnModel.TASK.equals(mact.getActivityType())
                //|| MBpmnModel.SUBPROCESS.equals(mact.getActivityType()))
            {
                ProcessThread thread = new ProcessThread(""+idcnt++, (MActivity)startevents.get(i), context, BpmnInterpreter.this);
                context.addThread(thread);
            }
//            else
//            {
//                System.out.println("Not starting act: "+mact);
//            }
        } 
        
        started = true;
		
//		// Use step in case agent is started as suspended.
//		// Todo: Better to create steps directly as they immediately appear in debugger!? 
////		scheduleStep(new IComponentStep<Void>()
////		{
////			public IFuture<Void> execute(IInternalAccess ia)
////			{
//				// Create initial thread(s). 
//				List startevents	= model.getStartActivities(pool, lane);
//				for(int i=0; startevents!=null && i<startevents.size(); i++)
//				{
//					ProcessThread thread = new ProcessThread(""+idcnt++, (MActivity)startevents.get(i), context, BpmnInterpreter.this);
//					context.addThread(thread);
//				}
//				started = true;
////				return IFuture.DONE;
////			}
////		});
	}
	
	/**
	 *  Init context variables.
	 */
	protected void initContextVariables()
	{
		Set	vars	= model.getContextVariables();
		for(Iterator it=vars.iterator(); it.hasNext(); )
		{
			String	name	= (String)it.next();
			if(!variables.containsKey(name))	// Don't overwrite arguments.
			{
				Object	value	= null;
				IParsedExpression	exp	= model.getContextVariableExpression(name, getConfiguration());
				if(exp!=null)
				{
					try
					{
						value = exp.getValue(getFetcher());
					}
					catch(RuntimeException e)
					{
						throw new RuntimeException("Error parsing context variable: "+this+", "+name+", "+exp, e);
					}
				}
				variables.put(name, value);
			}
		}
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(final IMessageAdapter message)
	{
		getComponentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{
				// Iterate through process threads and dispatch message to first
				// waiting and fitting one (filter check).
				boolean processed = false;
				for(Iterator it=context.getAllThreads().iterator(); it.hasNext() && !processed; )
				{
					ProcessThread pt = (ProcessThread)it.next();
					if(pt.isWaiting())
					{
						IFilter filter = pt.getWaitFilter();
						if(filter!=null && filter.filter(message))
						{
							BpmnInterpreter.this.notify(pt.getActivity(), pt, message);
//							((DefaultActivityHandler)getActivityHandler(pt.getActivity())).notify(pt.getActivity(), BpmnInterpreter.this, pt, message);
							processed = true;
						}
					}
				}
				
				if(!processed)
				{
					messages.add(message);
//					System.out.println("Dispatched to waitqueue: "+message);
				}
			}
		});
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void streamArrived(final IConnection con)
	{
		getComponentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{
				// Iterate through process threads and dispatch message to first
				// waiting and fitting one (filter check).
				boolean processed = false;
				for(Iterator it=context.getAllThreads().iterator(); it.hasNext() && !processed; )
				{
					ProcessThread pt = (ProcessThread)it.next();
					if(pt.isWaiting())
					{
						IFilter filter = pt.getWaitFilter();
						if(filter!=null && filter.filter(con))
						{
							BpmnInterpreter.this.notify(pt.getActivity(), pt, con);
//							((DefaultActivityHandler)getActivityHandler(pt.getActivity())).notify(pt.getActivity(), BpmnInterpreter.this, pt, message);
							processed = true;
						}
					}
				}
				
				if(!processed)
				{
					streams.add(con);
//					System.out.println("Dispatched to waitqueue: "+message);
				}
			}
		});
	}

//	/**
//	 *  Can be called concurrently (also during executeAction()).
//	 *   
//	 *  Request agent to kill itself.
//	 *  The agent might perform arbitrary cleanup activities during which executeAction()
//	 *  will still be called as usual.
//	 *  Can be called concurrently (also during executeAction()).
//	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
//	 */
//	public IFuture<Void> cleanupComponent()
//	{
//		final Future<Void> ret = new Future<Void>();
//		// Todo: cleanup required???
//		
//		IFuture<IClockService> fut = SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//		fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
//		{
//			public void customResultAvailable(final IClockService clock)
//			{
//				ComponentChangeEvent.dispatchTerminatingEvent(getComponentAdapter(), getCreationTime(), getModel(), getServiceProvider(), getInternalComponentListeners())
//					.addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
//						startEndSteps().addResultListener(createResultListener(new DelegationResultListener(ret)
//						{
//							public void customResultAvailable(Object result)
//							{
//								terminateExtensions().addResultListener(createResultListener(new DelegationResultListener(ret)
//								{
//									public void customResultAvailable(Object result)
//									{
//										getComponentAdapter().invokeLater(new Runnable()
//										{
//											public void run()
//											{	
//												// Call cancel on all running threads.
//												for(Iterator it= getThreadContext().getAllThreads().iterator(); it.hasNext(); )
//												{
//													ProcessThread pt = (ProcessThread)it.next();
//													getActivityHandler(pt.getActivity()).cancel(pt.getActivity(), BpmnInterpreter.this, pt);
//						//							System.out.println("Cancelling: "+pt.getActivity()+" "+pt.getId());
//												}
//												
//												getServiceContainer().shutdown().addResultListener(createResultListener(new IResultListener<Void>()
//												{
//													public void resultAvailable(Void result)
//													{
//														ComponentChangeEvent.dispatchTerminatedEvent(getComponentIdentifier(), getCreationTime(), getModel(), getInternalComponentListeners(), clock)
//															.addResultListener(new DelegationResultListener<Void>(ret));
//													}
//													public void exceptionOccurred(final Exception exception)
//													{
//														ComponentChangeEvent.dispatchTerminatedEvent(getComponentIdentifier(), getCreationTime(), getModel(), getInternalComponentListeners(), clock)
//															.addResultListener(new DelegationResultListener<Void>(ret)
//														{
//															public void customResultAvailable(Void result)
//															{
//																ret.setException(exception);
//															}
//															public void exceptionOccurred(Exception exception)
//															{
//																ret.setException(exception);
//															}
//														});
//													}
//												}));
//												
////												ComponentChangeEvent.dispatchTerminatedEvent(getComponentIdentifier(), getCreationTime(), getModel(), clisteners, clock)
////													.addResultListener(new DelegationResultListener<Void>(ret));
//											}
//										});
//									}
//								}));
//							}
//						}));
//					}	
//				}));
//			}
//		}));
//		
//		return ret;
//	}
	
	/**
	 *  Start the end steps of the component.
	 *  Called as part of cleanup behavior.
	 */
	public IFuture<Void>	startEndSteps()
	{
		final Future<Void> ret = new Future<Void>(); 
		
		getComponentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{	
				// Remove all threads (calls cancel on running activities)
				if(getThreadContext().getThreads()!=null)
				{
					ProcessThread[]	threads	= getThreadContext().getThreads().toArray(new ProcessThread[0]);
					for(ProcessThread pt: threads)
					{
						pt.getThreadContext().removeThread(pt);
					}
				}
				
				BpmnInterpreter.super.startEndSteps().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
			}
		});
		
		return ret;
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
		for(Iterator it=context.getAllThreads().iterator(); !isatbreakpoint && it.hasNext(); )
		{
			ProcessThread	pt	= (ProcessThread)it.next();
			isatbreakpoint	= bps.contains(pt.getActivity().getBreakpointId());
		}
		return isatbreakpoint;
	}
		
	//-------- helpers --------
	
	/**
	 *  Add an action from external thread.
	 *  The contract of this method is as follows:
	 *  The agent ensures the execution of the external action, otherwise
	 *  the method will throw a agent terminated sexception.
	 *  @param action The action.
	 * /
	public void invokeLater(Runnable action)
	{
		synchronized(ext_entries)
		{
			if(ext_forbidden)
				throw new ComponentTerminatedException("External actions cannot be accepted " +
					"due to terminated agent state: "+this);
			{
				ext_entries.add(action);
			}
		}
		adapter.wakeup();
	}*/
	
	/**
	 *  Invoke some code with agent behaviour synchronized on the agent.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed on the agent thread.
	 *  If the agent does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 *  Note: 1.4 compliant code.
	 *  Problem: Deadlocks cannot be detected and no exception is thrown.
	 * /
	public void invokeSynchronized(final Runnable code)
	{
		if(isExternalThread())
		{
//			System.err.println("Unsynchronized internal thread.");
//			Thread.dumpStack();

			final boolean[] notified = new boolean[1];
			final RuntimeException[] exception = new RuntimeException[1];
			
			// Add external will throw exception if action execution cannot be done.
//			System.err.println("invokeSynchonized("+code+"): adding");
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						code.run();
					}
					catch(RuntimeException e)
					{
						exception[0]	= e;
					}
					
					synchronized(notified)
					{
						notified.notify();
						notified[0] = true;
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
				synchronized(notified)
				{
					if(!notified[0])
					{
						notified.wait();
					}
				}
//				System.err.println("invokeSynchonized("+code+"): returned");
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			if(exception[0]!=null)
				throw exception[0];
		}
		else
		{
			System.err.println("Method called from internal agent thread.");
			Thread.dumpStack();
			code.run();
		}
	}*/
	
//	/**
//	 *  Check if the external thread is accessing.
//	 *  @return True, if access is ok.
//	 */ 
//	public boolean isExternalThread()
//	{
//		return adapter.isExternalThread();
//	}
	
//	/**
//	 *  Create the service container.
//	 *  @return The service container.
//	 */
//	public IServiceContainer getServiceContainer()
//	{
//		if(container==null)
//		{
//			// todo: support container customization via bpmn file
////			container = new CacheServiceContainer(new ComponentServiceContainer(getComponentAdapter()), 25, 1*30*1000); // 30 secs cache expire
//			container = new ComponentServiceContainer(getComponentAdapter(), BpmnFactory.FILETYPE_BPMNPROCESS,
//				getModel().getRequiredServices(), bindings);
//		}
//		return container;
//	}
	
	/**
	 *  Get the model of the BPMN process instance.
	 *  @return The model.
	 */
	public MBpmnModel getModelElement()
	{
		return (MBpmnModel)context.getModelElement();
	}
	
	/**
	 *  Get the thread context.
	 *  @return The thread context.
	 */
	public ThreadContext getThreadContext()
	{
		return context;
	}
	
	/**
	 *  Check, if the process has terminated.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the process instance is finished with regards to the specified pool/lane. When both pool and lane are null, true is returned only when all pools/lanes are finished.
	 */
	public boolean isFinished(String pool, String lane)
	{
		return context.isFinished(pool, lane);
	}

	/**
	 *  Execute one step of the process.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 */
	public void executeStep(String pool, String lane)
	{
		if(isFinished(pool, lane))
			throw new UnsupportedOperationException("Cannot execute a finished process: "+this);
		
		if(!isReady(pool, lane))
			throw new UnsupportedOperationException("Cannot execute a process with only waiting threads: "+this);
		
		ProcessThread	thread	= context.getExecutableThread(pool, lane);
		
		// Thread may be null when external entry has not changed waiting state of any active plan. 
		if(thread!=null)
		{
//			if("End".equals(thread.getActivity().getName()))
//				System.out.println("end: "+thread);
			// Update parameters based on edge inscriptions and initial values.
			thread.updateParametersBeforeStep(this);
			
			// Find handler and execute activity.
			IActivityHandler handler = (IActivityHandler)activityhandlers.get(thread.getActivity().getActivityType());
			if(handler==null)
				throw new UnsupportedOperationException("No handler for activity: "+thread);

//			System.out.println("step: "+getComponentIdentifier()+" "+thread.getId()+" "+thread.getActivity());
			MActivity act = thread.getActivity();
			notifyListeners(createActivityEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, thread, thread.getActivity()));
//			thread = handler.execute(act, this, thread);
			handler.execute(act, this, thread);

			
			// Moved to StepHandler
//			thread.updateParametersAfterStep(act, this);
			
			// Check if thread now waits for a message and there is at least one in the message queue.
			// Todo: check if thread directly or indirectly (multiple events!) waits for a message event before checking waitqueue
			if(thread.isWaiting() && messages.size()>0 /*&& MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(thread.getActivity().getActivityType()) 
				&& (thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE)==null 
					|| EventIntermediateMessageActivityHandler.MODE_RECEIVE.equals(thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE)))*/)
			{
				boolean processed = false;
				for(int i=0; i<messages.size() && !processed; i++)
				{
					Object message = messages.get(i);
					IFilter filter = thread.getWaitFilter();
					if(filter!=null && filter.filter(message))
					{
						notify(thread.getActivity(), thread, message);
						processed = true;
						messages.remove(i);
//						System.out.println("Dispatched from waitqueue: "+messages.size()+" "+message);
					}
				}
			}
			if(thread.isWaiting() && streams.size()>0) 
			{
				boolean processed = false;
				for(int i=0; i<streams.size() && !processed; i++)
				{
					Object stream = streams.get(i);
					IFilter filter = thread.getWaitFilter();
					if(filter!=null && filter.filter(stream))
					{
						notify(thread.getActivity(), thread, stream);
						processed = true;
						streams.remove(i);
//						System.out.println("Dispatched from stream: "+messages.size()+" "+message);
					}
				}
			}
			
			if(thread.getThreadContext()!=null)
				notifyListeners(createThreadEvent(IComponentChangeEvent.EVENT_TYPE_MODIFICATION, thread));
		}
	}

	/**
	 *  Check if the process is ready, i.e. if at least one process thread can currently execute a step.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 */
	public boolean	isReady(String pool, String lane)
	{
		boolean	ready;
//		// Todo: consider only external entries belonging to pool/lane
//		synchronized(ext_entries)
//		{
//			ready	= !ext_entries.isEmpty();
//		}
		ready	= context.getExecutableThread(pool, lane)!=null;
		return ready;
	}
	
	/**
	 *  Method that should be called, when an activity is finished and the following activity should be scheduled.
	 *  Can safely be called from external threads.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param event	The event that has occurred, if any.
	 */
	public void	notify(final MActivity activity, final ProcessThread thread, final Object event)
	{
		if(adapter.isExternalThread())
		{
			try
			{
				getComponentAdapter().invokeLater(new Runnable()
				{
					public void run()
					{
						if(isCurrentActivity(activity, thread))
						{
//							System.out.println("Notify1: "+getComponentIdentifier()+", "+activity+" "+thread+" "+event);
							step(activity, BpmnInterpreter.this, thread, event);
							thread.setNonWaiting();
							if(thread.getThreadContext()!=null)
								notifyListeners(createThreadEvent(IComponentChangeEvent.EVENT_TYPE_MODIFICATION, thread));
						}
						else
						{
							System.out.println("Nop, due to outdated notify: "+thread+" "+activity);
						}
					}
				});
			}
			catch(ComponentTerminatedException cte)
			{
				// Ignore outdated events
			}
		}
		else
		{
			if(isCurrentActivity(activity, thread))
			{
//				System.out.println("Notify2: "+getComponentIdentifier()+", "+activity+" "+thread+" "+event);
				step(activity, BpmnInterpreter.this, thread, event);
				thread.setNonWaiting();
				if(thread.getThreadContext()!=null)
					notifyListeners(createThreadEvent(IComponentChangeEvent.EVENT_TYPE_MODIFICATION, thread));
			}
			else
			{
				System.out.println("Nop, due to outdated notify: "+thread+" "+activity);
			}
		}
	}
	
	/**
	 *  Test if the notification is relevant for the current thread.
	 *  The normal test is if thread.getActivity().equals(activity).
	 *  This method must handle the additional cases that the current
	 *  activity of the thread is a multiple event activity or
	 *  when the activity is a subprocess with an attached timer event.
	 *  In this case the notification could be for one of the child/attached events. 
	 */
	protected boolean isCurrentActivity(final MActivity activity, final ProcessThread thread)
	{
		boolean ret = thread.getActivity().equals(activity);
		if(!ret && MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE.equals(thread.getActivity().getActivityType()))
		{
			List outedges = thread.getActivity().getOutgoingSequenceEdges();
			for(int i=0; i<outedges.size() && !ret; i++)
			{
				MSequenceEdge edge = (MSequenceEdge)outedges.get(i);
				ret = edge.getTarget().equals(activity);
			}
		}
		if(!ret && MBpmnModel.SUBPROCESS.equals(thread.getActivity().getActivityType()))
		{
			List handlers = thread.getActivity().getEventHandlers();
			for(int i=0; !ret && handlers!=null && i<handlers.size(); i++)
			{
				MActivity	handler	= (MActivity)handlers.get(i);
				ret	= activity.equals(handler) && handler.getActivityType().equals("EventIntermediateTimer");
			}
		}
		return ret;
		
	}
	
	/**
	 *  Get the activity handler for an activity.
	 *  @param actvity The activity.
	 *  @return The activity handler.
	 */
	public IActivityHandler getActivityHandler(MActivity activity)
	{
		return (IActivityHandler)activityhandlers.get(activity.getActivityType());
	}
	
//	/**
//	 *  Get the step handler.
//	 *  @return The step handler.
//	 */
//	public IStepHandler getStepHandler(MActivity activity)
//	{
//		IStepHandler ret = (IStepHandler)stephandlers.get(activity.getActivityType());
//		return ret!=null? ret: (IStepHandler)stephandlers.get(IStepHandler.STEP_HANDLER);
//	}
	
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void step(MActivity activity, BpmnInterpreter instance, ProcessThread thread, Object event)
	{
//		System.out.println("step: "+activity.getName());
		notifyListeners(createActivityEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, thread, activity));
		
		IStepHandler ret = (IStepHandler)stephandlers.get(activity.getActivityType());
		if(ret==null) 
			ret = (IStepHandler)stephandlers.get(IStepHandler.STEP_HANDLER);
		ret.step(activity, instance, thread, event);
	}

	/**
	 *  Test if the given context variable is declared.
	 *  @param name	The variable name.
	 *  @return True, if the variable is declared.
	 */
	public boolean hasContextVariable(String name)
	{
		return (variables!=null && variables.containsKey(name)) || getModel().getArgument(name)!=null  || getModel().getResult(name)!=null;
	}
	
	/**
	 *  Get the value of the given context variable.
	 *  @param name	The variable name.
	 *  @return The variable value.
	 */
	public Object getContextVariable(String name)
	{
		Object ret;
		if(variables!=null && variables.containsKey(name))
		{
			ret = variables.get(name);			
		}
		else if(getModel().getArgument(name)!=null)
		{
			ret = getArguments().get(name);
		}
		else if(getModel().getResult(name)!=null)
		{
			ret	= getResults().get(name);
		}
		else
		{
			throw new RuntimeException("Undeclared context variable: "+name+", "+this);
		}
		return ret;
	}
	
	/**
	 *  Set the value of the given context variable.
	 *  @param name	The variable name.
	 *  @param value	The variable value.
	 */
	public void setContextVariable(String name, Object value)
	{
		setContextVariable(name, null, value);
	}
	
	/**
	 *  Set the value of the given context variable.
	 *  @param name	The variable name.
	 *  @param value	The variable value.
	 */
	public void setContextVariable(String name, Object key, Object value)
	{
		boolean isvar = variables!=null && variables.containsKey(name);
		boolean isres = getModel().getResult(name)!=null;
		if(!isres && !isvar)
		{
			if(getModel().getArgument(name)!=null)
			{
				throw new RuntimeException("Cannot set argument: "+name+", "+this);
			}
			else
			{
				throw new RuntimeException("Undeclared context variable: "+name+", "+this);				
			}
		}
		
		if(key==null)
		{
			if(isres)
			{
				setResultValue(name, value);
			}
			else
			{
				variables.put(name, value);	
			}
		}
		else
		{
			Object coll;
			if(isres)
			{
				coll = getResults().get(name);
			}
			else
			{
				coll = variables.get(name);
			}
			if(coll instanceof List)
			{
				int index = key==null? -1: ((Number)key).intValue();
				if(index>=0)
					((List)coll).add(index, value);
				else
					((List)coll).add(value);
			}
			else if(coll!=null && coll.getClass().isArray())
			{
				int index = ((Number)key).intValue();
				Array.set(coll, index, value);
			}
			else if(coll instanceof Map)
			{
				((Map)coll).put(key, value);
			}
			if(isres)
			{
				// Trigger event notification
				setResultValue(name, coll);
			}
//				else
//				{
//					throw new RuntimeException("Unsupported collection type: "+coll);
//				}
		}
	}
	
//	/**
//	 *  Fires an activity execution event.
//	 *  @param threadid ID of the executing ProcessThread.
//	 *  @param activity The activity being executed.
//	 */
//	protected void fireStartActivity(String threadid, MActivity activity)
//	{
////		System.out.println("fire start: "+activity);
//		notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, ACTIVITY, activity.getName(), threadid, getComponentIdentifier(), null));
//	}
//	
//	/**
//	 *  Fires an activity execution event.
//	 *  @param threadid ID of the executing ProcessThread.
//	 *  @param activity The activity being executed.
//	 */
//	protected void fireEndActivity(String threadid, MActivity activity)
//	{
////		System.out.println("fire end: "+activity);
//		if(activitylisteners!=null)
//		{
//			for(Iterator it = activitylisteners.iterator(); it.hasNext(); )
//				((IActivityListener)it.next()).activityEnded(new ChangeEvent(threadid, null, activity));
//		}
//	}
	
	protected int cnt;
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public  <T> IFuture<T> scheduleStep(final IComponentStep<T> step)
	{
		final Future<T> ret = new Future<T>();
		// todo:
		//final Future ret = createStepFuture(step);
		// todo: use FutureFunctionality.connectDelegationFuture(future, res); 
		
		try
		{
			getComponentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					// To schedule a step an implicit activity is created.
					// In order to put the step parameter value it is necessary
					// to have an edge with a mapping. Otherwise the parameter
					// value with be deleted in process thread updateParametersBeforeStep().
					
					MActivity act = new MActivity();
					act.setName("External Step Activity: "+(cnt++));
					act.setClazz(ExecuteStepTask.class);
					act.addParameter(new MParameter(MParameter.DIRECTION_IN, Object[].class, "step", null));
					act.setActivityType(MBpmnModel.TASK);
					MSequenceEdge edge = new MSequenceEdge();
					edge.setTarget(act);
					edge.addParameterMapping("step", SJavaParser.parseExpression("step", null, null), null);
					act.addIncomingSequenceEdge(edge);
					MPool pl = pool!=null? model.getPool(pool): (MPool)model.getPools().get(0);
					act.setPool(pl);
					ProcessThread thread = new ProcessThread(""+idcnt++, act, context, BpmnInterpreter.this);
					thread.setLastEdge(edge);
					thread.setParameterValue("step", new Object[]{step, ret});
					context.addExternalThread(thread);
				}
			});
		}
		catch(ComponentTerminatedException cte)
		{
			ret.setException(cte);
		}
		return ret;
	}
	
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public IFuture waitFor(final long delay, final IComponentStep step)
//	{
//		// todo: remember and cleanup timers in case of component removal.
//		
//		final Future ret = new Future();
//		
//		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(createResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				IClockService cs = (IClockService)result;
//				cs.createTimer(delay, new ITimedObject()
//				{
//					public void timeEventOccurred(long currenttime)
//					{
//						scheduleStep(step).addResultListener(new DelegationResultListener(ret));
//					}
//				});
//			}
//		}));
//		
//		return ret;
//	}
	
//	/**
//	 *  Adds an activity listener. The listener will be called
//	 *  once a process thread executes a new activity.
//	 *  @param listener The activity listener.
//	 */
//	public void addActivityListener(IActivityListener listener)
//	{
//		if(activitylisteners==null)
//			activitylisteners = new ArrayList();
//		activitylisteners.add(listener);
//	}
//	
//	/**
//	 *  Removes an activity listener.
//	 *  @param listener The activity listener.
//	 */
//	public void removeActivityListener(IActivityListener listener)
//	{
//		if(activitylisteners!=null)
//			activitylisteners.remove(listener);
//	}
	
	/**
	 * 
	 */
	public ProcessThreadInfo createProcessThreadInfo(ProcessThread thread)
	{
		 ProcessThreadInfo info = new ProcessThreadInfo(thread.getId(), thread.getActivity().getBreakpointId(),
            thread.getActivity().getPool()!=null ? thread.getActivity().getPool().getName() : null,
            thread.getActivity().getLane()!=null ? thread.getActivity().getLane().getName() : null,
            thread.getException()!=null ? thread.getException().toString() : "",
            thread.isWaiting(), thread.getData()!=null ? thread.getData().toString() : "");
		 return info;
	}

	//-------- abstract interpreter methods --------
	
	/**
	 *  Get the value fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			fetcher = new BpmnInstanceFetcher(this, new InterpreterFetcher(this));
		}
		return fetcher;
	}
	
//	/**
//	 *  Add a default value for an argument (if not already present).
//	 *  Called once for each argument during init.
//	 *  @param name	The argument name.
//	 *  @param value	The argument value.
//	 */
//	public void	addDefaultArgument(String name, Object value)
//	{
//		// super to ensure that getArguments() return correct values.
//		super.addDefaultArgument(name, value); 
//		if(!variables.containsKey(name))
//		{
////			System.out.println("arg: "+name+" "+value);
//			variables.put(name, value);
//		}
//	}
	
//	/**
//	 *  Add a default value for a result (if not already present).
//	 *  Called once for each result during init.
//	 *  @param name	The result name.
//	 *  @param value	The result value.
//	 */
//	public void	addDefaultResult(String name, Object value)
//	{
////		super.addDefaultResult(name, value);
//		if(!variables.containsKey(name))
//		{
//			variables.put(name, value);
//		}
//	}
	
//	/**
//	 *  Get the results of the component (considering it as a functionality).
//	 *  @return The results map (name -> value). 
//	 */
//	public Map getResults()
//	{
//		IArgument[] results = getModel().getResults();
//		Map res = new HashMap();
//		
//		for(int i=0; i<results.length; i++)
//		{
//			String resname = results[i].getName();
//			if(variables.containsKey(resname))
//			{
//				res.put(resname, variables.get(resname));
//			}
//		}
//		return res;
//	}
	
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return this;
	}
	
	/**
	 *  Init a service.
	 *  Overriden to allow for service implementations as BPMN processes using signal events.
	 */
	protected IFuture<Void> initService(ProvidedServiceInfo info, IModelInfo model)
	{
		IFuture<Void>	ret;
		ProvidedServiceImplementation	impl	= info.getImplementation();
		
		// Service implementation inside BPMN: find start events for service methods.
		if(impl!=null && impl.getValue()==null && impl.getClazz()==null && info.getName()!=null)
		{
			// Build map of potentially matching events: method name -> {list of matching signal event activities}
			MultiCollection	events	= new MultiCollection();
			List<MActivity>	starts	= this.model.getStartActivities(pool, lane);
			for(int i=0; i<starts.size(); i++)
			{
				MActivity	act	= (MActivity)starts.get(i);
				if(MBpmnModel.EVENT_START_MULTIPLE.equals(act.getActivityType())
					&& info.getName().equals(act.getName()))
				{
					List<MSequenceEdge>	edges	= act.getOutgoingSequenceEdges();
					for(int k=0; k<edges.size(); k++)
					{
						MActivity	target	= ((MSequenceEdge)edges.get(k)).getTarget();
						if(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL.equals(target.getActivityType()))
						{
							events.put(target.getName(), target);
						}
					}
				}
				else if(MBpmnModel.EVENT_START_SIGNAL.equals(act.getActivityType())
					&& act.getName().startsWith(info.getName()+"."))
				{
					events.put(act.getName().substring(info.getName().length()+1), act);
				}
			}

			// Find matching events for each method.
			final Future<Void>	fut	= new Future<Void>();
			ret	= fut;
			Class<?>	type	= info.getType().getType(getClassLoader());
			Method[]	meths	= type.getMethods();
			Map	methods	= new HashMap(); // method -> event.
			for(int i=0; !fut.isDone() && i<meths.length; i++)
			{
				Collection	es	= events.getCollection(meths[i].getName());
				for(Iterator it=es.iterator(); it.hasNext(); )
				{
					MActivity	event	= (MActivity)it.next();
					if(event.getPropertyNames().length==meths[i].getParameterTypes().length)
					{
						if(methods.containsKey(meths[i]))
						{
							fut.setException(new RuntimeException("Ambiguous start events found for service method: "+meths[i]));
						}
						else
						{
							methods.put(meths[i], event);
						}
					}
				}
				
				if(!methods.containsKey(meths[i]))
				{
					fut.setException(new RuntimeException("No start event found for service method: "+meths[i]));
				}
			}

//			System.out.println("Found mapping: "+methods);
			// Todo: interceptors
			addService(info.getName(), info.getType().getType(getClassLoader()), info.getImplementation().getProxytype(), null,
				Proxy.newProxyInstance(getClassLoader(), new Class[]{info.getType().getType(getClassLoader())}, new ProcessServiceInvocationHandler(this, methods)), null)
				.addResultListener(new ExceptionDelegationResultListener<IInternalService, Void>(fut)
			{
				public void customResultAvailable(IInternalService result)
				{
					fut.setResult(null);
				}
			});
		}
		
		// External service implementation
		else
		{
			ret	= super.initService(info, model);
		}
		
		return ret;
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public Map createReply(IMessageAdapter msg)
	{
		return createReply(msg.getParameterMap(), msg.getMessageType());
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public Map createReply(final Map msg, final MessageType mt)
	{
		return mt.createReply(msg);
	}
	
	/**
	 *  Create a thread event (creation, modification, termination).
	 */
	public IComponentChangeEvent createThreadEvent(String type, ProcessThread thread)
	{
		return new ComponentChangeEvent(type, TYPE_THREAD, thread.getClass().getName(), 
			thread.getId(), getComponentIdentifier(), getComponentDescription().getCreationTime(),
			IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(type) ? null : createProcessThreadInfo(thread));
	}
	
	/**
	 *  Create an activity event (start, end).
	 */
	public IComponentChangeEvent createActivityEvent(String type, ProcessThread thread, MActivity activity)
	{
		return new ComponentChangeEvent(type, TYPE_ACTIVITY, activity.getName(), 
			thread.getId(), getComponentIdentifier(), getComponentDescription().getCreationTime(), createProcessThreadInfo(thread));
	}
	
	/**
	 *  Get the classloader.
	 *  @return the classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return model.getClassLoader();
	}
}
