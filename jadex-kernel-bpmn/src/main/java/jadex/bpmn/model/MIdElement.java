package jadex.bpmn.model;

/**
 *  Base model element with an id. 
 */
public class MIdElement
{
	//-------- attributes --------
	
	/** The id. */
	protected String id;

	//-------- methods ---------
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 *  Set the id.
	 *  @param id the id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}
}
