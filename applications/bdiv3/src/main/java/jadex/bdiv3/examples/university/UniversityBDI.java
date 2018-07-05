package jadex.bdiv3.examples.university;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IInternalAccess;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

/**
 *  Go to university example taken from  
 *  Winikoff, Padgham: developing intelligent agent systems, 2004.
 */
//@BDIConfigurations({
//	@BDIConfiguration(name="sunny", initialbeliefs=@NameValue(name="raining", value="false")),
//	@BDIConfiguration(name="rainy", initialbeliefs=@NameValue(name="raining", value="true"))
//})
@Configurations({@Configuration(name="sunny"), @Configuration(name="rainy")})
@Agent(keepalive=Boolean3.FALSE)
public class UniversityBDI
{
	// Annotation to inform FindBugs that the uninitialized field is not a bug.
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")
	
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** Belief if it is currently raining. Set through an agent argument. */
	@Belief
	protected boolean raining = agent.getConfiguration().equals("rainy");
	
	/** The top-level goal to come to the university. */
	@Goal
	protected class ComeToUniGoal
	{
	}
	
	/** The take x goal is for using a train or tram. */
	@Goal
	protected static class TakeXGoal
	{
		public enum Type{TRAIN, TRAM};
		
		protected Type type;
		
		public TakeXGoal(Type type)
		{
			this.type = type;
		}

		public Type getType()
		{
			return type;
		}
	}
	
	/** 
	 *  The agent body is executed on startup.
	 *  It creates and dispatches a come to university goal.
	 */
	@AgentBody
	public void body()
	{
		System.out.println("rainy: "+raining);
//		if(agent.getConfiguration().equals("rainy"))
//			raining = true;
		try
		{
			agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new ComeToUniGoal()).get();
		}
		catch(Exception e)
		{
			System.out.println("stayed at home");
		}
	}
	
	/**
	 *  The walk plan for the come to university goal.
	 *  Walk only if its not raining and not as first choice
	 */
	@Plan(trigger=@Trigger(goals=ComeToUniGoal.class), priority=-1)
	protected class WalkPlan
	{
		@PlanPrecondition
		protected boolean checkWeather()
		{
			return !raining;
		}
		
		@PlanBody
		protected void walk()
		{
			System.out.println("Walked to Uni.");
		}
	}
	
	/**
	 *  The train plan for the come to university goal.
	 *  Only take train when its raining (too expensive)
	 */
	@Plan(trigger=@Trigger(goals=ComeToUniGoal.class))
	protected class TrainPlan
	{
		@PlanPrecondition
		protected boolean checkWeather()
		{
			return raining;
		}
		
		@PlanBody
		protected void takeTrain(IPlan plan)
		{
			System.out.println("Trying to take train to Uni.");
			plan.dispatchSubgoal(new TakeXGoal(TakeXGoal.Type.TRAIN)).get();
			System.out.println("Took train to Uni.");
		}
	}

	/**
	 *  The tram plan for come to university goal.
	 *  Tram is always a good idea.
	 */
	@Plan(trigger=@Trigger(goals=ComeToUniGoal.class))
	protected void tramPlan(IPlan plan)
	{
		System.out.println("Trying to take tram to Uni.");
		plan.dispatchSubgoal(new TakeXGoal(TakeXGoal.Type.TRAM)).get();
		System.out.println("Took tram to Uni.");
	}
	
	/**
	 *  Take X plan for the take X goal.
	 */
	@Plan(trigger=@Trigger(goals=TakeXGoal.class))
	protected void takeX(TakeXGoal goal)
	{
		System.out.println("Walking to station.");
		System.out.println("Checking time table.");
		if(Math.random()>0.5)
		{
			System.out.println("Wait time is too long, failed.");
			throw new PlanFailureException();
		}
		else
		{
			System.out.println("Taking "+goal.getType());
		}
	}
}
