package jadex.backup.job;

import java.text.SimpleDateFormat;

/**
 *  A task is a small unit of work inside a job.
 */
public class Task
{
	public static final String	STATE_OPEN	= "open";

	public static final String	STATE_ACKNOWLEDGED	= "acknowledged";

	public static final String	STATE_ACTIVE = "active";

	public static final String	STATE_FINISHED	= "finished";

	protected static long cnt = 0;

	public static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm MM dd yyyy");

	/** The id. */
	protected long id;
  
	/** The creation date. */
	protected long date;
	
	/** The request state. */
	protected String state;
	
	/**
	 *  Create a new sync request.
	 */
	public Task()
	{
	}
	
	/**
	 *  Create a new sync request.
	 */
	public Task(long date)
	{
		this.id = cnt++;
		this.date = date;
		this.state = STATE_OPEN;
	}

	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public long getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(long id)
	{
		this.id = id;
	}
	
	/**
	 *  Get the date.
	 *  @return The date.
	 */
	public long getDate()
	{
		return date;
	}

	/**
	 *  Set the date.
	 *  @param date The date to set.
	 */
	public void setDate(long date)
	{
		this.date = date;
	}

	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		this.state = state;
	}
	
}
