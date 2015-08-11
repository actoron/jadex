package jadex.bdiv3.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.IGoal.GoalLifecycleState;
import jadex.bdiv3.runtime.IGoal.GoalProcessingState;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.rules.eca.RuleSystem;

/**
 *  The easy deliberation strategy.
 */
public class EasyDeliberationStrategy implements IDeliberationStrategy
{
	/** The agent. */
	protected IInternalAccess agent;

	/** The set of inhibitors. */
	protected Map<RGoal, Set<RGoal>> inhibitions;
	
	/**
	 *  Init the strategy.
	 *  @param agent The agent.
	 */
	public void init(IInternalAccess agent)
	{
		this.agent = agent;
		this.inhibitions = new HashMap<RGoal, Set<RGoal>>();
	}
	
	/**
	 *  Called when a goal has been adopted.
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsAdopted(RGoal goal)
	{
		for(RGoal other: getCapability().getGoals())
		{
//			if(other.getLifecycleState().equals(RGoal.GOALLIFECYCLESTATE_ACTIVE) 
//				&& other.getProcessingState().equals(RGoal.GOALPROCESSINGSTATE_INPROCESS)
			if(!isInhibitedBy(other, goal) && inhibits(other, goal))
			{
//				if(goal.getModelElement().getName().indexOf("achievecleanup")!=-1)
//					System.out.println("inhibit");
				addInhibitor(goal, other);
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Called when a goal becomes an option.
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsOption(RGoal goal)
	{
		if(!isInhibited(goal))
			reactivateGoal(goal);
		return IFuture.DONE;
	}
	
	/**
	 *  Called when a goal becomes active.
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsActive(RGoal goal)
	{
//		if(goal.getId().indexOf("PerformPatrol")!=-1)
//			System.out.println("addinh: "+goal);
		MDeliberation delib = goal.getMGoal().getDeliberation();
		if(delib!=null)
		{
			Set<MGoal> inhs = delib.getInhibitions(getCapability().getMCapability());
			if(inhs!=null)
			{
				for(MGoal inh: inhs)
				{
					Collection<RGoal> goals = getCapability().getGoals(inh);
					for(RGoal other: goals)
					{
//						if(!other.isInhibitedBy(goal) && goal.inhibits(other, getInternalAccess()))
						if(!isInhibitedBy(goal, other) && inhibits(goal, other))
						{
//							if(other.getModelElement().getName().indexOf("achievecleanup")!=-1)
//								System.out.println("inh achieve");
							addInhibitor(other, goal);
						}
					}
				}
			}
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Called when a goal is not active any longer (suspended or option).
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsNotActive(RGoal goal)
	{
		// Remove inhibitions of this goal 
		MDeliberation delib = goal.getMGoal().getDeliberation();
		if(delib!=null)
		{
			Set<MGoal> inhs = delib.getInhibitions(getCapability().getMCapability());
			if(inhs!=null)
			{
				for(MGoal inh: inhs)
				{
//					if(goal.getId().indexOf("AchieveCleanup")!=-1)
//						System.out.println("reminh: "+goal);
					Collection<RGoal> goals = getCapability().getGoals(inh);
					for(RGoal other: goals)
					{
						if(goal.equals(other))
							continue;
						
						if(isInhibitedBy(other, goal))
							removeInhibitor(other, goal);
					}
				}
			}
			
			// Remove inhibitor from goals of same type if cardinality is used
			if(delib.isCardinalityOne())
			{
				Collection<RGoal> goals = getCapability().getGoals(goal.getMGoal());
				if(goals!=null)
				{
					for(RGoal other: goals)
					{
						if(goal.equals(other))
							continue;
						
						if(isInhibitedBy(other, goal))
							removeInhibitor(other, goal);
					}
				}
			}
		}
	
		return IFuture.DONE;
	}
	
	/**
	 *  Add an inhibitor to a goal.
	 */
	public void addInhibitor(RGoal goal, RGoal inhibitor)
	{		
		Set<RGoal> inhibitors = getInhibitions(goal, true);

		if(inhibitors.add(inhibitor) && inhibitors.size()==1)
		{
			inhibitGoal(goal);
//			getRuleSystem().addEvent(new Event(new EventType(new String[]{ChangeEvent.GOALINHIBITED, goal.getMGoal().getName()}), this));
		}
		
//		if(inhibitor.getId().indexOf("AchieveCleanup")!=-1)
//			System.out.println("add inhibit: "+getId()+" "+inhibitor.getId()+" "+inhibitors);
	}
	
	/**
	 *  Inhibit a goal by making it an option.
	 */
	protected void inhibitGoal(RGoal goal)
	{
		if(IGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState()))
			goal.setLifecycleState(agent, RGoal.GoalLifecycleState.OPTION);
	}
	
	/**
	 *  Remove an inhibitor from a goal.
	 */
	protected void removeInhibitor(RGoal goal, RGoal inhibitor)
	{
//		System.out.println("rem inhibit: "+getId()+" "+inhibitor.getId()+" "+inhibitors);
		
//		if(inhibitor.getId().indexOf("AchieveCleanup")!=-1)
//			System.out.println("kokoko: "+inhibitor);
		
		Set<RGoal> inhibitors = getInhibitions(goal, false);
		
		if(inhibitors!=null)
		{
			if(inhibitors.remove(inhibitor) && inhibitors.size()==0)
			{
//				System.out.println("goal not inhibited: "+this);
//				BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
				reactivateGoal(goal);
//				getRuleSystem().addEvent(new Event(new EventType(new String[]{ChangeEvent.GOALNOTINHIBITED, goal.getMGoal().getName()}), this));
			}
		}
	}
	
	/**
	 *  (Re)activate a goal.
	 */
	protected void reactivateGoal(RGoal goal)
	{
		goal.setLifecycleState(agent, RGoal.GoalLifecycleState.ACTIVE);
	}
	
	/**
	 *  Test if goal is inhibited.
	 */
	protected boolean isInhibited(RGoal goal)
	{
		Set<RGoal> inhibitors = getInhibitions(goal, false);
		return inhibitors!=null && !inhibitors.isEmpty();
	}
	
	/**
	 * Test if goal is inhibited by another goal.
	 */
	protected boolean isInhibitedBy(RGoal goal, RGoal other)
	{
		Set<RGoal> inhibitors = getInhibitions(goal, false);
		return !goal.isFinished() && inhibitors!=null && inhibitors.contains(other);
	}
	
	/**
	 *  Test if this goal inhibits the other.
	 */
	protected boolean inhibits(RGoal goal, RGoal other)
	{
		if(goal.equals(other))
			return false;
		
		// todo: cardinality
		
		boolean ret = false;
		
		if(goal.getLifecycleState().equals(GoalLifecycleState.ACTIVE) && goal.getProcessingState().equals(GoalProcessingState.INPROCESS))
		{
			MDeliberation delib = goal.getMGoal().getDeliberation();
			if(delib!=null)
			{
				if(delib.isCardinalityOne() && other.getMGoal().equals(goal.getMGoal()))
				{
					ret = true;
				}
				else
				{
//					Set<MGoal> minh = delib.getInhibitions();
					Set<MGoal> minh = delib.getInhibitions(goal.getMCapability());
					MGoal mother = other.getMGoal();
					if(minh!=null && minh.contains(mother))
					{
						ret = true;
						
						// check if instance relation
						Map<String, MethodInfo> dms = delib.getInhibitionMethods();
						if(dms!=null)
						{
							MethodInfo mi = dms.get(mother.getName());
							if(mi!=null)
							{
								Method dm = mi.getMethod(agent.getClassLoader());
								try
								{
									dm.setAccessible(true);
									ret = ((Boolean)dm.invoke(goal.getPojoElement(), new Object[]{other.getPojoElement()})).booleanValue();
								}
								catch(Exception e)
								{
									Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
									agent.getLogger().severe("Exception in inhibits expression: "+t);
								}
							}
						}
						
						// xml inhibition expressions
						Map<String, UnparsedExpression> uexps = delib.getInhibitionExpressions();
						if(uexps!=null)
						{
							UnparsedExpression uexp = uexps.get(mother.getName());
							if(uexp!=null && uexp.getValue()!=null && uexp.getValue().length()>0)
							{
								SimpleValueFetcher fet = new SimpleValueFetcher(CapabilityWrapper.getFetcher(agent, uexp.getLanguage()));
								fet.setValue("$goal", goal);
								fet.setValue("$ref", other);
								
								try
								{
									ret = ((Boolean)SJavaParser.parseExpression(uexp, agent.getModel().getAllImports(), agent.getClassLoader()).getValue(fet)).booleanValue();
								}
								catch(Exception e)
								{
									Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
									agent.getLogger().severe("Exception in inhibits expression: "+t);
								}
							}
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the inhibitors.
	 *  @return The inhibitors.
	 */
	protected Set<RGoal> getInhibitors(RGoal goal)
	{
		return getInhibitions(goal, false);
	}
	
	/**
	 *  Get the rule system.
	 */
	protected RuleSystem getRuleSystem()
	{
		return agent.getComponentFeature(IInternalBDIAgentFeature.class).getRuleSystem();
	}
	
	/**
	 *  Get the capability.
	 */
	protected RCapability getCapability()
	{
		return agent.getComponentFeature(IInternalBDIAgentFeature.class).getCapability();
	}
	
	/**
	 *  Get or create the inhibition set.
	 */
	protected Set<RGoal> getInhibitions(RGoal goal, boolean create)
	{
		Set<RGoal> inhibitors = inhibitions.get(goal);
		if(inhibitors==null)
		{
			inhibitors = new HashSet<RGoal>();
			inhibitions.put(goal, inhibitors);
		}
		return inhibitors;
	}
}
