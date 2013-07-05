package jadex.bdiv3.examples.marsworld.sentry;

import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.marsworld.BaseBDI;
import jadex.bdiv3.examples.marsworld.carry.CarryBDI;
import jadex.bdiv3.examples.marsworld.carry.CarryOrePlan;
import jadex.bdiv3.examples.marsworld.carry.InformNewTargetPlan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;

@Agent
@Plans(
{
	@Plan(trigger=@Trigger(goals=CarryBDI.CarryOre.class), body=@Body(CarryOrePlan.class)),
	@Plan(trigger=@Trigger(factaddeds="movecapa.mytargets"), body=@Body(InformNewTargetPlan.class))
})
public class SentryBDI extends BaseBDI
{
	/**
	 * 
	 */
	@Goal(unique=true)
	public static class AnalyzeTarget
	{
		/** The sentry agent. */
		protected SentryBDI outer;
		
		/** The target. */
		protected ISpaceObject target;

		/**
		 *  Create a new CarryOre. 
		 */
		// todo: allow direct goal creation on fact added
		@GoalCreationCondition(events="movecapa.mytargets")
		public AnalyzeTarget(SentryBDI outer, ISpaceObject target)
		{
			this.outer = outer;
			this.target = target;
		}
		
//		/**
//		 * 
//		 */
//		@GoalCreationCondition(events="movecapa.mytargets")
//		public static boolean checkCreate(SentryBDI outer)
//		{
//			for(ISpaceObject so: outer.getMoveCapa().getMyTargets())
//			{
//				if(so.)
//			}
//		}
		
		/**
		 * 
		 */
		@GoalContextCondition
		public boolean checkContext()
		{
			IVector2 mypos = (IVector2)outer.getMoveCapa().getMyself().getProperty(Space2D.PROPERTY_POSITION);
			ISpaceObject nearest = null;
			IVector2 npos = null;
			for(ISpaceObject so: outer.getMoveCapa().getMyTargets())
			{
				if(nearest==null)
				{
					nearest = so;
					npos = (IVector2)nearest.getProperty(Space2D.PROPERTY_POSITION);
				}
				else
				{
					IVector2 spos = (IVector2)so.getProperty(Space2D.PROPERTY_POSITION);
					if(mypos.getDistance(spos).less(mypos.getDistance(npos)))
					{
						nearest = so;
						npos = (IVector2)nearest.getProperty(Space2D.PROPERTY_POSITION);
					}
				}
			}
			
			return nearest.equals(target);
			
			//			(select one Target $target from $beliefbase.my_targets
			// order by $beliefbase.my_location.getDistance($target.getLocation()))
			// == $goal.target
		}
		
		/**
		 * 
		 */
		@GoalDropCondition(events="movecapa.missionend")
		public boolean checkDrop()
		{
			return outer.getMoveCapa().isMissionend();
		}

		/**
		 *  Get the target.
		 *  @return The target.
		 */
		public ISpaceObject getTarget()
		{
			return target;
		}
	}
}

