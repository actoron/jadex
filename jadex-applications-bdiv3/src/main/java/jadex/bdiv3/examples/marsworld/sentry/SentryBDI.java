package jadex.bdiv3.examples.marsworld.sentry;

import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.marsworld.BaseBDI;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.bdiv3.examples.marsworld.producer.IProduceService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@Plans(
{
	@Plan(trigger=@Trigger(goals=SentryBDI.AnalyzeTarget.class), body=@Body(AnalyzeTargetPlan.class))
})
@RequiredServices(@RequiredService(name="produceser", multiple=true, type=IProduceService.class))
public class SentryBDI extends BaseBDI implements ITargetAnnouncementService
{
	/**
	 * 
	 */
	public IFuture<Void> announceNewTarget(ISpaceObject target)
	{
		System.out.println("Sentry was informed about new target: "+target);
		movecapa.addTarget(target);
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	@Goal(unique=true, deliberation=@Deliberation(inhibits=MovementCapability.WalkAround.class))
	public static class AnalyzeTarget
	{
		/** The sentry agent. */
		protected SentryBDI outer;
		
		/** The target. */
		protected ISpaceObject target;

		/**
		 *  Create a new AnalyzeTarget. 
		 */
		// todo: allow direct goal creation on fact added
//		@GoalCreationCondition(events="movecapa.mytargets")
		public AnalyzeTarget(SentryBDI outer, ISpaceObject target)
		{
//			System.out.println("new analyze target goal: "+target);
			if(target==null)
				System.out.println("target nulls");
			this.outer = outer;
			this.target = target;
		}
		
		/**
		 * 
		 */
		@GoalCreationCondition(events="movecapa.mytargets")
		public static AnalyzeTarget checkCreate(SentryBDI outer, ISpaceObject target)
		{
			AnalyzeTarget ret = null;
			if(target.getProperty("state").equals("unknown"))
				ret = new AnalyzeTarget(outer, target);
			return ret;
		}

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
			System.out.println("dropping: "+outer.getMoveCapa().isMissionend());
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
		
		/**
		 *  Get the outer.
		 *  @return The outer.
		 */
		public SentryBDI getOuter()
		{
			return outer;
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
			result = prime * result + ((target == null) ? 0 : target.hashCode());
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
			if(obj instanceof AnalyzeTarget)
			{
				AnalyzeTarget other = (AnalyzeTarget)obj;
				ret = outer.getClass().equals(other.getOuter().getClass()) && SUtil.equals(target, other.getTarget());
			}
			return ret;
		}
	}

}

