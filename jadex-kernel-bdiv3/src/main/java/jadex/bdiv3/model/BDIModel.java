package jadex.bdiv3.model;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.micro.MicroModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class BDIModel extends MicroModel
{
	protected List<MBelief> beliefs = new ArrayList<MBelief>();

	protected List<MGoal> goals = new ArrayList<MGoal>();
	
	protected List<MPlan> plans = new ArrayList<MPlan>();
	
	/**
	 *  Create a new model.
	 */
	public BDIModel(IModelInfo modelinfo)
	{
		super(modelinfo);
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
