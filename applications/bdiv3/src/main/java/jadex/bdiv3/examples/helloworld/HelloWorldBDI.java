package jadex.bdiv3.examples.helloworld;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgentFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Properties;

/**
 *  Hello World with goal driven print out.
 *  
 *  class is checked for annotations
 *  goal, plan type declarations from annotations or inline plans 
 *  are added to the agent type and conditions to eca rule system 
 *  class is rewritten to announce belief changes (field accesses and annotated methods)
 */
@Agent(type=BDIAgentFactory.TYPE)
@Imports({"java.util.logging.*"})
@Properties({@NameValue(name="logging.level", value="Level.INFO")})
public class HelloWorldBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
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
		@GoalCreationCondition(beliefs="sayhello")
//		public HelloGoal(@Event("sayhello") String text) 
		public HelloGoal(String text) 
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
	 *  First plan. Fails with exception.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello1(HelloGoal goal)
	{
		System.out.println("1: "+goal.getText());
		throw new PlanFailureException();
	}
	
	/**
	 *  Second plan. Fails 'asynchronously' (i.e. exception in future).
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello2(HelloGoal goal)
	{
		System.out.println("2: "+goal.getText());
		return new Future<Void>(new PlanFailureException());
	}
	
	/**
	 *  Third plan. Prints out goal text and passes.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello3(HelloGoal goal)
	{
		System.out.println("3: "+goal.getText());
		return IFuture.DONE;
	}
}
