package jadex.bdiv3.examples.garbagecollector;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.commons.SUtil;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;

/**
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@Plans(
{
	@Plan(trigger=@Trigger(goals=GarbageBurnerAgent.Burn.class), body=@Body(BurnPlanEnv.class)),
	@Plan(trigger=@Trigger(goals=GarbageBurnerAgent.Pick.class), body=@Body(PickUpPlanEnv.class))
})
public class GarbageBurnerAgent extends BaseAgent
{
//	/** The position. */
//	@Belief
//	protected IVector2 pos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

	/** 
	 *  The burn waste goal. For every garbage occurring at
	 *  its position a new goal is craeted (see binding).
	 *  The unique tag avoids creating more than one goal
	 *  per specific piece of garbage.
	 */
	@Goal(unique=true, deliberation=@Deliberation(cardinalityone=true))
	public static class Burn
	{
		/** The agent. */
		protected GarbageBurnerAgent outer;
		
		/** The garbage. */
		protected ISpaceObject garbage;
	
		/**
		 *  Create a new burn goal. 
		 */
		public Burn(GarbageBurnerAgent outer, ISpaceObject garbage)
		{
//			System.out.println("create burn goal with: "+garbage);
			this.outer = outer;
			this.garbage = garbage;
		}

		/**
		 * 
		 */
		// todo: support directly factadded etc.
//		@GoalCreationCondition(beliefs="garbages")
		@GoalCreationCondition(rawevents=@RawEvent(value=ChangeEvent.FACTADDED, second="garbages"))
//		public static Burn checkCreate(GarbageBurnerBDI outer, ISpaceObject garbage, IEvent event)
		public static Burn checkCreate(GarbageBurnerAgent outer, ISpaceObject garbage, ChangeEvent event)
		{
//			System.out.println("event: "+event.getType());
			return garbage==null? null: new Burn(outer, garbage);
		}
		
		// hashcode and equals implementation for unique flag
		
		/**
		 *  Get the hashcode.
		 */
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + outer.getClass().hashCode();
			result = prime * result + ((garbage == null) ? 0 : garbage.hashCode());
			return result;
		}

		/**
		 *  Test if equal to other goal.
		 *  @param obj The other object.
		 *  @return True, if equal.
		 */
		public boolean equals(Object obj)
		{
			boolean ret = false;
			if(obj instanceof Burn)
			{
				Burn other = (Burn)obj;
				ret = outer.getClass().equals(other.outer.getClass()) && SUtil.equals(garbage, other.garbage);
			}
			return ret;
		}
	}
	
	/**
	 *  The goal for picking up waste. Tries endlessly to pick up. 
	 */
	@Goal(excludemode=ExcludeMode.Never, retrydelay=100)
	public class Pick
	{
	}
	
}