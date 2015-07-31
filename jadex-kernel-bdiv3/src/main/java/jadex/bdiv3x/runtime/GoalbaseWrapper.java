package jadex.bdiv3x.runtime;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.runtime.IGoal;
import jadex.commons.future.IFuture;

/**
 *  Prepend capability prefix to goal names.
 */
public class GoalbaseWrapper implements IGoalbase
{
	//-------- attributes --------
	
	/** The flat goal base. */
	protected IGoalbase	goalbase;
	
	/** The full capability prefix. */
	protected String	prefix;
		
	//-------- constructors --------
	
	/**
	 *  Create a goal base wrapper.
	 */
	public GoalbaseWrapper(IGoalbase goalbase, String prefix)
	{
		this.goalbase	= goalbase;
		this.prefix	= prefix;
	}
	
	//-------- element methods ---------

	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public MElement getModelElement()
	{
		return goalbase.getModelElement();
	}
	
	//-------- IGoalbase methods --------
	
	/**
	 *  Test if an adopted goal is already contained in the goal base.
	 *  @param goal	The goal to test.
	 *  @return True, if the goal is contained.
	 */
	public boolean containsGoal(IGoal goal)
	{
		return goalbase.containsGoal(goal);
	}

	/**
	 *  Get all proprietary goals of a specified type (=model element name).
	 *  @param type The goal type.
	 *  @return All proprietary goals of the specified type.
	 */
	public IGoal[] getGoals(String type)
	{
		return goalbase.getGoals(prefix + type);
	}

	/**
	 *  Get all the adopted goals in this scope (including subgoals).
	 *  @return All goals and subgoals.
	 */
	public IGoal[]	getGoals()
	{
		List<IGoal>	ret	= new ArrayList<IGoal>();
		for(IGoal goal: goalbase.getGoals())
		{
			if(goal.getModelElement().getName().startsWith(prefix))
			{
				ret.add(goal);
			}
		}
		return ret.toArray(new IGoal[ret.size()]);
	}

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
	public IGoal createGoal(String type)
	{
		return goalbase.createGoal(prefix + type);
	}

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public <T>	IFuture<T>	dispatchTopLevelGoal(IGoal goal)
	{
		return goalbase.dispatchTopLevelGoal(goal);
	}
}