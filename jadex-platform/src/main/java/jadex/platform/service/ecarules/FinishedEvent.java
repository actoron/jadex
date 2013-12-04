package jadex.platform.service.ecarules;

/**
 * 
 */
public class FinishedEvent
{
	/** The id. */
	protected int id;
	
	/**
	 *  Create a new rule event.
	 */
	public FinishedEvent(int id)
	{
		this.id = id;
	}

	/**
	 *  Get the id.
	 *  return The id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 *  Set the id. 
	 *  @param id The id to set.
	 */
	public void setId(int id)
	{
		this.id = id;
	}
}
