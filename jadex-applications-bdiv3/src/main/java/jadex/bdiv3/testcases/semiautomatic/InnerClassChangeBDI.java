package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalAPI;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IGoal;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
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
public class InnerClassChangeBDI
{
	/** The bdi agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The text that is printed. */
	@Belief
	private String sayhello = "first";
	
	/**
	 *  Simple hello world goal.
	 */
	@Goal
	public class HelloGoal
	{
		@GoalAPI
		protected IGoal goal;
		
		/** The text. */
		protected String text;
		
		/**
		 *  Create a new goal whenever sayhello belief is changed.
		 */
		@GoalCreationCondition
		public HelloGoal(@Event("sayhello") String text)
		{
			this.text = text;
			
			if("first".equals(sayhello))
				sayhello = "second";
		}
		
		/**
		 *  Get the text.
		 *  @return the text.
		 */
		public String getText()
		{
			return text;
		}
		
		/**
		 * 
		 */
		public IGoal getGoal()
		{
			return goal;
		}
	}
	
	/**
	 *  First plan. Fails with exception.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello1(HelloGoal goal)
	{
		System.out.println(goal.getText());
		return IFuture.DONE;
	}
}

