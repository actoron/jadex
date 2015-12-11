package jadex.bpmn.features.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MContextVariable;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.MTask;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.IInternalProcessEngineService;
import jadex.bpmn.runtime.IStepHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.bpmn.runtime.handler.DefaultStepHandler;
import jadex.bpmn.runtime.handler.EventEndErrorActivityHandler;
import jadex.bpmn.runtime.handler.EventEndSignalActivityHandler;
import jadex.bpmn.runtime.handler.EventEndTerminateActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateErrorActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateMultipleActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateNotificationHandler;
import jadex.bpmn.runtime.handler.EventIntermediateRuleHandler;
import jadex.bpmn.runtime.handler.EventIntermediateServiceActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateTimerActivityHandler;
import jadex.bpmn.runtime.handler.EventMultipleStepHandler;
import jadex.bpmn.runtime.handler.EventStartRuleHandler;
import jadex.bpmn.runtime.handler.EventStartServiceActivityHandler;
import jadex.bpmn.runtime.handler.GatewayORActivityHandler;
import jadex.bpmn.runtime.handler.GatewayParallelActivityHandler;
import jadex.bpmn.runtime.handler.GatewayXORActivityHandler;
import jadex.bpmn.runtime.handler.SubProcessActivityHandler;
import jadex.bpmn.runtime.handler.TaskActivityHandler;
import jadex.bpmn.tools.ProcessThreadInfo;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.rules.eca.RuleSystem;

/**
 *  Base bpmn feature holding the important data structures.
 */
public class BpmnComponentFeature extends AbstractComponentFeature implements IBpmnComponentFeature, IInternalBpmnComponentFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IBpmnComponentFeature.class, BpmnComponentFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class, IProvidedServicesFeature.class, ISubcomponentsFeature.class}, null);
	
//	/** Constant for step event. */
//	public static final String TYPE_ACTIVITY = "activity";
//	
//	/** The change event prefix denoting a thread event. */
//	public static final String	TYPE_THREAD	= "thread";
	
	/** The activity execution handlers (activity type -> handler). */
	public static final Map<String, IActivityHandler> DEFAULT_ACTIVITY_HANDLERS;
	
	/** The step execution handlers (activity type -> handler). */
	public static final Map<String, IStepHandler> DEFAULT_STEP_HANDLERS;
	
	//-------- attributes --------
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The activity handlers. */
	protected Map<String, IActivityHandler> activityhandlers;
	
	/** The step handlers. */
	protected Map<String, IStepHandler> stephandlers;

	/** The top level process thread. */
	protected ProcessThread topthread;
	
	/** The messages waitqueue. */
	protected List<Object> messages;

	/** The streams waitqueue. */
	protected List<IConnection> streams;

//	/** The inited future. */
//	protected Future<Void> inited;
		
	/** The thread id counter. */
	protected int idcnt;
	
	//-------- static initializers --------
	
	static
	{
		Map<String, IStepHandler> stephandlers = new HashMap<String, IStepHandler>();
		
		stephandlers.put(IStepHandler.STEP_HANDLER, new DefaultStepHandler());
		stephandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE, new EventMultipleStepHandler());
		
		DEFAULT_STEP_HANDLERS = Collections.unmodifiableMap(stephandlers);
		
		Map<String, IActivityHandler> activityhandlers = new HashMap<String, IActivityHandler>();
		
		// Task/Subprocess handler.
//		activityhandlers.put(MBpmnModel.TASK, new TaskActivityHandler());
		activityhandlers.put(MTask.TASK, new TaskActivityHandler());
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
//		activityhandlers.put(MBpmnModel.EVENT_START_MESSAGE, new EventIntermediateMessageActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_MESSAGE, new EventStartServiceActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_MULTIPLE, new EventIntermediateMultipleActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_RULE, new EventStartRuleHandler());
		activityhandlers.put(MBpmnModel.EVENT_START_SIGNAL, new EventIntermediateNotificationHandler());
			
		// Intermediate events.
		// Options: empty, message, rule, timer, error, signal, multi, link, compensation, cancel
		// Missing: link, compensation, cancel
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_EMPTY, new DefaultActivityHandler());
//		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE, new EventIntermediateMessageActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE, new EventIntermediateServiceActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new EventIntermediateRuleHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_TIMER, new EventIntermediateTimerActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_ERROR, new EventIntermediateErrorActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE, new EventIntermediateMultipleActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL, new EventIntermediateNotificationHandler());
//		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new UserInteractionActivityHandler());
		
		// End events.
		// Options: empty, message, error, compensation, terminate, signal, multi, cancel, link
		// Missing: link, compensation, cancel, terminate, signal, multi
		activityhandlers.put(MBpmnModel.EVENT_END_TERMINATE, new EventEndTerminateActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_END_EMPTY, new DefaultActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_END_ERROR, new EventEndErrorActivityHandler());
//		activityhandlers.put(MBpmnModel.EVENT_END_MESSAGE, new EventIntermediateMessageActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_END_MESSAGE, new EventIntermediateServiceActivityHandler());
		activityhandlers.put(MBpmnModel.EVENT_END_SIGNAL, new EventEndSignalActivityHandler());

		DEFAULT_ACTIVITY_HANDLERS = Collections.unmodifiableMap(activityhandlers);
	}
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BpmnComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		construct(activityhandlers, stephandlers);
		initContextVariables();

//		this.bpmnmodel = (MBpmnModel)getComponent().getModel().getRawModel();
	}
	
	/**
	 *  Special init that is used to announce event start events to process engine (if any).
	 */
//	public IFuture<Void> init(IModelInfo model, String config, Map<String, Object> arguments)
//	{
	
	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
//		System.out.println("init: "+model+" "+arguments);
		
		final Future<Void> ret = new Future<Void>();
		
		final Map<MSubProcess, List<MActivity>> evtsubstarts = getModel().getEventSubProcessStartEventMapping();
		
		if(!evtsubstarts.isEmpty())
		{
			IFuture<IInternalProcessEngineService> fut = getComponent().getComponentFeature(IRequiredServicesFeature.class).searchService(IInternalProcessEngineService.class, RequiredServiceInfo.SCOPE_APPLICATION);
			fut.addResultListener(new IResultListener<IInternalProcessEngineService>()
			{
				public void resultAvailable(IInternalProcessEngineService ipes)
				{
					final CounterResultListener<String> crl = new CounterResultListener<String>(evtsubstarts.size(), new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result)
						{
//							System.out.println("init done");
							super.customResultAvailable(result);
						}
					});
					
					for(Map.Entry<MSubProcess, List<MActivity>> evtsubentry : evtsubstarts.entrySet())
					{
						for(MActivity mact: evtsubentry.getValue())
						{
							String[] eventtypes = (String[])mact.getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
							UnparsedExpression	upex	= mact.getPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_CONDITION);
							Map<String, Object>	params	= null; 
							if(upex!=null)
							{
								IParsedExpression	exp	= SJavaParser.parseExpression(upex, getComponent().getModel().getAllImports(), getComponent().getClassLoader());
								for(String param: exp.getParameters())
								{
									if(hasContextVariable(param))
									{
										Object	val	= getContextVariable(param);
										if(val!=null)	// omit null values (also excludes '$event')
										{
											if(params==null)
											{
												params	= new LinkedHashMap<String, Object>();
											}
											params.put(param, val);
										}
									}
								}
							}
							
							final Tuple2<MSubProcess, MActivity> fevtsubentry = new Tuple2<MSubProcess, MActivity>(evtsubentry.getKey(), mact);
							final IExternalAccess exta = getComponent().getExternalAccess();
							IFuture<String>	fut	= ipes.addEventMatcher(eventtypes, upex, getComponent().getModel().getAllImports(), params, false, new IResultCommand<IFuture<Void>, Object>()
							{
								public IFuture<Void> execute(final Object event)
								{
									return exta.scheduleStep(new IComponentStep<Void>()
									{
										public jadex.commons.future.IFuture<Void> execute(IInternalAccess ia) 
										{
//											BpmnInterpreter ip = (BpmnInterpreter)ia;
											IInternalBpmnComponentFeature feat = (IInternalBpmnComponentFeature)ia.getComponentFeature(IBpmnComponentFeature.class);
											ProcessThread thread = new ProcessThread(fevtsubentry.getFirstEntity(), feat.getTopLevelThread(), ia, true);
											feat.getTopLevelThread().addThread(thread);
											ProcessThread subthread = new ProcessThread(fevtsubentry.getSecondEntity(), thread, ia);
											thread.addThread(subthread);
											subthread.setOrCreateParameterValue("$event", event);
											return IFuture.DONE;
										}
									});
								}
							});
							fut.addResultListener(crl);
						}
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(exception instanceof ServiceNotFoundException)
					{
						getComponent().getLogger().warning("Process "+getComponent().getComponentIdentifier()+" contains event subprocesses but no process engine found. Subprocess start events will not trigger...");
						ret.setResult(null);
					}
					else if(exception instanceof ComponentTerminatedException)
					{							
						ret.setResult(null);
					}
					else
					{
						ret.setException(exception);
					}
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
	
		return ret;
	}
		
	/**
	 *  Init method holds constructor code for both implementations.
	 */
	protected void construct(Map<String, IActivityHandler> activityhandlers, Map<String, IStepHandler> stephandlers)
	{
//		this.bpmnmodel = model;
		
//		// Extract pool/lane from config.
//		String config = getConfiguration();
//		if(config==null || ALL.equals(config))
//		{
//			this.pool	= null;
//			this.lane	= null;
//		}
//		else
//		{
//			String poollane = model.getPoolLane(config);
//			if(poollane!=null && poollane.length()>0)
//			{
//				int idx	= config.indexOf('.');
//				if(idx==-1)
//				{
//					this.pool	= config;
//					this.lane	= null;
//				}
//				else
//				{
//					this.pool	= config.substring(0, idx);
//					this.lane	= config.substring(idx+1);
//				}
//			}
//		}
		
		this.activityhandlers = activityhandlers!=null? activityhandlers: DEFAULT_ACTIVITY_HANDLERS;
		this.stephandlers = stephandlers!=null? stephandlers: DEFAULT_STEP_HANDLERS;
		
		this.topthread = new ProcessThread(null, null, getComponent());
		this.messages = new ArrayList<Object>();
		this.streams = new ArrayList<IConnection>();
		
		if(getComponent().getComponentFeature(IArgumentsResultsFeature.class).getArguments()!=null)
		{
			for(Map.Entry<String, Object> entry: getComponent().getComponentFeature(IArgumentsResultsFeature.class).getArguments().entrySet())
			{
				topthread.setParameterValue(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/**
	 *  Init context variables.
	 */
	protected void initContextVariables()
	{
		List<MContextVariable>	vars	= getModel().getContextVariables();
		for(Iterator<MContextVariable> it=vars.iterator(); it.hasNext(); )
		{
			MContextVariable	cv	= it.next();
//			if(!variables.containsKey(cv.getName()))	// Don't overwrite arguments.
			if(!topthread.hasParameterValue(cv.getName()))	// Don't overwrite arguments.
			{
				Object	value	= null;
				UnparsedExpression exp	= cv.getValue(getComponent().getConfiguration());
				if(exp!=null)
				{
					try
					{
						IParsedExpression parsed = (IParsedExpression)exp.getParsed();
						value = parsed != null? parsed.getValue(getComponent().getFetcher()) : null;
					}
					catch(RuntimeException e)
					{
						e.printStackTrace();
						throw new RuntimeException("Error parsing context variable: "+this+", "+cv.getName()+", "+exp, e);
					}
				}
				topthread.setParameterValue(cv.getName(), value);
//				variables.put(cv.getName(), value);
			}
		}
	}
	
	/**
	 *  Test if the given context variable is declared.
	 *  @param name	The variable name.
	 *  @return True, if the variable is declared.
	 */
	public boolean hasContextVariable(String name)
	{
		return (topthread.hasParameterValue(name)) || getComponent().getModel().getArgument(name)!=null  || getComponent().getModel().getResult(name)!=null;
//		return (variables!=null && variables.containsKey(name)) || getModel().getArgument(name)!=null  || getModel().getResult(name)!=null;
	}
	
	/**
	 *  Get the value of the given context variable.
	 *  @param name	The variable name.
	 *  @return The variable value.
	 */
	public Object getContextVariable(String name)
	{
		Object ret;
		if(topthread.hasParameterValue(name))
		{
			ret = topthread.getParameterValue(name);			
		}
		else if(getComponent().getModel().getArgument(name)!=null)
		{
			ret = getComponent().getComponentFeature(IArgumentsResultsFeature.class).getArguments().get(name);
		}
		else if(getComponent().getModel().getResult(name)!=null)
		{
			ret	= getComponent().getComponentFeature(IArgumentsResultsFeature.class).getResults().get(name);
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
//		boolean isvar = variables!=null && variables.containsKey(name);
		boolean isvar = topthread.hasParameterValue(name);
		
		boolean isres = getComponent().getModel().getResult(name)!=null;
		if(!isres && !isvar)
		{
			if(getComponent().getModel().getArgument(name)!=null)
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
				getComponent().getComponentFeature(IArgumentsResultsFeature.class).getResults().put(name, value);
			}
			else
			{
//				variables.put(name, value);	
				topthread.setParameterValue(name, value);
			}
		}
		else
		{
			Object coll;
			if(isres)
			{
				coll = getComponent().getComponentFeature(IArgumentsResultsFeature.class).getResults().get(name);
			}
			else
			{
//				coll = variables.get(name);
				coll = topthread.getParameterValue(name);
			}
			if(coll instanceof List)
			{
				int index = ((Number)key).intValue();
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
			else if(coll instanceof Set)
			{
				((Set)coll).add(value);
			}
//			System.out.println("coll: "+coll);
			if(isres)
			{
				// Trigger event notification
				getComponent().getComponentFeature(IArgumentsResultsFeature.class).getResults().put(name, coll);
			}
//				else
//				{
//					throw new RuntimeException("Unsupported collection type: "+coll);
//				}
		}
	}
	
	/**
	 *  Create a thread event (creation, modification, termination).
	 */
	public IMonitoringEvent createThreadEvent(String type, ProcessThread thread)
	{
		MonitoringEvent event = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), type+"."+TYPE_THREAD, System.currentTimeMillis(), PublishEventLevel.FINE);
		event.setProperty("thread_id", thread.getId());
//		if(!type.startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
		event.setProperty("details", createProcessThreadInfo(thread));
		return event;
	}
	
	/**
	 *  Create an activity event (start, end).
	 */
	public IMonitoringEvent createActivityEvent(String type, ProcessThread thread, MActivity activity)
	{
		MonitoringEvent event = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), type+"."+TYPE_ACTIVITY, System.currentTimeMillis(), PublishEventLevel.FINE);
		event.setProperty("thread_id", thread.getId());
		event.setProperty("activity", activity.getName());
		event.setProperty("details", createProcessThreadInfo(thread));
		return event;
	}

	/**
	 *  Create a new process thread info for logging / debug tools.
	 */
	public ProcessThreadInfo createProcessThreadInfo(ProcessThread thread)
	{
		String poolname = thread.getActivity()!=null && thread.getActivity().getPool()!=null ? thread.getActivity().getPool().getName() : null;
		String parentid = thread.getParent()!=null? thread.getParent().getId(): null;
//		String actname = thread.getActivity()!=null? thread.getActivity().getBreakpointId(): null;
		String actname = thread.getActivity()!=null? thread.getActivity().getName(): null;
		String actid = thread.getActivity()!=null? thread.getActivity().getId(): null;
		String lanename =  thread.getActivity()!=null && thread.getActivity().getLane()!=null ? thread.getActivity().getLane().getName() : null;
		String ex = thread.getException()!=null ? thread.getException().toString() : "";
		String data = thread.getData()!=null ? thread.getData().toString() : "";
		String edges = thread.getDataEdges()!=null ? thread.getDataEdges().toString() : "";
		ProcessThreadInfo info = new ProcessThreadInfo(thread.getId(), parentid, actname,
			actid, poolname, lanename, ex, thread.isWaiting(), data, edges);
		return info;
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
	
	/**
	 *  Get the top level thread (is not executed and just acts as top level thread container).
	 */
	public ProcessThread getTopLevelThread()
	{
		return topthread;
	}
	
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void step(MActivity activity, IInternalAccess instance, ProcessThread thread, Object event)
	{
//		System.out.println("step: "+activity.getName());
//		notifyListeners(createActivityEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, thread, activity));
		if(getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null 
			&& getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
		{
			getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(createActivityEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL, thread, activity), PublishTarget.TOALL);
		}
		
		IStepHandler ret = (IStepHandler)stephandlers.get(activity.getActivityType());
		if(ret==null) 
			ret = (IStepHandler)stephandlers.get(IStepHandler.STEP_HANDLER);
		ret.step(activity, instance, thread, event);
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
		if(!getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			try
			{
				getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						if(isCurrentActivity(activity, thread))
						{
//							System.out.println("Notify1: "+getComponentIdentifier()+", "+activity+" "+thread+" "+event);
							
							//TODO: Hack!? Cancel here or somewhere else?
							if (!activity.equals(thread.getActivity()) && thread.getTask() != null && thread.isWaiting())
							{
								thread.getTask().cancel(component).get();
							}
							
							step(activity, getComponent(), thread, event);
							thread.setNonWaiting();
							if(getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null 
								&& getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
							{
								getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
							}
						}
						else
						{
							System.out.println("Nop, due to outdated notify: "+thread+" "+activity);
						}
						return IFuture.DONE;
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
//				System.out.println("Notify1: "+getComponentIdentifier()+", "+activity+" "+thread+" "+event);
				step(activity, getComponent(), thread, event);
				thread.setNonWaiting();
//				if(getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null
//					&& getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//				{
//					getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
//				}
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
		boolean ret = SUtil.equals(thread.getActivity(), activity);
		if(!ret && thread.getActivity()!=null && MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE.equals(thread.getActivity().getActivityType()))
		{
			List<MSequenceEdge> outedges = thread.getActivity().getOutgoingSequenceEdges();
			for(int i=0; i<outedges.size() && !ret; i++)
			{
				MSequenceEdge edge = outedges.get(i);
				ret = edge.getTarget().equals(activity);
			}
		}
		if(!ret && thread.getActivity()!=null)
		{
			List<MActivity> handlers = thread.getActivity().getEventHandlers();
			for(int i=0; !ret && handlers!=null && i<handlers.size(); i++)
			{
				MActivity handler = handlers.get(i);
				ret	= activity.equals(handler);// && handler.getActivityType().equals("EventIntermediateTimer");
			}
		}
		return ret;
		
	}

	/**
	 *  Check if the process is ready, i.e. if at least one process thread can currently execute a step.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 */
	public boolean	isReady()
	{
		return isReady(null, null);
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
		ready = topthread.getExecutableThread(pool, lane)!=null;
		return ready;
	}
	
	/**
	 *  Check, if the process has terminated.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the process instance is finished with regards to the specified pool/lane. When both pool and lane are null, true is returned only when all pools/lanes are finished.
	 */
	public boolean isFinished()
	{
		return topthread.isFinished(null, null);
	}
	
	/**
	 *  Check, if the process has terminated.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the process instance is finished with regards to the specified pool/lane. When both pool and lane are null, true is returned only when all pools/lanes are finished.
	 */
	public boolean isFinished(String pool, String lane)
	{
		return topthread.isFinished(pool, lane);
	}

	/**
	 *  Get the messages.
	 *  @return The messages
	 */
	public List<Object> getMessages()
	{
		return messages;
	}

	/**
	 *  Get the streams.
	 *  @return The streams
	 */
	public List<IConnection> getStreams()
	{
		return streams;
	}
	
	/**
	 * 
	 */
	protected MBpmnModel getModel()
	{
		return (MBpmnModel)getComponent().getModel().getRawModel();
	}
	
	
	/**
	 *  The feature can inject parameters for expression evaluation
	 *  by providing an optional value fetcher. The fetch order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IValueFetcher	getValueFetcher()
	{
		return new IValueFetcher()
		{
			public Object fetchValue(String name)
			{
				Object	ret;
				if(hasContextVariable(name))
				{
					ret	= getContextVariable(name);
				}
				else
				{
					throw new RuntimeException("Parameter not found: "+name);
				}
				return ret;
			}
		};
	}
}
