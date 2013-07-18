package jadex.bdiv3.examples.garbagecollector;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.garbagecollector.GarbageCollectorBDI.Pick;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;

import java.util.HashMap;
import java.util.Map;

/**
 *  Try to pickup some piece of garbage.
 */
@Plan
public class PickUpPlanEnv
{
	//-------- attributes --------

	@PlanCapability
	protected BaseAgentBDI agent;
		
	@PlanAPI
	protected IPlan rplan;
		
	@PlanReason
	protected Pick goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
//		System.out.println("Pickup plan: "+getAgentName()+" "+getReason());
		
		IEnvironmentSpace env = agent.getEnvironment();
		// todo: garbage as parameter?
		
		Future<Boolean> fut = new Future<Boolean>();
		DelegationResultListener<Boolean> lis = new DelegationResultListener<Boolean>(fut, true);
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, agent.getAgent().getComponentDescription());
		env.performSpaceAction("pickup", params, lis); // todo: garbage as parameter?
		Boolean done = fut.get();  
		if(!done.booleanValue())
			throw new PlanFailureException();
			
		// todo: handle result
//		if(!((Boolean)srl.waitForResult()).booleanValue()) 
//			fail();
		
//		System.out.println("pickup plan end");
	}
}
