package jadex.bdiv3.examples.marsworld.producer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.marsworld.carry.ICarryService;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.Move;
import jadex.bdiv3.examples.marsworld.producer.ProducerBDI.ProduceOre;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;


/**
 *  Inform the sentry agent about a new target.
 */
@Plan
public class ProduceOrePlan 
{
	//-------- attributes --------

	@PlanCapability
	protected ProducerBDI producer;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected ProduceOre goal;
		
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		ISpaceObject target = goal.getTarget();

		MovementCapability capa = producer.getMoveCapa();
		
		// Move to the target.
		Move move = capa.new Move(target.getProperty(Space2D.PROPERTY_POSITION));
		rplan.dispatchSubgoal(move).get();
		
		// Produce ore at the target.
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
		ISpaceObject	myself	= capa.getMyself();
		Map props = new HashMap();
		props.put(ProduceOreTask.PROPERTY_TARGET, target);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
		IEnvironmentSpace space = capa.getEnvironment();
		Object taskid	= space.createObjectTask(ProduceOreTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(taskid, myself.getId(), lis);
		fut.get();
//		System.out.println("Produced ore at target: "+getAgentName()+", "+ore+" ore produced.");
		
		callCarryAgent(target);
	}

	/**
	 *  Sending a location to the Producer Agent.
	 *  Therefore it has first to be looked up in the DF.
	 *  @param target
	 */
	private void callCarryAgent(ISpaceObject target)
	{
//		System.out.println("Calling some Production Agent...");

		try
		{
			IFuture<Collection<ICarryService>> fut = producer.getAgent().getComponentFeature(IRequiredServicesFeature.class).getServices("carryser");
			Collection<ICarryService> ansers = fut.get();
			
			for(ICarryService anser: ansers)
			{
				anser.doCarry(target);
			}
		}
		catch(RuntimeException e)
		{
			System.out.println("No carry found");
		}
	}
}
