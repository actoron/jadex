package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.commons.IFilter;
import jadex.commons.SReflect;

import java.util.HashMap;
import java.util.Map;

/**
 *  Representation of a single control flow in a BPMN process instance,
 *  i.e. an instance of a sequence flow.
 */
public class ProcessThread	implements ITaskContext
{
	//-------- constants --------
	
	/** Waiting constant for time. */
	public static String WAITING_FOR_TIME = "waiting_for_time";
	
	/** Waiting constant for message. */
	public static String WAITING_FOR_MESSAGE = "waiting_for_message";
	
	/** Waiting constant for condition. */
	public static String WAITING_FOR_CONDITION = "waiting_for_condition";
	
	/** Waiting constant for join. */
	public static String WAITING_FOR_JOIN = "waiting_for_join";

	/** Waiting constant for task. */
	public static String WAITING_FOR_TASK = "waiting_for_task";

	/** Waiting constant for subprocess. */
	public static String WAITING_FOR_SUBPROCESS = "waiting_for_subprocess";
	
	/** Waiting constant for multi. */
	public static String WAITING_FOR_MULTI = "waiting_for_multi";
	
	//-------- attributes --------
	
	/** The next activity. */
	protected MActivity	activity;
	
	/** The last edge (if any). */
	protected MSequenceEdge	edge;
		
	/** The contexts (data) of previous activities (task name ->). */
	protected Map	contexts;
	
	/** The contexts (data) of previous activities. */
	protected Map	values;
	
	/** The exception that has just occurred in the process (if any). */
	protected Exception	exception;
	
	/** Is the process in a waiting state. */
	protected String	waiting;

	/** The wait info. */
	protected Object waitinfo;
	
	/** The wait filter. */
	protected IFilter waitfilter;
	
	//-------- constructors --------

	/**
	 *  Create a new process instance.
	 *  @param activity	The current activity.
	 */
	public ProcessThread(MActivity activity)
	{
		this.activity	= activity;
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
		buf.append(", contexts=");
		buf.append(contexts);
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
	public void	setNextActivity(MActivity activity)
	{
		setLastEdge(null);
		this.activity	= activity;
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
		this.edge	= edge;
		this.activity	= edge!=null ? (MActivity) edge.getTarget() : null;
		this.values	= null;	// Clean context for next activity;
	}
	
	/**
	 *  Is the process in a waiting state (i.e. blocked)? 
	 *  @return The waiting flag.
	 */
	public boolean	isWaiting()
	{
		return this.waiting!=null;
	}
	
	/**
	 *  Set to non waiting.
	 *  @return The waiting flag.
	 */
	public void	setNonWaiting()
	{
		this.waiting = null;
	}
	
	/**
	 *  Get the waiting type. 
	 *  @return The waiting type.
	 */
	public String getWaitingState()
	{
		return this.waiting;
	}

	/**
	 *  Set the process waiting state (i.e. blocked). 
	 *  @param waiting	The waiting flag.
	 */
	public void setWaitingState(String waiting)
	{
		this.waiting = waiting;
	}
	
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
	 *  Create a copy of this thread (e.g. for a parallel split).
	 */
	public ProcessThread	createCopy()
	{
		ProcessThread	ret	= new ProcessThread(activity);
		ret.edge	= edge;
		ret.contexts	= contexts;
		ret.values	= null;
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
		return values!=null? values.containsKey(name): false;
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
		return values!=null ? values.get(name) : null;
	}

	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	public void	setParameterValue(String name, Object value)
	{
		if(values==null)
		{
			values	= new HashMap();
			
			if(contexts==null)
			{
				contexts	= new HashMap();
			}
			
			contexts.put(activity.getName(), values);
		}
		
		values.put(name, value);
	}

	/**
	 *  Get the context of a previously executed task.
	 *  @param name	The name of the task.
	 *  @return	The context (if any).
	 */
	public Map getContext(String name)
	{
		return contexts!=null ? (Map)contexts.get(name) : null;
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
}
