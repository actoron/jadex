package jadex.bdiv3.examples.marsworld.carry;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.marsworld.ITargetAnnouncementService;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

import java.util.Collection;

/**
 *  Inform the sentry agent about a new target.
 */
@Plan
public class InformNewTargetPlan
{
	//-------- attributes --------

	@PlanCapability
	protected MovementCapability capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected ISpaceObject target;
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IFuture<Collection<ITargetAnnouncementService>> fut = capa.getCapability().getServiceContainer().getRequiredServices("targetser");
		Collection<ITargetAnnouncementService> ansers = fut.get();
		
		for(ITargetAnnouncementService anser: ansers)
		{
			anser.announceNewTarget(target);
		}
		
//		System.out.println("Informing sentries: "+getScope().getAgentName());
	}
}
