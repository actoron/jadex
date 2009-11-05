package jadex.gpmn.model;

/**
 *  Model for an achieve goal.
 */
public class MAchieveGoal extends MGoal
{
	//-------- attributes --------
	
	/** The target condition. */
	protected String targetcondition;

	//-------- methods --------
	
	/**
	 *  Get the target condition.
	 *  @return The targetcondition.
	 */
	public String getTargetCondition()
	{
		return this.targetcondition;
	}

	/**
	 *  Set the target condition.
	 *  @param targetcondition The targetcondition to set.
	 */
	public void setTargetCondition(String targetcondition)
	{
		this.targetcondition = targetcondition;
	}
	
}
