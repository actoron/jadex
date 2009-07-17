package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MSequenceEdge;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *  Representation of a single control flow in a BPMN process instance,
 *  i.e. an instance of a sequence flow.
 */
public class ProcessThread	implements ITaskContext
{
	//-------- constants --------
	
//	/** Waiting constant for time. */
//	public static String WAITING_FOR_TIME = "waiting_for_time";
//	
//	/** Waiting constant for message. */
//	public static String WAITING_FOR_MESSAGE = "waiting_for_message";
//	
//	/** Waiting constant for condition. */
//	public static String WAITING_FOR_CONDITION = "waiting_for_condition";
//	
//	/** Waiting constant for join. */
//	public static String WAITING_FOR_JOIN = "waiting_for_join";
//
//	/** Waiting constant for task. */
//	public static String WAITING_FOR_TASK = "waiting_for_task";
//
//	/** Waiting constant for subprocess. */
//	public static String WAITING_FOR_SUBPROCESS = "waiting_for_subprocess";
//	
//	/** Waiting constant for multi. */
//	public static String WAITING_FOR_MULTI = "waiting_for_multi";
	
	//-------- attributes --------
	
	/** The next activity. */
	protected MActivity	activity;
	
	/** The last edge (if any). */
	protected MSequenceEdge	edge;
		
	/** The data of previous activities (task name -> Map). */
	protected Map	data;
	
	/** The thread context. */
	protected ThreadContext	context;
	
	/** The Bpmn instance. */
	protected BpmnInstance instance;
	
	/** The exception that has just occurred in the process (if any). */
	protected Exception	exception;
	
	/** Is the process in a waiting state. */
//	protected String	waiting;
	protected boolean waiting;
	
	/** The wait info. */
	protected Object waitinfo;
	
	/** The wait filter. */
	protected IFilter waitfilter;
	
	//-------- constructors --------

	/**
	 *  Create a new process instance.
	 *  @param activity	The current activity.
	 */
	public ProcessThread(MActivity activity, ThreadContext context, BpmnInstance instance)
	{
		this.activity	= activity;
		this.context	= context;
		this.instance = instance;
	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation of this process thread.
	 *  @return A string representation of this process thread.
	 */
	public String	toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(activity=");
		buf.append(activity);
		buf.append(", data=");
		buf.append(data);
		buf.append(", waiting=");
		buf.append(waiting);
		buf.append(")");
		return buf.toString();
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
	 *  @return The activity.
	 */
	public void	setActivity(MActivity activity)
	{
		this.edge	= null;
		this.activity	= activity;
		
		// Remove old data, if activity was executed before.
		if(activity!=null && data!=null)
			data.remove(activity.getName());
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
		this.waiting = waiting;
	}
	
	/**
	 *  Set to non waiting.
	 *  @return The waiting flag.
	 */
	public void	setNonWaiting()
	{
		this.waiting = false;
		this.waitinfo = null;
		this.waitfilter = null;
//		System.out.println("Thread: "+this+" "+waiting);
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
	
	/**
	 *  Set the process waiting info. 
	 *  @param waiting	The waiting info.
	 */
	public void setWaitInfo(Object waitinfo)
	{
		this.waitinfo = waitinfo;
	}
	
	/**
	 *  Get the waitinfo.
	 *  @return The waitinfo.
	 */
	public Object getWaitInfo()
	{
		return this.waitinfo;
	}
	
	/**
	 *  Get the wait filter.
	 *  @return The waitfilter.
	 */
	public IFilter getWaitFilter()
	{
		return this.waitfilter;
	}

	/**
	 *  Set the wait filter.
	 *  @param waitfilter The waitfilter to set.
	 */
	public void setWaitFilter(IFilter waitfilter)
	{
		this.waitfilter = waitfilter;
	}
	
	/**
	 *  Get the thread context
	 *  @return	The thread context.
	 */
	public ThreadContext	getThreadContext()
	{
		return context;
	}

	/**
	 *  Create a copy of this thread (e.g. for a parallel split).
	 */
	public ProcessThread	createCopy()
	{
		ProcessThread	ret	= new ProcessThread(activity, context, instance);
		ret.edge	= edge;
		ret.data	= data;
		return ret;
	}
	
	//-------- ITaskContext --------
	
	/**
	 *  Test if a parameter has been set on activity.
	 *  @param name	The parameter name. 
	 *  @return	True if parameter is known.
	 */
	public boolean hasParameterValue(String name)
	{
		assert activity!=null;
		return data!=null && data.containsKey(activity.getName())? ((Map)data.get(activity.getName())).containsKey(name): false;
	}

	/**
	 *  Get the model element.
	 *  @return	The model of the task.
	 */
	public MActivity	getModelElement()
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
		assert activity!=null;
		return data!=null && data.containsKey(activity.getName())? ((Map)data.get(activity.getName())).get(name): null;
	}

	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	public void	setParameterValue(String name, Object value)
	{
		assert activity!=null;
		if(data==null)
			data	= new HashMap();
			
		if(!data.containsKey(activity.getName()))
		{
			data.put(activity.getName(), new HashMap());
		}
			
		((Map)data.get(activity.getName())).put(name, value);
	}
	
	/**
	 *  Get the value of a property.
	 *  @param name	The property name. 
	 *  @return	The property value. 
	 */
	public Object	getPropertyValue(String name)
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
	public Object	getPropertyValue(String name, MActivity activity)
	{
		assert activity!=null;
		Object ret	= activity.getPropertyValue(name);
		if(ret instanceof IParsedExpression)
		{
			ret = ((IParsedExpression)ret).getValue(new ProcessThreadValueFetcher(this, true, instance.getValueFetcher()));
		}
		return ret;
	}
	
	/**
	 *  Get the context of a previously executed task.
	 *  @param name	The name of the task.
	 *  @return	The context (if any).
	 */
	public Map getData(String name)
	{
		return data!=null ? (Map)data.get(name) : null;
	}

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
	 *  Test if the thread belongs to the given pool and/or lane.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the thread belongs to the given pool and/or lane. Also returns true when both pool and lane are null.
	 */
	public boolean belongsTo(String pool, String lane)
	{
		// Test pool.
		boolean	ret	= pool==null || pool.equals(getActivity().getPool().getName());
		
		// Test lane
		if(ret && lane!=null)
		{
			List	lanes	= new ArrayList();
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
}
