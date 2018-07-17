package jadex.bdiv3.examples.garbagecollector;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Garbage collector agent.</H3>
 *  Runs a predefined way on the grid and searches for
 *  garbage. Whenever it sees garbage at its actual position
 *  it tries to pick it up and brings it to one of the available
 *  garbage burners (chosen randomly).
 */
@Agent(type=BDIAgentFactory.TYPE)
@Plans(
{
	@Plan(trigger=@Trigger(goals=GarbageCollectorAgent.Check.class), body=@Body(CheckingPlanEnv.class)),
	@Plan(trigger=@Trigger(goals=GarbageCollectorAgent.Take.class), body=@Body(TakePlanEnv.class)),
	@Plan(trigger=@Trigger(goals=GarbageCollectorAgent.Go.class), body=@Body(GoPlanEnv.class)),
	@Plan(trigger=@Trigger(goals=GarbageCollectorAgent.Pick.class), body=@Body(PickUpPlanEnv.class))
})
public class GarbageCollectorAgent extends BaseAgent
{
//	/** The position. */
//	@Belief(dynamic=true)
//	protected IVector2 pos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

	/**
	 *  Goal for picking up a piece of waste, bringing it
	 *  to some burner and going back. A new goal is created
	 *  whenever the actual position is dirty and there is no
	 *  burner present.
	 */
	@Goal(unique=true, deliberation=@Deliberation(inhibits={Check.class}))
	public static class Take
	{
		/**
		 * 
		 */
		// todo: support directly factadded etc.
//		@GoalCreationCondition(beliefs="garbages")
		@GoalCreationCondition(rawevents=@RawEvent(value=ChangeEvent.FACTADDED, second="garbages"))
//		public static boolean checkCreate(GarbageCollectorBDI outer, ISpaceObject garbage, IEvent event)
		public static boolean checkCreate(GarbageCollectorAgent outer, ISpaceObject garbage, ChangeEvent event)
		{
			boolean ret = outer.isDirty() && outer.getEnvironment().getSpaceObjectsByGridPosition(outer.getPosition(), "burner")==null;
//			if(ret)
//				System.out.println("collector creating new take goal for: "+garbage);
			return ret;
		}
		
		/**
		 *  Get the hashcode. 
		 */
		public int hashCode()
		{
			return 31;
		}
		
		/**
		 *  Test if equal to other object.
		 */
		public boolean equals(Object obj)
		{
			return obj instanceof Take;
		}
	}
	
	/**
	 *  Goal for running around on the grid and searching for garbage.
	 */
	@Goal(excludemode=ExcludeMode.Never, orsuccess=false)
	public class Check
	{
	}
	
	/**
	 *  Goal for going to a specified position.
	 */
	@Goal(excludemode=ExcludeMode.Never)
	public class Go
	{
		/** The position. */
		protected IVector2 pos;

		/**
		 *  Create a new Go. 
		 */
		public Go(IVector2 pos)
		{
			this.pos = pos;
		}

		/**
		 *  Get the pos.
		 *  @return The pos.
		 */
		public IVector2 getPosition()
		{
			return pos;
		}
	}
	
	/**
	 *  The goal for picking up waste. Tries endlessly to pick up.
	 */
	@Goal(excludemode=ExcludeMode.Never, retrydelay=1000)
	public class Pick
	{
		/**
		 * 
		 */
		@GoalDropCondition(beliefs="garbages")
		public boolean checkDrop()
		{
			boolean ret = !isDirty() && !hasGarbage();
//			if(ret)
//				System.out.println("drop pick: "+isDirty()+", "+hasGarbage()+" "+myself.getProperty(Space2D.PROPERTY_POSITION));
			return ret;
			//!$beliefbase.is_dirty &amp;&amp; !$beliefbase.has_garbage
		}
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new Check());
	}
	
	/**
	 * 
	 */
	protected boolean isDirty()
	{
		return garbages.size()>0;
	}
	
	/**
	 * 
	 */
	protected boolean hasGarbage()
	{
		return myself.getProperty("garbage")!=null;
	}
	
}
