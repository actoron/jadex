package jadex.bdiv3.examples.marsworld.producer;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.Move;
import jadex.bdiv3.examples.marsworld.producer.ProducerBDI.ProduceOre;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;


/**
 *  Inform the sentry agent about a new target.
 */
@Plan
public class ProduceOrePlan 
{
	//-------- attributes --------

	@PlanCapability
	protected MovementCapability capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected ProduceOre goal;
		
	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject target = goal.getTarget();

		// Move to the target.
		Move move = capa.new Move((IVector2)target.getProperty(Space2D.PROPERTY_POSITION));
		rplan.dispatchSubgoal(move).get();
		
		// Produce ore at the target.
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut);
		ISpaceObject	myself	= capa.getMyself();
		Map props = new HashMap();
		props.put(ProduceOreTask.PROPERTY_TARGET, target);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
		IEnvironmentSpace space = capa.getEnvironment();
		Object taskid	= space.createObjectTask(ProduceOreTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(taskid, myself.getId(), lis);
		fut.get();
//		System.out.println("Produced ore at target: "+getAgentName()+", "+ore+" ore produced.");
	}
}
