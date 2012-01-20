package jadex.bdiv3;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.micro.MicroModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class BDIModel extends MicroModel
{
	List<Field> beliefs = new ArrayList<Field>();

	List<Class> goals = new ArrayList<Class>();
	
	List<Method> plans = new ArrayList<Method>();
	
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
	public List<Field> getBeliefs()
	{
		return beliefs;
	}

	/**
	 *  Set the beliefs.
	 *  @param beliefs The beliefs to set.
	 */
	public void setBeliefs(List<Field> beliefs)
	{
		this.beliefs = beliefs;
	}

	/**
	 *  Add a belief.
	 */
	public void addBelief(Field belief)
	{
		if(beliefs==null)
			beliefs = new ArrayList<Field>();
		beliefs.add(belief);
	}
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public List<Class> getGoals()
	{
		return goals;
	}

	/**
	 *  Set the goals.
	 *  @param goals The goals to set.
	 */
	public void setGoals(List<Class> goals)
	{
		this.goals = goals;
	}
	
	/**
	 *  Add a goal.
	 */
	public void addGoal(Class goal)
	{
		if(goals==null)
			goals = new ArrayList<Class>();
		goals.add(goal);
	}

	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public List<Method> getPlans()
	{
		return plans;
	}

	/**
	 *  Set the plans.
	 *  @param plans The plans to set.
	 */
	public void setPlans(List<Method> plans)
	{
		this.plans = plans;
	}
	
	/**
	 *  Add a plan.
	 */
	public void addPlan(Method plan)
	{
		if(plans==null)
			plans = new ArrayList<Method>();
		plans.add(plan);
	}
}
