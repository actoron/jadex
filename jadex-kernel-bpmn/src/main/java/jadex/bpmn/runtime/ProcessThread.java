package jadex.bpmn.runtime;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.MTask;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.handler.ICancelable;
import jadex.bpmn.runtime.handler.SplitInfo;
import jadex.bpmn.runtime.handler.SubProcessActivityHandler.SubprocessResultHandler;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.lang.reflect.Array;
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
import java.util.StringTokenizer;

/**
 *  Representation of a single control flow in a BPMN process instance,
 *  i.e. an instance of a sequence flow.
 */
public class ProcessThread	implements ITaskContext
{
	//-------- attributes --------
	
	/** The thread id. */
	protected String id;
	
	/** The next activity. */
	protected MActivity	activity;
	
	/** The last edge (if any). */
	protected MSequenceEdge	edge;
		
	/** The data of the current or last activity. */
	protected Map<String, Object> data;
	
	/** The data of the current data edges. */
	protected Map<String, Object> dataedges;
	
	/** The parent process thread. */
	protected ProcessThread parent;
	
	/** The subthreads. */
	protected List<ProcessThread> subthreads;
	
	/** The Bpmn instance. */
	protected IInternalAccess instance;
	
	/** The exception that has just occurred in the process (if any). */
	protected Exception	exception;
	
	/** Is the process in a waiting state. */
	protected boolean waiting;
	
	/** The wait info. */
	protected ICancelable cancelinfo;
	
	/** The wait filter. */
	protected IFilter<Object> waitfilter;
	
	/** The current task. */
	protected ITask task;
	
	/** Is the task canceled. */
	protected boolean canceled;
	
	/** The id counter for sub processes. */
	public int idcnt;
	
	/** The split infos. */
	public Map<String, SplitInfo> splitinfos;
	
	/** The loop command. */
	protected IResultCommand<Boolean, Void> loopcmd;
	
	/** The subprocess intermediate result received command. */
	protected SubprocessResultHandler resulthandler;
	
	//-------- constructors --------

	/**
	 *  Create a new process instance.
	 *  @param activity	The current activity.
	 */
	public ProcessThread(MActivity activity, ProcessThread parent, IInternalAccess instance)
	{
		this(null, activity, parent, instance);
	}
	
	/**
	 *  Create a new process instance.
	 *  @param activity	The current activity.
	 */
	public ProcessThread(String id, MActivity activity, ProcessThread parent, IInternalAccess instance)
	{
		this.id	= parent!=null? parent.getNextChildId(): null;
//		this.activity	= activity;
		this.parent = parent;
		this.instance = instance;
		
		setActivity(activity);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return	The bpmn model.
	 */
	public MBpmnModel getBpmnModel()
	{
		return (MBpmnModel)instance.getModel().getRawModel();
	}
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return this.id;
	}	
	
	/**
	 *  Get the activity.
	 *  @return The activity.
	 */
	public MActivity getActivity()
	{
		return this.activity;
	}

	/**
	 *  Set the next activity. Sets the last edge to null.
	 *  Should only be used, when no edge available (e.g. start events or event handlers of subprocesses).
	 */
	public void	setActivity(MActivity activity)
	{
		this.activity	= activity;
		
		// Clear edge and resulthandler on each transition
		this.edge	= null;
		this.resulthandler = null;
		
		if(activity!=null)
			scheduleExecution();
//		else
//			System.out.println("activity to null: "+getId());
	}
	
	/**
	 *  Is the current task canceled?
	 *  @return The canceled flag.
	 */
	public boolean isCanceled()
	{
		return canceled;
	}
	
	/**
	 *  Set the canceled state.
	 *  @param canceled True, if canceled.
	 */
	public void setCanceled(boolean canceled)
	{
		this.canceled = canceled;
	}
	
	/**
	 *  Gets the current task.
	 *  @return The current task.
	 */
	public ITask getTask()
	{
		return task;
	}
	
	/**
	 *  Sets the current task.
	 *  @param task The current task.
	 */
	public void setTask(ITask task)
	{
		this.task = task;
	}
	
	/**
	 *  Get the last edge (if any).
	 *  @return The edge.
	 */
	public MSequenceEdge getLastEdge()
	{
		return this.edge;
	}

	/**
	 *  Set the last edge. Also sets the next activity.
	 *  @param edge	The edge.
	 */
	public void setLastEdge(MSequenceEdge edge)
	{
		setActivity(edge!=null ? (MActivity)edge.getTarget() : null);
		this.edge	= edge;
	}
	
	/**
	 *  Is the process in a waiting state (i.e. blocked)? 
	 *  @return The waiting flag.
	 */
	public boolean	isWaiting()
	{
//		return this.waiting!=null;
		return waiting;
	}
	
	/**
	 *  Set the waiting state.
	 */
	public void setWaiting(boolean waiting)
	{
//		System.out.println("Set waiting thread: "+getId()+" "+waiting);
		this.waiting = waiting;
		if(getInstance().getComponentFeature0(IMonitoringComponentFeature.class)!=null && getInstance().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			getInstance().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(getBpmnFeature(getInstance()).createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, this), PublishTarget.TOALL);
	}
	
	/**
	 *  Set to non waiting.
	 */
	public void	setNonWaiting()
	{
//		boolean waswaiting = waiting;
		
		this.waiting = false;
//		this.waitinfo = null;
		this.cancelinfo = null;
		this.waitfilter = null;
		if(getInstance().getComponentFeature0(IMonitoringComponentFeature.class)!=null && getInstance().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			getInstance().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(getBpmnFeature(getInstance()).createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, this), PublishTarget.TOALL);

//		if(waswaiting)
//			scheduleExecution();
		
//		System.out.println("Thread: "+ComponentIdentifier.LOCAL.get()+", "+getId()+" "+waiting);
	}
	
	/**
	 *  Schedule notification of this thread.
	 */
	protected void scheduleExecution()
	{
		getInstance().getComponentFeature(IExecutionFeature.class).scheduleStep(new ExecuteProcessThread(this)).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// nop, can happen when step is invalid
				// todo: use StepInvalidException?!
			}
		});
	}
	
	/**
	 *  Get the waiting type. 
	 *  @return The waiting type.
	 * /
	public String getWaitingState()
	{
		return this.waiting;
	}*/

	/**
	 *  Set the process waiting state (i.e. blocked). 
	 *  @param waiting	The waiting flag.
	 * /
	public void setWaitingState(String waiting)
	{
		this.waiting = waiting;
//		System.out.println("Thread: "+this+" "+waiting);
	}*/
	
	// todo: refactor/simplify the waiting stuff.
	
	/**
	 *  Set the process waiting info. 
	 *  @param waiting	The waiting info.
	 */
	public void setWaitInfo(ICancelable cancelinfo)
	{
//		this.waitinfo = waitinfo;
		this.cancelinfo = cancelinfo;
//		System.out.println("Thread waitinfo: "+ComponentIdentifier.LOCAL.get()+", "+getId()+" "+waitinfo);
	}
	
	/**
	 *  Get the waitinfo.
	 *  @return The waitinfo.
	 */
	public ICancelable getWaitInfo()
	{
		return this.cancelinfo;
	}
	
	/**
	 *  Get the wait filter.
	 *  @return The waitfilter.
	 */
	public IFilter<Object> getWaitFilter()
	{
		return this.waitfilter;
	}

	/**
	 *  Set the wait filter.
	 *  @param waitfilter The waitfilter to set.
	 */
	public void setWaitFilter(IFilter<Object> waitfilter)
	{
		this.waitfilter = waitfilter;
	}
	
//	/**
//	 *  Get the thread context
//	 *  @return	The thread context.
//	 */
//	public ThreadContext getThreadContext()
//	{
//		return context;
//	}
//	
//	/**
//	 *  Set the context.
//	 *  @param context The context to set.
//	 */
//	public void setThreadContext(ThreadContext context)
//	{
//		this.context = context;
//	}

	/**
	 *  Create a copy of this thread (e.g. for a parallel split).
	 */
	public ProcessThread createCopy()
	{
		ProcessThread	ret	= new ProcessThread(activity, parent, instance);
		ret.edge	= edge;
		ret.data	= data!=null? new HashMap<String, Object>(data): null;
		ret.dataedges	= dataedges!=null? new HashMap<String, Object>(dataedges): null;
		ret.splitinfos	= splitinfos!=null ? new LinkedHashMap<String, SplitInfo>(splitinfos) : null;
		return ret;
	}
	
	/**
	 *  Create a copy of this thread (e.g. for a parallel split).
	 */
	public void copy(ProcessThread ret)
	{
		ret.edge	= edge;
		ret.data	= data!=null? new HashMap<String, Object>(data): null;
		ret.dataedges	= dataedges!=null? new HashMap<String, Object>(dataedges): null;
		ret.splitinfos	= splitinfos!=null ? new LinkedHashMap<String, SplitInfo>(splitinfos) : null;
	}
	
	/**
	 *  Test if a parameter has been set on activity.
	 *  @param name	The parameter name. 
	 *  @return	True if parameter is known.
	 */
	public boolean hasOwnParameterValue(String name)
	{
		return data!=null && data.containsKey(name);
	}
	
	//-------- ITaskContext --------
	
	/**
	 *  Test if a parameter has been set on activity.
	 *  @param name	The parameter name. 
	 *  @return	True if parameter is known.
	 */
	public boolean hasParameterValue(String name)
	{
		return hasOwnParameterValue(name) || getParent()!=null 
			&& getParent().hasParameterValue(name);
	}

	/**
	 *  Get the model element.
	 *  @return	The model of the task.
	 */
	public MActivity getModelElement()
	{
		return activity;
	}

	/**
	 *  Get the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @return	The parameter value. 
	 */
	public Object getParameterValue(String name)
	{
		return hasOwnParameterValue(name)? data.get(name): 
			getParent()!=null? getParent().getParameterValue(name): null;
	}
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public Map<String, Object> getParameters()
	{
		return data;
	}
	
	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	public void	setDataEdgeValue(String name, Object value)
	{
		if(dataedges == null)
		{
			dataedges = new HashMap<String, Object>();
		}
		dataedges.put(name, value);
	}

	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	public void	setParameterValue(String name, Object value)
	{
		setParameterValue(name, null, value);
	}
	
	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	public void	setParameterValue(String name, Object key, Object value)
	{
		internalSetParameterValue(name, key, value, this);
	}

	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	protected void	internalSetParameterValue(String name, Object key, Object value, ProcessThread start)
	{
		if(getActivity()!=null && getActivity().hasParameter(name))
		{
			// Local parameter
			setOrCreateParameterValue(name, key, value);
		}
		else if(getParent()!=null)
		{
			// Try local parameter in parent
			getParent().internalSetParameterValue(name, key, value, start);
		}
//		else if(getParent()==null && instance.getModelElement().getContextVariable(name)!=null)
		else if(getParent()==null && ((MBpmnModel)getInstance().getModel().getRawModel()).getContextVariable(name)!=null)
		{
			// Global parameter
			setOrCreateParameterValue(name, key, value);			
		}
		else
		{
			throw new RuntimeException("No such parameter: "+name+", "+start);
		}
	}

	/**
	 *  Set or create a parameter value directly in this thread.
	 */
	public void setOrCreateParameterValue(String name, Object value)
	{
		setOrCreateParameterValue(name, null, value);
	}
	
	/**
	 *  Set or create a parameter value directly in this thread.
	 */
	public void setOrCreateParameterValue(String name, Object key, Object value)
	{
		if(data==null)
			data = new HashMap<String, Object>();
		
		if(key==null)
		{
			data.put(name, value);	
		}
		else
		{
			Object coll = data.get(name);
			if(coll instanceof List)
			{
				int index = ((Number)key).intValue();
				if(index>=0)
					((List)coll).set(index, value);
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
//			else
//			{
//				throw new RuntimeException("Unsupported collection type: "+coll);
//			}
		}
		
		if(getActivity() instanceof MSubProcess)
		{
			if(getActivity().hasParameter(name) && getActivity().getParameter(name).isOut())
			{
//				System.out.println("setting subprocess out parameter: "+name+" "+value);
				// Hack?! should this be called directly?
				if(resulthandler==null)
					resulthandler = new SubprocessResultHandler(this, activity);
				resulthandler.handleProcessResult(name, key, value);
//				SubProcessActivityHandler.handleProcessResult(instance, this, activity, name, key, value);
			}
		}
	}
	
	/**
	 *  Remove the value of a parameter.
	 *  @param name	The parameter name. 
	 */
	public void	removeParameterValue(String name)
	{
		assert activity!=null;
		if(data!=null)
		{
			data.remove(name);
		}
	}
	
	/**
	 *  Get the name of all parameters.
	 *  @return The parameter names.
	 */
	public String[] getParameterNames()
	{
		return data!=null? (String[])data.keySet().toArray(new String[data.size()]): SUtil.EMPTY_STRING_ARRAY;
	}
	
	/**
	 *  Get the name of all parameters.
	 *  @return The parameter names.
	 */
	public Set<String> getAllParameterNames()
	{
		Set<String> ret = new HashSet<String>();
		ProcessThread pt = this;
		while(pt!=null)
		{
			String[] names = getParameterNames();
			for(String name: names)
			{
				ret.add(name);
			}
			pt = pt.getParent();
		}
		return ret;
	}
	
	/**
	 *  Get the value of a property.
	 *  @param name	The property name. 
	 *  @return	The property value. 
	 */
	public Object getPropertyValue(String name)
	{
		return getPropertyValue(name, activity);
	}
	
	/**
	 *  Hack: method is necessary because thread.activity is not always 
	 *  the activity to execute in case of multiple event.
	 *  Get the value of a property.
	 *  @param name	The property name. 
	 *  @return	The property value. 
	 */
	public Object getPropertyValue(String name, MActivity activity)
	{
		assert activity!=null;
		UnparsedExpression	upex	= activity.getPropertyValue(name);
		try
		{
			return upex!=null ? ((IParsedExpression)upex.getParsed()).getValue(new ProcessThreadValueFetcher(this, true, instance.getFetcher())) : null;
		}
		catch(RuntimeException e)
		{
			throw new RuntimeException("Error parsing property: "+instance+", "+this+", "+name+", "+upex, e);
		}
	}
	
	/**
	 *  Test, if a property is declared.
	 *  @param name	The property name.
	 *  @return True, if the property is declared.
	 */
	public boolean	hasPropertyValue(String name)
	{
		return activity.hasPropertyValue(name);
	}
	
//	/**
//	 *  Gets the hard constraints.
//	 *  @return The hard constraints.
//	 */
//	public RHardConstraints getHardConstraints()
//	{
//		return context.getHardConstraints();
//	}
	
//	/**
//	 *  Check if the value of a result is set.
//	 *  @param name	The result name. 
//	 *  @return	True, if the result is set to some value. 
//	 */
//	public boolean hasResultValue(String name)
//	{
//		return instance.hasResultValue(name);
//	}
//
//	/**
//	 *  Get the value of a result.
//	 *  @param name	The result name. 
//	 *  @return	The result value. 
//	 */
//	public Object getResultValue(String name)
//	{
//		return instance.getResultValue(name);
//	}
//	
//	/**
//	 *  Set the value of a result.
//	 *  @param name	The result name. 
//	 *  @param value The result value. 
//	 */
//	public void	setResultValue(String name, Object value)
//	{
//		instance.setResultValue(name, value);
//	}
	
	/**
	 *  Get the exception (if any).
	 *  @return	The exception (if any).
	 */
	public Exception	getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception	The exception.
	 */
	public void	setException(Exception exception)
	{
		this.exception	= exception;
	}
	
	/**
	 *  Get the instance.
	 *  @return The instance.
	 */
	public IInternalAccess getInstance()
	{
		return this.instance;
	}
	
	/**
	 *  Get the data edges.
	 *  @return The data edges.
	 */
	public Map<String, Object> getDataEdges()
	{
		return this.dataedges;
	}
	
	/**
	 *  Get the data.
	 *  @return The data.
	 */
	public Map<String, Object> getData()
	{
		return this.data;
	}

	/**
	 *  Test if the thread belongs to the given pool and/or lane.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the thread belongs to the given pool and/or lane. Also returns true when both pool and lane are null.
	 */
	public boolean belongsTo(String pool, String lane)
	{
		// Pool null for external steps.
		MPool po = getActivity().getPool();
		assert po!=null: getActivity();
		boolean	ret	= pool==null || pool.equals(po.getName());
		
		// Test lane
		if(ret && lane!=null)
		{
			List<MLane>	lanes	= new ArrayList<MLane>();
			MLane	mlane	= getActivity().getLane();
			while(mlane!=null)
			{
				lanes.add(mlane);
				mlane	= mlane.getLane();
			}
			
			StringTokenizer	stok	= new StringTokenizer(lane, ".");
			while(ret && stok.hasMoreTokens())
			{
				ret	= !lanes.isEmpty() && ((MLane)lanes.remove(lanes.size()-1)).getName().equals(stok.nextToken());
			}
		}
		
		return ret;
	}

	/**
	 *  Update parameters based on edge inscriptions and initial values.
	 *  @param instance	The calling BPMN instance.
	 */
	public  void updateParametersBeforeStep(IInternalAccess instance)
	{
//		System.out.println("before: "+getActivity());

		if(getActivity().getActivityType()!=null && getActivity().getActivityType().indexOf("Event")!=-1)
		{
			// Just pass on data edge params in case of event
			Map<String, Object>	passedparams = getDataEdgeValues();
			IndexMap<String, MParameter> params = getActivity().getParameters();
			if(params!=null && passedparams!=null)
			{
				for(Iterator<MParameter> it=params.values().iterator(); it.hasNext(); )
				{
					MParameter param = (MParameter)it.next();
					if(passedparams.containsKey(param.getName()))
					{
						setParameterValue(param.getName(), passedparams.get(param.getName()));
					}
				}
			}
		}
//		else if(MBpmnModel.TASK.equals(getActivity().getActivityType())
//			|| getActivity() instanceof MSubProcess)
		else if(getActivity() instanceof MTask
				|| getActivity() instanceof MSubProcess)
		{
			// Handle parameter passing in edge inscriptions.
			Map<String, Object>	passedparams = null;
			Set<String>	indexparams	= null;
			
			if(getLastEdge()!=null)
			{
				if(getLastEdge().getParameterMappings()!=null)
				{
					IndexMap<String, Tuple2<UnparsedExpression, UnparsedExpression>> mappings = getLastEdge().getParameterMappings();
					if(mappings!=null)
					{
						IValueFetcher fetcher = new ProcessThreadValueFetcher(this, false, instance.getFetcher());
						for(Iterator<String> it=mappings.keySet().iterator(); it.hasNext(); )
						{
							boolean	found	= false;
							String	name	= (String)it.next();
	//						IParsedExpression exp = (IParsedExpression)((Object[])mappings.get(name))[0];
	//						IParsedExpression iexp = (IParsedExpression)((Object[])mappings.get(name))[1];
							IParsedExpression exp = (IParsedExpression)((Tuple2<UnparsedExpression, UnparsedExpression>)mappings.get(name)).getFirstEntity().getParsed();
							UnparsedExpression uiexp = ((Tuple2<UnparsedExpression, UnparsedExpression>)mappings.get(name)).getSecondEntity();
							IParsedExpression iexp = uiexp != null? (IParsedExpression)uiexp.getParsed(): null;
							Object value;
							Object index;
							try
							{
								value	= exp.getValue(fetcher);
							}
							catch(RuntimeException e)
							{
								throw new RuntimeException("Error parsing parameter value: "+instance+", "+this+", "+name+", "+exp, e);
							}
							try
							{
								index	= iexp!=null ? iexp.getValue(fetcher) : null;
							}
							catch(RuntimeException e)
							{
								throw new RuntimeException("Error parsing parameter index: "+instance+", "+this+", "+name+", "+iexp, e);
							}
									
							// if activity has parameter with name save it in passedparams
							if(getActivity().hasParameter(name))
							{
								if(passedparams==null)
									passedparams	= new HashMap<String, Object>();
								
								// If indexed param, create name, list and add (index, value) entry
								if(iexp!=null)
								{
									if(!passedparams.containsKey(name))
									{
										passedparams.put(name, new ArrayList<Object>());
										if(indexparams==null)
											indexparams	= new HashSet<String>();
										
										indexparams.add(name);
									}
									((List<Object>)passedparams.get(name)).add(new Object[]{index, value});
								}
								// else save name, value
								else
								{
									passedparams.put(name, value);
								}
								found	= true;
							}
							
							// else if process thread has parameter, set it in thread 
							if(!found && hasParameterValue(name))
							{
								setParameterValue(name, index, value);
								found	= true;
							}
							
							// else if bpmn instance has context variable
							if(!found && getBpmnFeature(instance).hasContextVariable(name))
							{
								if(iexp!=null)
								{
									Object	array	= getBpmnFeature(instance).getContextVariable(name);
									Array.set(array, ((Number)index).intValue(), value);
								}
								else
								{
									getBpmnFeature(instance).setContextVariable(name, value);
								}
								found	= true;
							}
							else if(!found)
							{
								throw new RuntimeException("Unknown parameter or context variable: "+name+", "+this);
							}
						}
					}
				}
				else 
				{
					// Try to find data edges
					passedparams = getDataEdgeValues();
					
//					List<MDataEdge> ds = getActivity().getIncomingDataEdges();
//					if(ds!=null)
//					{
//						passedparams = new HashMap<String, Object>();
//						
//						for(MDataEdge de: ds)
//						{
//							if(dataedges!=null)
//							{
//								if(dataedges.containsKey(de.getId()))
//								{
//									String pname = de.getTargetParameter();
//									// Value is consumed -> remove?!
//									Object val = dataedges.remove(de.getId());
//								
//									// if already contains value must be identical
//									if(passedparams.containsKey(pname) && !SUtil.equals(passedparams.get(pname), val))
//										throw new RuntimeException("Different edges have different values");
//								
//									passedparams.put(pname, val);
//								}
//								else if (de.getSource() == null)
//								{
//									// Argument data edge
//									passedparams.put(de.getTargetParameter(), instance.getArguments().get(de.getSourceParameter()));
//								}
//								else
//								{
//									String pname = de.getTargetParameter();
//									if (getActivity().getParameters() == null ||
//										getActivity().getParameters().get(pname) == null ||
//										getActivity().getParameters().get(pname).getInitialValueString() == null ||
//										getActivity().getParameters().get(pname).getInitialValueString().length()==0)
//									{
//										throw new RuntimeException("Could not find data edge value for: "+de.getId());
//									}
//								}
//							}
//						}
//					}
				}
			}
			
			// todo: parameter direction / class
			
			Set<String> before = data!=null? new HashSet<String>(data.keySet()): Collections.EMPTY_SET;
			before.remove(ProcessServiceInvocationHandler.THREAD_PARAMETER_SERVICE_RESULT);	// Hack!!! Keep future available locally for thread.
			IValueFetcher fetcher = new ProcessThreadValueFetcher(this, true, instance.getFetcher());
			IndexMap<String, MParameter> params = getActivity().getParameters();
			if(params!=null)
			{
				Set<String> initialized = new HashSet<String>();
				for(Iterator<MParameter> it=params.values().iterator(); it.hasNext(); )
				{
					MParameter param = (MParameter)it.next();
					
					if(passedparams!=null && passedparams.containsKey(param.getName()))
					{
						if(indexparams!=null && indexparams.contains(param.getName()))
						{
							Object	array	= getParameterValue(param.getName());
							List<Object[]>	values	= (List<Object[]>)passedparams.get(param.getName());
							for(int i=0; i<values.size(); i++)
							{
								Object[]	entry	= (Object[])values.get(i);
								Array.set(array, ((Number)entry[0]).intValue(), entry[1]);
							}
						}
						else
						{
							setParameterValue(param.getName(), passedparams.get(param.getName()));
							before.remove(param.getName());
						}
						initialized.add(param.getName());
					}
				}
				// 2-pass to ensure correct expression evaluation?
				for(Iterator<MParameter> it=params.values().iterator(); it.hasNext(); )
				{
					MParameter param = it.next();
					if(!initialized.contains(param.getName()))
					{
						try
						{
							setParameterValue(param.getName(), param.getInitialValue()==null? null: param.getInitialValue().getParsed() == null? null: ((IParsedExpression) param.getInitialValue().getParsed()).getValue(fetcher));
							before.remove(param.getName());
						}
						catch(RuntimeException e)
						{
							e.printStackTrace();
							throw new RuntimeException("Error parsing parameter value: "+instance+", "+this+", "+param.getName()+", "+param.getInitialValue(), e);
						}
					}
				}
			}
			
			// Remove old data (all values that have not been renewed).
			for(Iterator<String> it=before.iterator(); it.hasNext(); )
			{
				String name = it.next();
//				System.out.println("removing data: "+name);
				data.remove(name);
			}
		}
	}
	
	/**
	 * 
	 */
	protected Map<String, Object> getDataEdgeValues()
	{
		Map<String, Object> passedparams = null;
		
		// Try to find data edges
		List<MDataEdge> ds = getActivity().getIncomingDataEdges();
		if(ds!=null)
		{
			passedparams = new HashMap<String, Object>();
			
			for(MDataEdge de: ds)
			{
				if(dataedges!=null)
				{
					if(dataedges.containsKey(de.getId()))
					{
						String pname = de.getTargetParameter();
						// Value is consumed -> remove?!
						Object val = dataedges.remove(de.getId());
					
						// if already contains value must be identical
						if(passedparams.containsKey(pname) && !SUtil.equals(passedparams.get(pname), val))
							throw new RuntimeException("Different edges have different values");
					
						passedparams.put(pname, val);
					}
					else if (de.getSource() == null)
					{
						// Argument data edge
						passedparams.put(de.getTargetParameter(), instance.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get(de.getSourceParameter()));
					}
					else
					{
						String pname = de.getTargetParameter();
						if (getActivity().getParameters() == null ||
							getActivity().getParameters().get(pname) == null ||
							getActivity().getParameters().get(pname).getInitialValueString() == null ||
							getActivity().getParameters().get(pname).getInitialValueString().length()==0)
						{
							throw new RuntimeException("Could not find data edge value for: "+de.getId());
						}
					}
				}
			}
		}
		
		return passedparams;
	}
	
	/**
	 *  Remove in parameters after step.
	 *  @param instance	The calling BPMN instance.
	 */
	public  void updateParametersAfterStep(MActivity activity, IInternalAccess instance)
	{
//		System.out.println("after: "+activity);
		
		if(activity!=null && (activity instanceof MTask 
			|| activity instanceof MSubProcess || activity.getActivityType().indexOf("Event")!=-1))
		{
			// Add parameter value for each out edge using the edge id
			List<MDataEdge> des = activity.getOutgoingDataEdges();
			if(des!=null && des.size()>0)
			{
				for(MDataEdge de: des)
				{
					String pname = de.getSourceParameter();
					Object value = getParameterValue(pname);
					
					if(value!=null)
					{
						if(de.getParameterMapping()!=null)
						{
							SimpleValueFetcher sf = new SimpleValueFetcher(instance.getFetcher());
							sf.setValue("$value", value);
							sf.setValue(pname, value);
							IValueFetcher fetcher = new ProcessThreadValueFetcher(this, true, sf);
							IParsedExpression exp = (IParsedExpression)de.getParameterMapping().getParsed();
							try
							{
								value	= exp.getValue(fetcher);
							}
							catch(RuntimeException e)
							{
								throw new RuntimeException("Error parsing parameter value: "+instance+", "+this+", "+pname+", "+exp, e);
							}
						}
						
						// Test if parameter value type fits
						MParameter mparam = de.getTarget().getParameters().get(de.getTargetParameter());
						Class<?> mpclz = mparam.getClazz().getType(instance.getClassLoader(), instance.getModel().getAllImports());
						if(!SReflect.isSupertype(mpclz, value.getClass()))
						{
							// Autoconvert basic from string
							if(value instanceof String)
							{
								IStringObjectConverter conv = BasicTypeConverter.getBasicStringConverter(mpclz);
								if(conv!=null)
								{
									try
									{
										value = conv.convertString((String)value, null);
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
							}
							// Autoconvert basic to string
							else if(mpclz.equals(String.class))
							{
								IObjectStringConverter conv = BasicTypeConverter.getBasicObjectConverter(value.getClass());
								if(conv!=null)
								{
									try
									{
										value = conv.convertObject(value, null);
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
							}
						}
					}
					if(de.getTarget() == null)
					{
						// Result data edge
						instance.getComponentFeature(IArgumentsResultsFeature.class).getResults().put(de.getTargetParameter(), value);
					}
					else
					{
						if(dataedges==null)
							dataedges = new HashMap<String, Object>();
						dataedges.put(de.getId(), value);
					}
				}
			}
			
			// Remove all in parameters (not in case of events)
			if(activity.getActivityType().indexOf("Event")==-1)
			{
				List<MParameter> params = activity.getParameters(new String[]{MParameter.DIRECTION_IN});
				for(int i=0; i<params.size(); i++)
				{
					MParameter inp = (MParameter)params.get(i);
					removeParameterValue(inp.getName());
	//				System.out.println("Removed thread param value: "+inp.getName());
				}
			}
		}
	}
	
	/**
	 *  Get the split infos.
	 */
	public Collection<SplitInfo> getSplitInfos()
	{
		Collection<SplitInfo>	ret;
		if(splitinfos!=null)
			ret	= splitinfos.values();
		else
			ret	= Collections.emptyList();
		return ret;
	}
	
	/**
	 *  Get a specific split info, if available.
	 */
	public SplitInfo getSplitInfo(String id)
	{
		return splitinfos!=null ? splitinfos.get(id) : null;
	}
	
	/**
	 *  Add a split info.
	 */
	public void addSplitInfo(SplitInfo spi)
	{
		if(splitinfos==null)
			splitinfos = new LinkedHashMap<String, SplitInfo>();
		assert !splitinfos.containsKey(spi.getSplitId());
//		System.out.println("push: "+getId()+" "+splitinfos);
		splitinfos.put(spi.getSplitId(), spi);
	}

	/**
	 *  Remove the split info.
	 */
	public void	removeSplitInfo(SplitInfo spi)
	{
//		System.out.println("pop: "+getId()+" "+splitinfos);
		splitinfos.remove(spi.getSplitId());
	}
//	
//	/**
//	 *  Get the topmost split info.
//	 *  @return The split info.
//	 */
//	public int[] peakSplitInfo()
//	{
//		return (int[])splitinfos.get(splitinfos.size()-1);
//	}
//	
//	/**
//	 *  Get the current split id.
//	 *  @return The split id.
//	 */
//	public int getSplitId()
//	{
//		return splitinfos==null? 0: ((int[])splitinfos.get(splitinfos.size()-1))[0];
//	}
//	
//	/**
//	 *  Get the current split count.
//	 *  @return The split count.
//	 */
//	public int getSplitCount()
//	{
//		return splitinfos==null? 0: ((int[])splitinfos.get(splitinfos.size()-1))[1];
//	}
//	
//	/**
//	 *  Get the current split depth.
//	 *  @return The split depth.
//	 */
//	public int getSplitDepth()
//	{
//		return splitinfos==null? 0: splitinfos.size();
//	}
	
//	/**
//	 *  Returns the current subcontext.
//	 *  @return The current subcontext.
//	 */
//	public ThreadContext getSubcontext()
//	{
//		return subcontext;
//	}
//	
//	/**
//	 *  Sets the subcontext.
//	 */
//	public  void setSubcontext(ThreadContext subcontext)
//	{
//		this.subcontext = subcontext;
//	}
	
	/**
	 *  Remove a sub context but keep the corresponding thread.
	 *  E.g. when a sub process terminates, the sub context is removed
	 *  and the initiating thread continues in the outer context.
	 *  @param context	The sub context to be removed.
	 */
	public void removeSubcontext()
	{
//		assert threads!=null && threads.containsKey(context.getInitiator());
		
		List<ProcessThread>	subthreads	= getSubthreads();
		if(subthreads!=null)
		{
			ProcessThread[] subt = (ProcessThread[])subthreads.toArray(new ProcessThread[subthreads.size()]);
			for(int i=0; i<subt.length; i++)
			{
				removeThread(subt[i]);
			}
		}
	}
	
	/**
	 *  Remove a thread from this context.
	 *  @param thread	The thread to be removed.
	 */
	public void removeThread(ProcessThread thread)
	{
		if(hasSubthreads())
		{
//			if(getSubcontext(thread)!=null)
//				removeSubcontext(getSubcontext(thread));
//			if(thread.hasSubthreads())
//			
			thread.removeSubcontext();
			
			// Cancel activity (e.g. timer).
			MActivity	act	= thread.getActivity();
			if(act!=null && thread.isWaiting())
			{
				getBpmnFeature(thread.getInstance()).getActivityHandler(act).cancel(act, thread.getInstance(), thread);
			}
			thread.setActivity(null);
			// Notify the thread itself that it has finished
			thread.notifyFinished();
			
			boolean rem = getSubthreads().remove(thread);
//			thread.setThreadContext(null);
			
			if(getSubthreads().isEmpty())
				subthreads	= null;
		
			if(rem)
			{
//				System.out.println("remove1: "+thread);
//				BpmnInterpreter in = thread.getInstance();
				if(getInstance().getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
				{	
					thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(getBpmnFeature(thread.getInstance()).createThreadEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL, thread), PublishTarget.TOALL);
				}
			}
		}
		
//		System.out.println("remove0: "+thread);
	}
	
	/**
	 *  Add a thread to this context.
	 *  @param thread	The thread to be added.
	 */
	public void	addThread(ProcessThread thread)
	{
//		if(threads==null)
//			threads	= new LinkedHashMap<ProcessThread, ThreadContext>();
//		
//		threads.put(thread, null);
		if(subthreads==null)
			subthreads	= new ArrayList<ProcessThread>();
		
		subthreads.add(thread);
		if(getInstance().getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
		{	
			thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(getBpmnFeature(thread.getInstance()).createThreadEvent(IMonitoringEvent.EVENT_TYPE_CREATION, thread), PublishTarget.TOALL);
		}
//		System.out.println("add: "+thread);
	}
	
	/**
	 *  Add an external thread to this context.
	 *  @param thread	The thread to be added.
	 */
	// Hack!!! Make external threads execute before others.
	public void	addExternalThread(ProcessThread thread)
	{
		if(subthreads==null)
			subthreads	= new ArrayList<ProcessThread>();
		subthreads.add(0, thread);
////		Map<ProcessThread, ThreadContext>	oldthreads	= threads;
//		Set<ProcessThread> oldthreads = threads;
////		threads	= new LinkedHashMap<ProcessThread, ThreadContext>();
//		threads = new HashSet<ProcessThread>();
////		threads.put(thread, null);
//		threads.add(thread);
//		if(oldthreads!=null)
//		{
//			threads.addAll(oldthreads);
////			threads.putAll(oldthreads);
//		}
		
		if(getInstance().getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
		{	
			thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(getBpmnFeature(thread.getInstance()).createThreadEvent(IMonitoringEvent.EVENT_TYPE_CREATION, thread), PublishTarget.TOALL);
		}
//		System.out.println("add: "+thread);
	}
	
	/**
	 *  Get all threads of the context and all subcontexts.
	 */
	public Set<ProcessThread> getAllThreads()
	{
		Set<ProcessThread> ret = new HashSet<ProcessThread>();
		if(subthreads!=null)
		{
//			for(Iterator<ProcessThread> it=threads.keySet().iterator(); it.hasNext(); )
			for(Iterator<ProcessThread> it=subthreads.iterator(); it.hasNext(); )
			{
				ProcessThread pc = it.next();
				ret.add(pc);
//				ThreadContext tc = (ThreadContext)threads.get(pc);
				//ThreadContext tc = pc.getSubcontext();
				if(pc.hasSubthreads())
				{
					ret.addAll(pc.getAllThreads());
				}
			}
		}
		return ret;
	}	
	
//	public void removeSubcontext(ThreadContext context)
//	{
////		assert threads!=null && threads.containsKey(context.getInitiator());
//		
//		Set<ProcessThread>	subthreads	= context.getThreads();
//		if(subthreads!=null)
//		{
//			ProcessThread[] subt = (ProcessThread[])subthreads.toArray(new ProcessThread[subthreads.size()]);
//			for(int i=0; i<subt.length; i++)
//			{
//				context.removeThread(subt[i]);
//			}
//		}
//
//		threads.put(context.getInitiator(), null);
//	}
	
	/**
	 *  Get the parent.
	 *  return The parent.
	 */
	public ProcessThread getParent()
	{
		return parent;
	}

	/**
	 *  Set the parent. 
	 *  @param parent The parent to set.
	 */
	public void setParent(ProcessThread parent)
	{
		this.parent = parent;
	}
	
	/**
	 *  Get the subthreads.
	 */
	public List<ProcessThread> getSubthreads()
	{
		return subthreads;
	}
	
	/**
	 *  Test if thread has subthreads.
	 */
	public boolean hasSubthreads()
	{
		return subthreads!=null && !subthreads.isEmpty();
	}
	
	/**
	 *  The context is finished, when there are no (more) threads to execute.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the process instance is finished with regards to the specified pool/lane. When both pool and lane are null, true is returned only when all pools/lanes are finished.
	 */
	public boolean	isFinished(String pool, String lane)
	{
		boolean	finished	= true;
		if(subthreads!=null && !subthreads.isEmpty())
		{
//			for(Iterator<ProcessThread> it=threads.keySet().iterator(); finished && it.hasNext(); )
			for(Iterator<ProcessThread> it=subthreads.iterator(); finished && it.hasNext(); )
			{
				finished = !it.next().belongsTo(pool, lane);
			}
		}
		return finished;
	}
	
	/**
	 *  Get an executable thread in the context or its sub contexts.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return	An executable thread (if any).
	 */
	public ProcessThread getExecutableThread(String pool, String lane)
	{
		// Iterate over all thread contexts and find executable thread
		ProcessThread	ret	= null;
		if(getSubthreads()!=null)
		{
			for(Iterator<ProcessThread> it=getSubthreads().iterator(); ret==null && it.hasNext(); )
			{
				ProcessThread thread = it.next();
				
				if(thread.getSubthreads()!=null)
				{
					ret	= thread.getExecutableThread(pool, lane);
				}
				else if(!thread.isWaiting() && thread.belongsTo(pool, lane))
				{
					ret	= thread;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get a thread per id.
	 *  @param id The thread id.
	 *  @return The process thread.
	 */
	public ProcessThread getThread(String id)
	{
		ProcessThread ret = null;
		if(SUtil.equals(getId(), id))
		{
			ret = this;
		}
		else if(getSubthreads()!=null)
		{
			for(Iterator<ProcessThread> it=getSubthreads().iterator(); ret==null && it.hasNext(); )
			{
				ProcessThread thread = it.next();
				ret = thread.getThread(id);
				if(ret!=null)
				{
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get a cnt for subprocesses.
	 */
	protected String getNextChildId()
	{
		return getId()!=null? getId()+":"+idcnt++: ""+idcnt++;
	}
	
	/**
	 *  Get the loopcmd.
	 *  @return The loopcmd.
	 */
	public IResultCommand<Boolean, Void> getLoopCommand()
	{
		return loopcmd;
	}

	/**
	 *  Set the loopcmd.
	 *  @param loopcmd The loopcmd to set.
	 */
	public void setLoopCommand(IResultCommand<Boolean, Void> loopcmd)
	{
		this.loopcmd = loopcmd;
	}

	/**
	 *  Method that can be used to determine (override) that the thread is finished.
	 */
	public void notifyFinished()
	{
	}
	
	/**
	 *  Get the bpmn feature.
	 */
	protected IInternalBpmnComponentFeature getBpmnFeature(IInternalAccess ia)
	{
		return (IInternalBpmnComponentFeature)ia.getComponentFeature(IBpmnComponentFeature.class);
	}
	
	/**
	 *  Create a string representation of this process thread.
	 *  @return A string representation of this process thread.
	 */
	public String	toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(id=");
		buf.append(id);
		buf.append("(activity=");
		buf.append(activity);
		buf.append(", data=");
		buf.append(data);
		buf.append(", dataedges=");
		buf.append(dataedges);
		buf.append(", waiting=");
		buf.append(waiting);
		buf.append(")");
		return buf.toString();
	}
}
