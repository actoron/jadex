package jadex.bdi.model;

/**
 *  Interface for goalbase model.
 */
public interface IMGoalbase
{
    /**
	 *  Get a goal for a name.
	 *  @param name	The goal name.
	 */
	public IMGoal getGoal(String name);

	/**
	 *  Returns all goals.
	 *  @return All goals.
	 */
	public IMGoal[] getGoals();
	
	/**
	 *  Get a goal reference for a name.
	 *  @param name	The goal reference name.
	 */
	public IMGoalReference getGoalReference(String name);

	/**
	 *  Get all goal references.
	 *  @param name	Goal references.
	 */
	public IMGoalReference[] getGoalReferences();
}
