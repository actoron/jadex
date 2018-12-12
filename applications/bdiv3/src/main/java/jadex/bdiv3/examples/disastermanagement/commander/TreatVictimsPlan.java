package jadex.bdiv3.examples.disastermanagement.commander;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.examples.disastermanagement.ITreatVictimsService;
import jadex.bdiv3.examples.disastermanagement.commander.CommanderAgent.SendRescueForce;
import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
@Plan
public class TreatVictimsPlan
{
	//-------- attributes --------
	
	/** The service future. */
	protected ITerminableFuture<Void>	tv;
	
	//-------- plan methods --------
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	@PlanBody
	public void	body(SendRescueForce goal)
	{
		ISpaceObject disaster = (ISpaceObject)goal.getDisaster();
//		System.out.println("treat victims for: "+disaster);
		ITreatVictimsService force = (ITreatVictimsService)goal.getRescueForce();
		tv	= force.treatVictims(disaster.getId());
		tv.get();
//		System.out.println("treat victims end for: "+disaster);
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	@PlanAborted
	public void aborted()
	{
		if(tv!=null)
		{
			try
			{
				tv.terminate();
			}
			catch(Exception e)
			{
				// Wait until service is finished before superordinated goal is dropped.
				tv.get();
			}
		}
	}
	
	/**
	 * 
	 */
	@PlanPrecondition
	public boolean checkPrecondition(SendRescueForce goal)
	{
		return goal.getRescueForce() instanceof ITreatVictimsService;
	}
}
