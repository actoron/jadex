package jadex.bdiv3.examples.marsworld.producer;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.marsworld.BaseAgent;
import jadex.bdiv3.examples.marsworld.carry.ICarryService;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.WalkAround;
import jadex.bdiv3.examples.marsworld.sentry.ITargetAnnouncementService;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@Service
@ProvidedServices(@ProvidedService(type=IProduceService.class, implementation=@Implementation(expression="$pojoagent")))
@RequiredServices({
	@RequiredService(name="targetser", type=ITargetAnnouncementService.class, multiple=true),
	@RequiredService(name="carryser", type=ICarryService.class, multiple=true)
})
@Plans({
	@Plan(trigger=@Trigger(goals=ProducerAgent.ProduceOre.class), body=@Body(ProduceOrePlan.class)),
	@Plan(trigger=@Trigger(factadded="movecapa.mytargets"), body=@Body(InformNewTargetPlan.class))
})
public class ProducerAgent extends BaseAgent implements IProduceService
{
	/**
	 * 
	 */
	@Goal(deliberation=@Deliberation(inhibits=WalkAround.class, cardinalityone=true))
	public class ProduceOre
	{
		/** The target. */
		protected ISpaceObject target;

		/**
		 *  Create a new CarryOre. 
		 */
		public ProduceOre(ISpaceObject target)
		{
			this.target = target;
		}
		
		/**
		 * 
		 */
		@GoalDropCondition(beliefs="movecapa.missionend")
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
	public IFuture<Void> doProduce(@Reference ISpaceObject target)
	{
//		System.out.println("producer received produce command: "+target);
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new ProduceOre(target));
		return IFuture.DONE;
	}
}
