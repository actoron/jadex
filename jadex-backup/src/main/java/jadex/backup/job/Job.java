package jadex.backup.job;

import jadex.bridge.IExternalAccess;

/**
 *  Base class for all kinds of jobs.
 */
public abstract class Job
{
	//-------- attributes --------
	
	/** The id. */ 
	protected String id;
	
	/** The job name. */
	protected String name;
	
	/** Flag if job is active. */
	protected boolean active;
	
	//-------- constructors --------
	
	/**
	 *  Create a new job.
	 */
	public Job()
	{
	}
	
	/**
	 *  Create a new job.
	 */
	public Job(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	//-------- methods --------
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}	

	/**
	 *  Get the active.
	 *  @return The active.
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 *  Set the active.
	 *  @param active The active to set.
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	//-------- additional convenience mapping methods --------
	
	/**
	 *  Get the agent type.
	 */
	public String getAgentType()
	{
		return null;
	}
	
	/**
	 *  Get the view.
	 */
	public Object getView(IExternalAccess ea, boolean editable)
	{
		return null;
	}

}
