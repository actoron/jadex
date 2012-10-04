package jadex.backup.job;

/**
 * 
 */
public abstract class Job
{
	/** The id. */ 
	protected String id;
	
	/** The job name. */
	protected String name;
	
	/**
	 *  Create a new job.
	 */
	public Job(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

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
}
