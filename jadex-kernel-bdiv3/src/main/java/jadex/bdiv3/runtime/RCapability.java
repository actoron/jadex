package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MCapability;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class RCapability extends RElement
{
	/** The beliefs. */
//	protected List<RBelief> beliefs;
	
	/** The goals. */
	protected List<RGoal> goals;
	
	/** The plans. */
	protected List<RPlan> plans;

	/**
	 *  Create a new bdi state.
	 */
	public RCapability(MCapability mcapa)
	{
		super(mcapa);
	}
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public List<RGoal> getGoals()
	{
		return goals;
	}

	/**
	 *  Set the goals.
	 *  @param goals The goals to set.
	 */
	public void setGoals(List<RGoal> goals)
	{
		this.goals = goals;
	}

	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public List<RPlan> getPlans()
	{
		return plans;
	}

	/**
	 *  Set the plans.
	 *  @param plans The plans to set.
	 */
	public void setPlans(List<RPlan> plans)
	{
		this.plans = plans;
	}
	
	/**
	 *  Add a new goal.
	 *  @param goal The goal.
	 */
	public void addGoal(RGoal goal)
	{
		if(goals==null)
		{
			goals = new ArrayList<RGoal>();
		}
		goals.add(goal);
	}
	
	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(RGoal goal)
	{
		if(goal!=null)
		{
			goals.remove(goal);
		}
	}
	
	/**
	 *  Add a new goal.
	 *  @param goal The goal.
	 */
	public void addPlan(RPlan plan)
	{
		if(plans==null)
		{
			plans = new ArrayList<RPlan>();
		}
		plans.add(plan);
	}
	
	/**
	 *  Remove a plan.
	 *  @param plan The plan.
	 */
	public void removePlan(RPlan plan)
	{
		if(plan!=null)
		{
			plans.remove(plan);
		}
	}
}
