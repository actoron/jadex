package jadex.bdiv3.testcases.misc;

import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ICapability;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  Capability with goal and plan.
 */
@Capability
public class TestCapability
{
	/** The capability. */
	@Agent
	protected ICapability capa;
	
	@Goal
	public class TestGoal
	{
		protected int cnt;

		/**
		 *  Get the cnt.
		 *  @return The cnt.
		 */
		public int getCnt()
		{
			return cnt;
		}

		/**
		 *  Set the cnt.
		 *  @param cnt The cnt to set.
		 */
		public void setCnt(int cnt)
		{
			this.cnt = cnt;
		}
	}

	/**
	 *  Plan reacting on test goal.
	 */
	@Plan(trigger=@Trigger(goals=TestGoal.class))
	public IFuture<Void> testPlan(TestGoal goal)
	{
		goal.setCnt(goal.getCnt()+1);
		System.out.println("Capa plan: "+this);
		return new Future<Void>(new RuntimeException());
	}
}
