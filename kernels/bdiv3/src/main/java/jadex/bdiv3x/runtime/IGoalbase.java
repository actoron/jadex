package jadex.bdiv3x.runtime;

import jadex.bdiv3.runtime.IGoal;
import jadex.commons.future.IFuture;


/**
 *  The programmers interface for the goalbase.
 *  todo: getTopLevelGoals() ?
 */
public interface IGoalbase extends IElement
{
	/**
	 *  Get a (proprietary) adopted goal by name.
	 *  @param name	The goal name.
	 *  @return The goal (if found).
	 */
//	public IGoal getGoal(String name);

	/**
	 *  Test if an adopted goal is already contained in the goal base.
	 *  @param goal	The goal to test.
	 *  @return True, if the goal is contained.
	 */
	public boolean containsGoal(IGoal goal);

	/**
	 *  Get all proprietary goals of a specified type (=model element name).
	 *  @param type The goal type.
	 *  @return All proprietary goals of the specified type.
	 */
	public IGoal[] getGoals(String type);

	/**
	 *  Get all the adopted goals in this scope (including subgoals).
	 *  @return All goals and subgoals.
	 */
	public IGoal[]	getGoals();

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
	public IGoal createGoal(String type);

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public <T>	IFuture<T>	dispatchTopLevelGoal(IGoal goal);

	/**
	 *  Register a new goal model.
	 *  @param mgoal The goal model.
	 */
//	public void registerGoal(IMGoal mgoal);

	/**
	 *  Deregister a goal model.
	 *  @param mgoal The goal model.
	 */
//	public void deregisterGoal(IMGoal mgoal);

	/**
	 *  Register a new goal reference model.
	 *  @param mgoalref The goal reference model.
	 */
//	public void registerGoalReference(IMGoalReference mgoalref);

	/**
	 *  Deregister a goal reference model.
	 *  @param mgoalref The goal reference model.
	 */
//	public void deregisterGoalReference(IMGoalReference mgoalref);
	
	//-------- listeners --------
	
//	/**
//	 *  Add a goal listener.
//	 *  @param type	The goal type.
//	 *  @param listener The goal listener.
//	 */
//	public void addGoalListener(String type, IGoalListener listener);	
//	
//	/**
//	 *  Remove a goal listener.
//	 *  @param type	The goal type.
//	 *  @param listener The goal listener.
//	 */
//	public void removeGoalListener(String type, IGoalListener listener);
}
