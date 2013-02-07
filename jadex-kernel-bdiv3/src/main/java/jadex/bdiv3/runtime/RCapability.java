package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 *  Runtime element for storing goal ans plan instances.
 */
public class RCapability extends RElement
{
	//-------- attributes --------
	
	/** The goals. */
	protected Collection<RGoal> goals;
	
	/** The goals by model element. */
	protected Map<MGoal, Collection<RGoal>> mgoals;
	
	/** The goals by goal class. */
	protected Map<Class<?>, Collection<RGoal>> cgoals;
	
	/** The plans. */
	protected List<RPlan> plans;

	//-------- constructors --------
	
	/**
	 *  Create a new bdi state.
	 */
	public RCapability(MCapability mcapa)
	{
		super(mcapa);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public Collection<RGoal> getGoals()
	{
		Collection<RGoal>	ret	= goals;
		if(ret==null)
		{
			ret	= Collections.emptySet();
		}
		return ret;
	}
	
	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public Collection<RGoal> getGoals(MGoal mgoal)
	{
		Collection<RGoal>	ret	= mgoals!=null ? mgoals.get(mgoal) : null;
		if(ret==null)
		{
			ret	= Collections.emptySet();
		}
		return ret;
	}
	
	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public Collection<RGoal> getGoals(Class<?> type)
	{
		Collection<RGoal>	ret	= cgoals!=null ? cgoals.get(type) : null;
		if(ret==null)
		{
			ret	= Collections.emptySet();
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
		return goals!=null? goals.contains(pojogoal): false;
	}

	/**
	 *  Set the goals.
	 *  @param goals The goals to set.
	 */
	public void setGoals(Collection<RGoal> goals)
	{
		this.goals = null;
		for(RGoal g: goals)
		{
			addGoal(g);
		}
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
			mgoals	= new HashMap<MGoal, Collection<RGoal>>();
			cgoals	= new HashMap<Class<?>, Collection<RGoal>>();
		}
		
		if(goals.contains(goal))
			throw new RuntimeException("Goal already contained: "+goal);
		
		goals.add(goal);

		Collection<RGoal>	mymgoals	= mgoals.get(goal.getModelElement());
		if(mymgoals==null)
		{
			mymgoals	= new LinkedHashSet<RGoal>();
			mgoals.put((MGoal)goal.getModelElement(), mymgoals);
		}
		mymgoals.add(goal);
		
		Collection<RGoal>	mycgoals	= cgoals.get(goal.getPojoElement().getClass());
		if(mycgoals==null)
		{
			mycgoals	= new LinkedHashSet<RGoal>();
			cgoals.put(goal.getPojoElement().getClass(), mycgoals);
		}
		mycgoals.add(goal);

//		if(goal.getPojoElement().getClass().getName().indexOf("AchieveCleanup")!=-1)
//			System.out.println("adopted new goal: "+goal);
	}
	
	protected Map<RGoal, Exception>	ex	= new HashMap<RGoal, Exception>();
	
	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(RGoal goal)
	{
		if(!ex.containsKey(goal))
		{
			Exception ex	= new RuntimeException("first");
			ex.fillInStackTrace();
			this.ex.put(goal, ex);
		}
		if(goal==null)
		{
			throw new IllegalArgumentException("Goal is null.");
		}
		else if(goals==null)
		{
			throw new IllegalStateException("Goals are null.");
		}
		else if(!goals.remove(goal))
		{
			if(ex.containsKey(goal))
			{
				ex.get(goal).printStackTrace();
			}
			throw new IllegalStateException("Goal not contained: "+goal.getId());			
		}
		else
		{
			Collection<RGoal>	mymgoals	= mgoals.get(goal.getModelElement());
			mymgoals.remove(goal);
			if(mymgoals.isEmpty())
			{
				mgoals.remove((MGoal)goal.getModelElement());
			}
			
			Collection<RGoal>	mycgoals	= cgoals.get(goal.getPojoElement().getClass());
			mycgoals.remove(goal);
			if(mycgoals.isEmpty())
			{
				cgoals.remove(goal.getPojoElement().getClass());
			}			
		}
	}
	
	/**
	 *  Add a new plan.
	 *  @param plan The plan.
	 */
	public void addPlan(RPlan plan)
	{
		if(plans==null)
		{
			plans = new ArrayList<RPlan>();
		}
		if(plans.contains(plan))
			throw new RuntimeException("Plan already contained: "+plan);
		plans.add(plan);
	}
	
	/**
	 *  Remove a plan.
	 *  @param plan The plan.
	 */
	public void removePlan(RPlan plan)
	{
		if(plan==null)
		{
			throw new IllegalArgumentException("Plan is null.");
		}
		else if(plans==null)
		{
			throw new IllegalStateException("Plans are null.");
		}
		else if(!plans.remove(plan))
		{
			throw new IllegalStateException("Plan not contained.");			
		}
	}
	
	/**
	 * 
	 */
	protected void dumpGoalsPeriodically(IInternalAccess ia)
	{
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				dumpGoals();
				ia.waitForDelay(500, this);
				return IFuture.DONE;
			}
		};
		ia.getExternalAccess().scheduleStep(step);
	}
	
	/**
	 * 
	 */
	protected void dumpGoals()
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
	}
}
