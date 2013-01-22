package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 */
public class RCapability extends RElement
{
//	/** The beliefs. */
//	protected List<RBelief> beliefs;
	
	/** The goals. */
//	protected List<RGoal> goals;
	protected Set<RGoal> goals;
	
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
	public Collection<RGoal> getGoals()
	{
		return goals;
	}
	
	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public List<RGoal> getGoals(MGoal mgoal)
	{
		if(goals==null)
			return Collections.EMPTY_LIST;
		
		List<RGoal> ret = new ArrayList<RGoal>();
		
		for(RGoal goal: goals)
		{
			if(mgoal.equals(goal.getMGoal()))
			{
				ret.add(goal);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public List<RGoal> getGoals(Class<?> type)
	{
		if(goals==null)
			return Collections.EMPTY_LIST;
		
		List<RGoal> ret = new ArrayList<RGoal>();
		
		for(RGoal goal: goals)
		{
			if(type.equals(goal.getPojoElement().getClass()))
			{
				ret.add(goal);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if a goal is contained.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public boolean containsGoal(Object pojogoal)
	{
		return goals.contains(pojogoal);
	}

	/**
	 *  Set the goals.
	 *  @param goals The goals to set.
	 */
	public void setGoals(Set<RGoal> goals)
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
			goals = new LinkedHashSet<RGoal>();
		}
		
		if(goals.contains(goal))
			throw new RuntimeException("Goal already contained: "+goal);
		
		goals.add(goal);
		
//		if(goal.getPojoElement().getClass().getName().indexOf("AchieveCleanup")!=-1)
//			System.out.println("adopted new goal: "+goal);
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
	
	/**
	 * 
	 */
	public void dumpGoals(IInternalAccess ia)
	{
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(goals!=null)
				{
					System.out.println("--------");
					for(RGoal goal: goals)
					{
						System.out.println(goal+" "+goal.getLifecycleState()+" "+goal.getProcessingState());
						System.out.println(goal.inhibitors);
						System.out.println("--------");
					}
				}
				ia.waitForDelay(500, this);
				return IFuture.DONE;
			}
		};
		ia.getExternalAccess().scheduleStep(step);
	}
}
