package jadex.bdi.model.editable;

import jadex.bdi.model.IMGoalbase;

/**
 * 
 */
public interface IMEGoalbase extends IMGoalbase, IMEElement
{
	/**
	 *  Create a perform goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEPerformGoal createPerformGoal(String name);
	
	/**
	 *  Create a achieve goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEAchieveGoal createAchieveGoal(String name);
	
	/**
	 *  Create a query goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEQueryGoal createQueryGoal(String name);
	
	/**
	 *  Create a maintain goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEMaintainGoal createMaintainGoal(String name);
	
	/**
	 *  Create a meta goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEMetaGoal createMetaGoal(String name);

	/**
	 *  Get a goal reference for a name.
	 *  @param name	The goal reference name.
	 *  @param ref The referenced element name.
	 */
	public IMEGoalReference createGoalReference(String name, String ref);

}
