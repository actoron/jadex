package jadex.bdiv3.examples.helloworld;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Imports;
import jadex.rules.eca.annotations.Event;

/**
 *  Hello World with goal driven print out.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Imports({"java.util.logging.*"})
//@Properties({@NameValue(name="logging.level", value="Level.INFO")})
@Description("Hello world agent that uses a belief to trigger a hello goal.")
public class HelloWorldBelief
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
		@GoalParameter
		protected String text;
		
		/**
		 *  Create a new goal whenever sayhello belief is changed.
		 */
		@GoalCreationCondition
		public HelloGoal(@Event("sayhello") String text)
		{
//			System.out.println("New HelloGoal: "+text);
			this.text = text;
		}
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		sayhello = "Hello BDI agent V3.";
//		System.out.println("body end: "+getClass().getName());
	}
	
	/**
	 *  Plan that prints out goal text and passes.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected void printHello(String text, IPlan plan)
	{
		System.out.println(text);
		plan.waitFor(1000).get();
		System.out.println("Good bye.");
		agent.killComponent();
	}
}
