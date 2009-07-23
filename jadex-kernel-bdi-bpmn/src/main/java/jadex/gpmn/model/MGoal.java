package jadex.gpmn.model;


/**
 *  Base class for all kinds of goals.
 */
public class MGoal extends MProcessElement
{
	//-------- attributes --------
	
	/** The creation condition. */
	protected String creationcondition;
	
	/** The context condition. */
	protected String contextcondition;
	
	/** The drop condition. */
	protected String dropcondition;

	//-------- methods --------
	
	/**
	 *  Get the creationcondition.
	 *  @return The creationcondition.
	 */
	public String getCreationCondition()
	{
		return this.creationcondition;
	}

	/**
	 *  Set the creationcondition.
	 *  @param creationcondition The creationcondition to set.
	 */
	public void setCreationCondition(String creationcondition)
	{
		this.creationcondition = creationcondition;
	}

	/**
	 *  Get the contextcondition.
	 *  @return The contextcondition.
	 */
	public String getContextCondition()
	{
		return this.contextcondition;
	}

	/**
	 *  Set the contextcondition.
	 *  @param contextcondition The contextcondition to set.
	 */
	public void setContextCondition(String contextcondition)
	{
		this.contextcondition = contextcondition;
	}

	/**
	 *  Get the dropcondition.
	 *  @return The dropcondition.
	 */
	public String getDropCondition()
	{
		return this.dropcondition;
	}

	/**
	 *  Set the dropcondition.
	 *  @param dropcondition The dropcondition to set.
	 */
	public void setDropCondition(String dropcondition)
	{
		this.dropcondition = dropcondition;
	}	
	
}
