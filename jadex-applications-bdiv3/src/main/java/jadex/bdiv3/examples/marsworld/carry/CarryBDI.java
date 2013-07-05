package jadex.bdiv3.examples.marsworld.carry;

import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.marsworld.BaseBDI;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.WalkAround;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;

/**
 * 
 */
@Agent
@Plans(
{
	@Plan(trigger=@Trigger(goals=CarryBDI.CarryOre.class), body=@Body(CarryOrePlan.class)),
	@Plan(trigger=@Trigger(factaddeds="movecapa.mytargets"), body=@Body(InformNewTargetPlan.class))
})
public class CarryBDI extends BaseBDI implements ICarryService
{
	/**
	 * 
	 */
	@Goal(deliberation=@Deliberation(inhibits=WalkAround.class))
	public class CarryOre
	{
		/** The target. */
		protected ISpaceObject target;

		/**
		 *  Create a new CarryOre. 
		 */
		public CarryOre(ISpaceObject target)
		{
			this.target = target;
		}
		
		/**
		 * 
		 */
		@GoalDropCondition(events="movecapa.missionend")
		public boolean checkDrop()
		{
			return movecapa.isMissionend();
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
	
	/**
	 * 
	 */
	public IFuture<Void> doCarry(@Reference ISpaceObject target)
	{
		agent.dispatchTopLevelGoal(new CarryOre(target));
		return IFuture.DONE;
	}
}



