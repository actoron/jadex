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

	/** The exclude mode. */
	protected String excludemode;

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

	/**
	 *  Get the exclude mode.
	 *  @return The exclude mode.
	 */
	public String getExcludeMode()
	{
		return this.excludemode;
	}

	/**
	 *  Set the exclude mode.
	 *  @param excludemode The exclude mode to set.
	 */
	public void setExcludeMode(String excludemode)
	{
		this.excludemode = excludemode;
	}		
}
