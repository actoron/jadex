package jadex.bdiv3.examples.helloworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.rules.eca.annotations.Event;

/**
 *  Hello World with goal driven print out.
 *  
 *  class is checked for annotations
 *  goal, plan type declarations from annotations or inline plans 
 *  are added to the agent type and conditions to eca rule system 
 *  class is rewritten to announce belief changes (field accesses and annotated methods)
 */
@Agent
@Imports({"java.util.logging.*"})
@Properties({@NameValue(name="logging.level", value="Level.INFO")})
public class HelloWorld2BDI
{
	/** The bdi agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The text that is printed. */
	@Belief
	private String sayhello = "wurst";
	
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
	
	/**
	 *  The agent body.
	 *  
	 *  body is executed
	 *  changes variable value (sayhello=true)
	 *  notification is sent to eca rule system
 	 *  rule system finds creation condition of goal and executes it
	 *  right hand side creates goal and executes it
	 *  Plan is selected and executed (hello is printed out)
	 */
	@AgentBody
	public void body()
	{
		sayhello = "Hello BDI agent V3.";
		System.out.println("body end: "+getClass().getName());
	}
	
	/**
	 *  Hello world plan.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	public class HelloWorldPlan
	{
		@PlanBody
		protected IFuture<Void> printHello1(HelloGoal goal)
		{
			System.out.println("1: "+goal.getText());
//			throw new RuntimeException();
			return IFuture.DONE;
		}
		
		@PlanPassed
		protected IFuture<Void> passed(HelloGoal goal)
		{
			System.out.println("Passed: "+goal.getText());
			return IFuture.DONE;
		}
		
		@PlanFailed
		protected IFuture<Void> failed(HelloGoal goal)
		{
			System.out.println("Failed: "+goal.getText());
			return IFuture.DONE;
		}
	}
}

