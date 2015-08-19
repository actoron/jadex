package jadex.bdiv3.runtime.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.IDeliberationStrategy;
import jadex.bdiv3x.runtime.RBeliefbase;
import jadex.bdiv3x.runtime.REventbase;
import jadex.bdiv3x.runtime.RExpressionbase;
import jadex.bdiv3x.runtime.RGoalbase;
import jadex.bdiv3x.runtime.RPlanbase;
import jadex.bridge.IInternalAccess;

/**
 *  Runtime element for storing goal and plan instances.
 */
public class RCapability extends RElement
{
	/** Flag to save first exceptions. */
	protected final static boolean DEBUG = false;
	
	/** The map of exceptions. */
	protected Map<RElement, Exception>	ex	= DEBUG? new HashMap<RElement, Exception>(): null;

	//-------- attributes --------
	
	/** The goals. */
	protected Collection<RGoal> goals;
	
	/** The goals by model element. */
	protected Map<MGoal, Collection<RGoal>> mgoals;
	
	/** The goals by goal type (class or string). */
	protected Map<Class<?>, Collection<RGoal>> cgoals;
	
	
	/** The plans. */
	protected Collection<RPlan> plans;
	
	/** The plans by model element. */
	protected Map<MPlan, Collection<RPlan>> mplans;

	
	/** The deliberation strategy. */
	protected IDeliberationStrategy delstr;
	
	//-------- additional xml elements --------
	
	/** The beliefbase. */
	protected RBeliefbase beliefbase;
	
	/** The goalbase. */
	protected RGoalbase goalbase;
	
	/** The expressionbase. */
	protected RExpressionbase expressionbase;
	
	/** The eventbase. */
	protected REventbase eventbase;
	
	/** The planbase. */
	protected RPlanbase planbase;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bdi state.
	 */
	public RCapability(MCapability mcapa, IInternalAccess agent)
	{
		super(mcapa, agent);
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
	 *  Get the rGoal for a pojogoal.
	 */
	public RGoal getRGoal(Object pojogoal)
	{
		RGoal ret = null;
		Collection<RGoal> rgoals = getGoals(pojogoal.getClass());
		if(rgoals!=null)
		{
			for(RGoal rgoal: rgoals)
			{
				if(rgoal.getPojoElement().equals(pojogoal))
				{
					ret = rgoal;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Test if a goal is contained.
	 *  Goal can be either pojogoal or an IGoal.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public boolean containsGoal(Object goal)
	{
		RGoal rgoal = goal instanceof RGoal? (RGoal)goal: getRGoal(goal);
		return goals!=null? goals.contains(rgoal): false;
//		return goals!=null? goals.contains(pojogoal): false;
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
		
//		if(goal.getModelElement().getName().indexOf("help")!=-1)
//			System.out.println("dffded");
		
		if(goals.contains(goal))
			throw new RuntimeException("Goal already contained: "+goal);
	
//		if(goal.getModelElement().getName().indexOf("cleanup")!=-1)
//			System.out.println("adopted goal:"+goal+" "+goal.getParameter("waste").getValue());
		
		goals.add(goal);

		Collection<RGoal>	mymgoals	= mgoals.get(goal.getModelElement());
		if(mymgoals==null)
		{
			mymgoals	= new LinkedHashSet<RGoal>();
			mgoals.put((MGoal)goal.getModelElement(), mymgoals);
		}
		mymgoals.add(goal);
		
		// for pojo goal also add to class map
		if(goal.getPojoElement()!=null)
		{
			Collection<RGoal> mycgoals = cgoals.get(goal.getPojoElement().getClass());
			if(mycgoals==null)
			{
				mycgoals	= new LinkedHashSet<RGoal>();
				cgoals.put(goal.getPojoElement().getClass(), mycgoals);
			}
			mycgoals.add(goal);
		}

//		if(goal.getPojoElement().getClass().getName().indexOf("AchieveCleanup")!=-1)
//			System.out.println("adopted new goal: "+goal);
	}
	
	
	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(RGoal goal)
	{
		if(DEBUG && !ex.containsKey(goal))
		{
			Exception e	= new RuntimeException("first");
			e.fillInStackTrace();
			ex.put(goal, e);
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
			if(DEBUG && ex.containsKey(goal))
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
			
			// for pojo goal also remove from class map
			if(goal.getPojoElement()!=null)
			{
				Collection<RGoal>	mycgoals	= cgoals.get(goal.getPojoElement().getClass());
				mycgoals.remove(goal);
				if(mycgoals.isEmpty())
				{
					cgoals.remove(goal.getPojoElement().getClass());
				}
			}
		}
	}
	
	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public Collection<RPlan> getPlans()
	{
		return plans!=null? plans: Collections.EMPTY_SET;
	}

	/**
	 *  Set the plans.
	 *  @param plans The plans to set.
	 */
	public void setPlans(Collection<RPlan> plans)
	{
		this.plans = null;
		for(RPlan p: plans)
		{
			addPlan(p);
		}
	}
	
	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public Collection<RPlan> getPlans(MPlan mplan)
	{
		Collection<RPlan>	ret	= mplans!=null ? mplans.get(mplan) : null;
		if(ret==null)
		{
			ret	= Collections.emptySet();
		}
		return ret;
	}
	
	/**
	 *  Add a new plan.
	 *  @param plan The plan.
	 */
	public void addPlan(RPlan plan)
	{
//		System.out.println("add plan: "+plan);
		if(plans==null)
		{
			plans = new HashSet<RPlan>();
			mplans = new HashMap<MPlan, Collection<RPlan>>();
		}
		if(plans.contains(plan))
			throw new RuntimeException("Plan already contained: "+plan);
		
		Collection<RPlan>	mymplans	= mplans.get(plan.getModelElement());
		if(mymplans==null)
		{
			mymplans	= new LinkedHashSet<RPlan>();
			mplans.put((MPlan)plan.getModelElement(), mymplans);
		}
		mymplans.add(plan);
		
		plans.add(plan);
	}
	
	/**
	 *  Remove a plan.
	 *  @param plan The plan.
	 */
	public void removePlan(RPlan plan)
	{
		if(DEBUG && !ex.containsKey(plan))
		{
			Exception e = new RuntimeException("first");
			e.fillInStackTrace();
			ex.put(plan, e);
		}
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
			if(DEBUG && ex.containsKey(plan))
			{
				ex.get(plan).printStackTrace();
			}
			throw new IllegalStateException("Plan not contained: "+plan);			
		}
		
		Collection<RPlan>	mymplans	= mplans.get(plan.getModelElement());
		mymplans.remove(plan);
		if(mymplans.isEmpty())
		{
			mplans.remove((MPlan)plan.getModelElement());
		}
	}
	
	/**
	 *  Get the beliefbase.
	 *  @return The beliefbase
	 */
	public RBeliefbase getBeliefbase()
	{
		return beliefbase;
	}
	
	/**
	 *  The beliefbase to set.
	 *  @param beliefbase The beliefbase to set
	 */
	public void setBeliefbase(RBeliefbase beliefbase)
	{
		this.beliefbase = beliefbase;
	}
	
	/**
	 *  Get the expressionbase.
	 *  @return The expressionbase
	 */
	public RExpressionbase getExpressionbase()
	{
		return expressionbase;
	}

	/**
	 *  The expressionbase to set.
	 *  @param expressionbase The expressionbase to set
	 */
	public void setExpressionbase(RExpressionbase expressionbase)
	{
		this.expressionbase = expressionbase;
	}

	/**
	 *  Get the eventbase.
	 *  @return The eventbase
	 */
	public REventbase getEventbase()
	{
		if(eventbase==null)
		{
			eventbase	= new REventbase(getAgent(), null);
		}
		return eventbase;
	}

//	/**
//	 *  The eventbase to set.
//	 *  @param eventbase The eventbase to set
//	 */
//	public void setEventbase(REventbase eventbase)
//	{
//		this.eventbase = eventbase;
//	}

	/**
	 *  Get the goalbase.
	 *  @return The goalbase
	 */
	public RGoalbase getGoalbase()
	{
		if(goalbase==null)
		{
			goalbase	= new RGoalbase(getAgent(), null);
		}
		return goalbase;
	}

	/**
	 *  Get the planbase. 
	 *  @return The planbase
	 */
	public RPlanbase getPlanbase()
	{
		return planbase;
	}

	/**
	 *  Set the planbase.
	 *  @param planbase The planbase to set
	 */
	public void setPlanbase(RPlanbase planbase)
	{
		this.planbase = planbase;
	}

	/**
	 *  Get the deliberationStrategy.
	 *  @return The deliberationStrategy
	 */
	public IDeliberationStrategy getDeliberationStrategy()
	{
		return delstr;
	}

	/**
	 *  The deliberationStrategy to set.
	 *  @param delstr The deliberationStrategy to set
	 */
	public void setDeliberationStrategy(IDeliberationStrategy delstr)
	{
		this.delstr = delstr;
	}

	/**
	 * 
	 */
	protected void dumpGoalsPeriodically(IInternalAccess ia)
	{
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				dumpGoals();
			}
		}, 3000, 3000);
		
//		IComponentStep<Void> step = new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				dumpGoals();
//				ia.waitForDelay(500, this);
//				return IFuture.DONE;
//			}
//		};
//		ia.getExternalAccess().scheduleStep(step);
	}
	
	/**
	 * 
	 */
	protected void dumpPlansPeriodically(IInternalAccess ia)
	{
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				dumpPlans();
			}
		}, 5000, 5000);
		
//		IComponentStep<Void> step = new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				dumpPlans();
//				ia.waitForDelay(500, this);
//				return IFuture.DONE;
//			}
//		};
//		ia.getExternalAccess().scheduleStep(step);
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
				System.out.println("goal: "+goal+" "+goal.getLifecycleState()+" "+goal.getProcessingState()+" "+goal.getParent());
//				if(goal.getInhibitors()!=null)
//				{
//					for(RGoal g: goal.getInhibitors())
//					{
//						System.out.print(g+" "+g.getLifecycleState()+" "+g.getProcessingState());
//					}
//					System.out.println();
//				}
//				System.out.println("inhibitors: "+goal.getInhibitors());
				System.out.println("--------");
			}
		}
	}
	
	/**
	 * 
	 */
	protected void dumpPlans()
	{
		if(plans!=null)
		{
			System.out.println("plans: "+plans.size());
			System.out.println("--------");
			for(RPlan plan: plans)
			{
				StringBuffer buf = new StringBuffer();
				determineValid(plan, plan, buf);
				if(plan.isFinishing())
					System.out.println("aborted zombie plan: "+plan.getId());
				
				System.out.println(buf.toString());
				System.out.println(plan.getId()+" "+plan.getLifecycleState()+" "+plan.getProcessingState());
				System.out.println("--------");
			}
		}
	}
	
	/**
	 * 
	 * @param plan
	 * @param orig
	 * @param buf
	 */
	protected void determineValid(RPlan plan, RPlan orig, StringBuffer buf)
	{
		buf.append(plan.getId()+plan.isFinishing());
		Object reason = plan.getReason();
		if(reason instanceof RGoal)
		{
			RGoal rg = (RGoal)reason;
			if(rg.isFinished())
				System.out.println("fini goal"+" "+reason+" "+orig);
			RPlan pp = rg.getParentPlan();
			buf.append(" reason is: "+((RProcessableElement)reason).getId()+rg.lifecyclestate);
			if(pp!=null)
			{
				if(pp.isFinished())
					System.out.println("fini plan"+" "+pp+" "+orig);
				buf.append(" parent plan is: ");
				determineValid(pp, orig, buf);
			}
		}
	}
	
	/**
	 *  Get the capability part of a complex element name.
	 */
	public static String getCapabilityPart(String name)
	{
		String ret = null;
		int	idx = name.lastIndexOf(MElement.CAPABILITY_SEPARATOR);
		if(idx!=-1)
		{
			ret = name.substring(0, idx);
		}
		return ret;
	}
	
	/**
	 *  Get the name part of a complex element name.
	 */
	public static String getNamePart(String name)
	{
		String ret = name;
		int	idx = name.lastIndexOf("$");
		if(idx==-1)
		{
			idx = name.lastIndexOf(".");
		}
		if(idx==-1)
		{	
			idx = name.lastIndexOf(MElement.CAPABILITY_SEPARATOR);
		}
		if(idx!=-1)
		{	
			ret = name.substring(idx+1);
		}
		return ret;
	}
	
	/**
	 *  Get beautified element name.
	 */
	public static String getBeautifiedName(String name)
	{
		String capa = getCapabilityPart(name);
		String pname = getNamePart(name);
		return capa!=null? capa.replace(MElement.CAPABILITY_SEPARATOR, ".")+"."+pname: pname;
	}
	
	//-------- extra methods for simple access from bdi and bdix kernels --------
	
//	/**
//	 *  Drop a pojo goal.
//	 *  @param goal The pojo goal.
//	 */
//	public void dropGoal(Object goal)
//	{
//		if(goal instanceof IGoal)
//		{
//			((IGoal)goal).drop();
//		}
//		else
//		{
//			for(RGoal rgoal: getCapability().getGoals(goal.getClass()))
//			{
//				if(goal.equals(rgoal.getPojoElement()))
//				{
//					rgoal.drop();
//					break;
//				}
//			}
//		}
//	}
}
