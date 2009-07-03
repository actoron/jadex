package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.commons.SReflect;

import java.util.Map;

/**
 *  Representation of a single control flow in a BPMN process instance,
 *  i.e. an instance of a sequence flow.
 */
public class ProcessThread
{
	//-------- attributes --------
	
	/** The next activity. */
	protected MActivity	activity;
	
	/** The last edge (if any). */
	protected MSequenceEdge	edge;
	
	/** Is the process in a waiting state. */
	protected boolean	waiting;
	
	/** The currently available output values. */
	protected Map	values;
	
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
		this.activity	= (MActivity) edge.getTarget();
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
		ProcessThread	ret	= new ProcessThread(null);
		ret.activity	= activity;
		ret.edge	= edge;
//		ret.values	= new HashMap(values);
		return ret;
		
	}
}
