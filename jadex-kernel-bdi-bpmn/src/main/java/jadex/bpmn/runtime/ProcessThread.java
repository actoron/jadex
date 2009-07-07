package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.commons.SReflect;

import java.util.HashMap;
import java.util.Map;

/**
 *  Representation of a single control flow in a BPMN process instance,
 *  i.e. an instance of a sequence flow.
 */
public class ProcessThread	implements ITaskContext
{
	//-------- attributes --------
	
	/** The next activity. */
	protected MActivity	activity;
	
	/** The last edge (if any). */
	protected MSequenceEdge	edge;
	
	/** Is the process in a waiting state. */
	protected boolean	waiting;
	
	/** The execution context. */
	protected ThreadContext	context;
	
	/** The contexts (data) of previous activities (task name ->). */
	protected Map	contexts;
	
	/** The contexts (data) of previous activities. */
	protected Map	values;
	
	/** The exception that has just occurred in the process (if any). */
	protected Exception	exception;
	
	//-------- constructors --------

	/**
	 *  Create a new process instance.
	 *  @param activity	The current activity.
	 */
	public ProcessThread(MActivity activity, ThreadContext context)
	{
		this.activity	= activity;
		this.context	= context;
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
		buf.append(")");
		return buf.toString();
	}

	/**
	 *  Get the next activity.
	 *  @return The activity.
	 */
	public MActivity getNextActivity()
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
		return this.waiting;
	}

	/**
	 *  Set the process waiting state (i.e. blocked). 
	 *  @param waiting	The waiting flag.
	 */
	public void setWaiting(boolean waiting)
	{
		this.waiting	= waiting;
	}
	
	/**
	 *  Create a copy of this thread (e.g. for a parallel split).
	 */
	public ProcessThread	createCopy()
	{
		ProcessThread	ret	= new ProcessThread(activity, context);
		ret.edge	= edge;
		ret.contexts	= contexts;
		ret.values	= null;
		context.addThread(ret);
		return ret;
		
	}
	
	//-------- ITaskContext --------
	
	/**
	 *  Get the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @return	The parameter value. 
	 */
	public Object	getParameterValue(String name)
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
	
	/**
	 *  Get the execution context of the thread.
	 */
	public ThreadContext	getThreadContext()
	{
		return context;
	}
}
