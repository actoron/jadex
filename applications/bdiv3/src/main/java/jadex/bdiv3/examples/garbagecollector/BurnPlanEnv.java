package jadex.bdiv3.examples.garbagecollector;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.examples.garbagecollector.GarbageBurnerBDI.Pick;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;

/**
 *  Burn a piece of garbage.
 */
@Plan
public class BurnPlanEnv 
{
	//-------- attributes --------

	@PlanCapability
	protected GarbageBurnerBDI burner;
	
	@PlanAPI
	protected IPlan rplan;
	
//	@PlanReason
//	protected Burn goal;
	
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
//		System.out.println("Burn plan activated!");
		
		IEnvironmentSpace env = burner.getEnvironment();

		// Pickup the garbarge.
		Pick pickup = burner.new Pick();
		rplan.dispatchSubgoal(pickup).get();
		
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.ACTOR_ID, burner.getAgent().getComponentDescription());
		env.performSpaceAction("burn", params, lis);
		fut.get();
	}
}
