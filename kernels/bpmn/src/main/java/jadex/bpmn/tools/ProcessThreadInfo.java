package jadex.bpmn.tools;

import jadex.commons.SUtil;


/**
 *  Visualization data about a process thread. 
 */
public class ProcessThreadInfo
{
	//-------- attributes --------
	
	/** The thread that executed this activity. */
	protected String id;
	
	/** The parent id. */
	protected String parentid;
	
	/** The activity name. */
	protected String activity;
	
	/** The activity id. */
	protected String actid;

	/** The pool name. */
	protected String pool;

	/** The lane name. */
	protected String lane;
	
	/** The exception that has just occurred in the process (if any). */
	protected String exception;
	
	/** Is the process in a waiting state. */
	protected boolean waiting;
	
	/** The data of the process. */
	protected String data;
	
	/** The data edges of the process. */
	protected String edges;

	//-------- constructors --------
	
	/**
	 *  Create a new info.
	 */
	public ProcessThreadInfo()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a new history info.
	 */
	public ProcessThreadInfo(String id, String parentid, String activity, String actid, String pool, String lane)
	{
		this.id = id;
		this.parentid = parentid;
		this.activity = activity;
		this.actid = actid;
		this.pool = pool;
		this.lane = lane;
	}
	
	/**
	 *  Create a new process info.
	 */
	public ProcessThreadInfo(String id, String parentid, String activity, String actid, String pool, String lane, 
		String exception, boolean waiting, String data, String edges)
	{
		this.id = id;
		this.parentid = parentid;
		this.activity = activity;
		this.actid = actid;
		this.pool = pool;
		this.lane = lane;
		this.exception = exception;
		this.waiting = waiting;
		this.data = data;
		this.edges = edges;
	}
	
	//-------- methods --------

	/**
	 *  Get the thread id.
	 *  @return The thread id.
	 */
	public String getThreadId()
	{
		return this.id;
	}

	/**
	 *  Set the thread id.
	 *  @param id	The thread id.
	 */
	public void	setThreadId(String id)
	{
		this.id	= id;
	}
	
	/**
	 *  Get the parentid.
	 *  return The parentid.
	 */
	public String getParentId()
	{
		return parentid;
	}

	/**
	 *  Set the parentid. 
	 *  @param parentid The parentid to set.
	 */
	public void setParentId(String parentid)
	{
		this.parentid = parentid;
	}

	/**
	 *  Get the activity.
	 *  @return The activity.
	 */
	public String getActivity()
	{
		return this.activity;
	}

	/**
	 *  Set the activity.
	 *  @param activity The activity.
	 */
	public void	setActivity(String activity)
	{
		this.activity	= activity;
	}

	/**
	 *  Get the actid.
	 *  @return The actid.
	 */
	public String getActId()
	{
		return actid;
	}

	/**
	 *  Set the actid.
	 *  @param actid The actid to set.
	 */
	public void setActId(String actid)
	{
		this.actid = actid;
	}

	/**
	 *  Get the pool.
	 *  @return The pool.
	 */
	public String getPool()
	{
		return this.pool;
	}

	/**
	 *  Set the pool.
	 *  @param pool	The pool.
	 */
	public void setPool(String pool)
	{
		this.pool	= pool;
	}

	/**
	 *  Get the lane.
	 *  @return The lane.
	 */
	public String getLane()
	{
		return this.lane;
	}

	/**
	 *  Set the lane.
	 *  @param lane	The lane.
	 */
	public void setLane(String lane)
	{
		this.lane	= lane;
	}

	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public String getException()
	{
		return this.exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception.
	 */
	public void	setException(String exception)
	{
		this.exception	= exception;
	}

	/**
	 *  Get the waiting flag.
	 *  @return The waiting flag.
	 */
	public boolean isWaiting()
	{
		return this.waiting;
	}

	/**
	 *  Set the waiting flag.
	 *  @param waiting The waiting flag.
	 */
	public void setWaiting(boolean waiting)
	{
		this.waiting	= waiting;
	}

	/**
	 *  Get the data.
	 *  @return The data.
	 */
	public String getData()
	{
		return this.data;
	}

	/**
	 *  Set the data.
	 *  @param data The data.
	 */
	public void getData(String data)
	{
		this.data = data;
	}
	
	/**
	 *  Get the edges.
	 *  @return The edges.
	 */
	public String getEdges()
	{
		return edges;
	}

	/**
	 *  Set the edges.
	 *  @param edges The edges to set.
	 */
	public void setEdges(String edges)
	{
		this.edges = edges;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ProcessThreadInfo(id="+id
			+ ", activity=" + this.activity+")"; 
//			+ ", pool=" + this.pool
//			+ ", lane=" + this.lane 
//			+ ", exception=" + this.exception
//			+ ", waiting="+ this.waiting + ")";
	}

	/**
	 *  Test if two objects are equal.
	 */
	public boolean	equals(Object obj)
	{
		return obj instanceof ProcessThreadInfo && SUtil.equals(((ProcessThreadInfo)obj).id, id);
	}
	
	/**
	 *  Get the hashcode
	 */
	public int	hashCode()
	{
		return 31+id.hashCode();
	}
}