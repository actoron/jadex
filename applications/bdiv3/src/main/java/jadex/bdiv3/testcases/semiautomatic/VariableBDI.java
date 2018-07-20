package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.micro.annotation.Agent;

/**
 *  Test if a variable with the same name as the belief
 *  causes a belief change event.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class VariableBDI
{
	@Belief
	protected String str = "hello";
	
	/**
	 * 
	 */
	@Goal
	public class AGoal
	{
		protected String str;

		/**
		 *  Create a new AGoal.
		 */
		// todo: support me
		@GoalCreationCondition//(beliefs="str")
		public AGoal(String str)
		{
			System.out.println("Created goal: "+str);
			this.str = str;
		}
	}
}
