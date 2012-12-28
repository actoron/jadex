package jadex.bdiv3.example.helloworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.PlanFailureException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.rules.eca.annotations.Event;

@Agent
//@Goals(HelloGoal.class) 

// class is checked for annotations
// goal, plan type declarations from annotations or inline plans 
// are added to the agent type and conditions to eca rule system 
// class is rewritten to announce belief changes (field accesses and annotated methods)

// body is executed
// changes variable value (sayhello=true)
// notification is sent to eca rule system
// rule system finds creation condition of goal and executes it
// right hand side creates goal and executes it
// Plan is selected and executed (hello is printed out)
public class HelloWorldBDI
{
	/** The bdi agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The text that is printed. */
	@Belief
	private String sayhello;
	
	/**
	 *  Simple hello world goal.
	 */
	@Goal
	public class HelloGoal
	{
		/** The text. */
		protected String text;
		
		/**
		 *  Create a new goal whenever sayhello belief is changed.
		 */
		@GoalCreationCondition
		public HelloGoal(@Event("sayhello") String text)
		{
			this.text = text;
		}
		
		/**
		 *  Get the text.
		 *  @return the text.
		 */
		public String getText()
		{
			return text;
		}
	}
	
	@AgentBody
	public void body()
	{
		sayhello = "Hello BDI agent V3.";
		System.out.println("body end: "+getClass().getName());
	}
	
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello1(HelloGoal goal)
	{
		System.out.println("1: "+goal.getText());
		return new Future<Void>(new PlanFailureException());
	}
	
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello2(HelloGoal goal)
	{
		System.out.println("2: "+goal.getText());
		return IFuture.DONE;
	}
}
