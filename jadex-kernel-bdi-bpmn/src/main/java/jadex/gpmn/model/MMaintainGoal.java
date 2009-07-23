package jadex.gpmn.model;

/**
 *  Model for a maintain goal.
 */
public class MMaintainGoal extends MGoal
{
	//-------- attributes --------
	
	/** The maintain condition. */
	protected String maintaincondition;

	//-------- methods --------
	
	/**
	 *  Get the maintaincondition.
	 *  @return The maintaincondition.
	 */
	public String getMaintainCondition()
	{
		return this.maintaincondition;
	}

	/**
	 *  Set the maintaincondition.
	 *  @param maintaincondition The maintaincondition to set.
	 */
	public void setMaintainCondition(String maintaincondition)
	{
		this.maintaincondition = maintaincondition;
	}
}
