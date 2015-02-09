package jadex.bpmn.features.impl;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.IStepHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.tools.ProcessThreadInfo;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.rules.eca.RuleSystem;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 *
 */
public class BpmnComponentFeature extends AbstractComponentFeature implements IBpmnComponentFeature, IInternalBpmnComponentFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IBpmnComponentFeature.class, BpmnComponentFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class, IProvidedServicesFeature.class, ISubcomponentsFeature.class}, null);
	
	/** Constant for step event. */
	public static final String TYPE_ACTIVITY = "activity";
	
	/** The change event prefix denoting a thread event. */
	public static final String	TYPE_THREAD	= "thread";
	
	//-------- attributes --------
	
	/** The micro agent model. */
	protected MBpmnModel bpmnmodel;
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The activity handlers. */
	protected Map<String, IActivityHandler> activityhandlers;
	
	/** The step handlers. */
	protected Map<String, IStepHandler> stephandlers;

	/** The top level process thread. */
	protected ProcessThread topthread;
	
//	/** The finishing flag marker. */
//	protected boolean finishing;
	
	/** The messages waitqueue. */
	protected List<Object> messages;

	/** The streams waitqueue. */
	protected List<IConnection> streams;

//	/** The inited future. */
//	protected Future<Void> inited;
	
//	/** The started flag. */
//	protected boolean started;
	
	/** The thread id counter. */
	protected int idcnt;
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BpmnComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		this.bpmnmodel = (MBpmnModel)getComponent().getModel().getRawModel();
	}

	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		return IFuture.DONE;
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
			ret = getComponent().getComponentFeature(IArgumentsFeature.class).getArguments().get(name);
		}
		else if(getComponent().getModel().getResult(name)!=null)
		{
			ret	= getComponent().getComponentFeature(IArgumentsFeature.class).getResults().get(name);
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
				getComponent().getComponentFeature(IArgumentsFeature.class).getResults().put(name, value);
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
				coll = getComponent().getComponentFeature(IArgumentsFeature.class).getResults().get(name);
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
				getComponent().getComponentFeature(IArgumentsFeature.class).getResults().put(name, coll);
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
		if(getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
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
				getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						if(isCurrentActivity(activity, thread))
						{
//							System.out.println("Notify1: "+getComponentIdentifier()+", "+activity+" "+thread+" "+event);
							step(activity, getComponent(), thread, event);
							thread.setNonWaiting();
							if(getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
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
				if(getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
				{
					getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
				}
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
		if(!ret && thread.getActivity()!=null && MBpmnModel.SUBPROCESS.equals(thread.getActivity().getActivityType()))
		{
			List<MActivity> handlers = thread.getActivity().getEventHandlers();
			for(int i=0; !ret && handlers!=null && i<handlers.size(); i++)
			{
				MActivity handler = handlers.get(i);
				ret	= activity.equals(handler) && handler.getActivityType().equals("EventIntermediateTimer");
			}
		}
		return ret;
		
	}
}
