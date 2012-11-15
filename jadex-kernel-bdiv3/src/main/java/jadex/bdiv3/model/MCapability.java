package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MCapability extends MElement
{
	protected List<MBelief> beliefs = new ArrayList<MBelief>();

	protected List<MGoal> goals = new ArrayList<MGoal>();
	
	protected List<MPlan> plans = new ArrayList<MPlan>();
	
	/**
	 *  Create a capability.
	 */
	public MCapability(String name)
	{
		super(name);
	}

	/**
	 *  Get the beliefs.
	 *  @return The beliefs.
	 */
	public List<MBelief> getBeliefs()
	{
		return beliefs;
	}

	/**
	 *  Set the beliefs.
	 *  @param beliefs The beliefs to set.
	 */
	public void setBeliefs(List<MBelief> beliefs)
	{
		this.beliefs = beliefs;
	}

	/**
	 *  Add a belief.
	 */
	public void addBelief(MBelief belief)
	{
		if(beliefs==null)
			beliefs = new ArrayList<MBelief>();
		beliefs.add(belief);
	}
	
	/**
	 *  Test if a belief is contained.
	 */
	public boolean hasBelief(String name)
	{
		boolean ret = false;
		
		if(beliefs!=null && name!=null)
		{
			for(MBelief bel: beliefs)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public List<MGoal> getGoals()
	{
		return goals;
	}

	/**
	 *  Set the goals.
	 *  @param goals The goals to set.
	 */
	public void setGoals(List<MGoal> goals)
	{
		this.goals = goals;
	}
	
	/**
	 *  Add a goal.
	 */
	public void addGoal(MGoal goal)
	{
		if(goals==null)
			goals = new ArrayList<MGoal>();
		goals.add(goal);
	}
	
	/**
	 *  Get the goal for a pojo type.
	 *  @return The goal.
	 */
	public MGoal getGoal(Class<?> pojotype)
	{
		MGoal ret = null;
		if(goals!=null)
		{
			for(MGoal goal: goals)
			{
				if(goal.getTarget().equals(pojotype))
				{
					ret = goal;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public List<MPlan> getPlans()
	{
		return plans;
	}

	/**
	 *  Set the plans.
	 *  @param plans The plans to set.
	 */
	public void setPlans(List<MPlan> plans)
	{
		this.plans = plans;
	}
	
	/**
	 *  Add a plan.
	 */
	public void addPlan(MPlan plan)
	{
		if(plans==null)
			plans = new ArrayList<MPlan>();
		plans.add(plan);
	}
}
