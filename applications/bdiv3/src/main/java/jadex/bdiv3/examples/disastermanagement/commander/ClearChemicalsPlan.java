package jadex.bdiv3.examples.disastermanagement.commander;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.examples.disastermanagement.IClearChemicalsService;
import jadex.bdiv3.examples.disastermanagement.commander.CommanderBDI.SendRescueForce;
import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
@Plan
public class ClearChemicalsPlan 
{
	protected ITerminableFuture<Void>	cc;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	@PlanBody
	public void	body(SendRescueForce goal)
	{
		ISpaceObject disaster = goal.getDisaster();
		IClearChemicalsService force = (IClearChemicalsService)goal.getRescueForce();
		cc = force.clearChemicals(disaster.getId());
		cc.get();
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	@PlanAborted
	public void aborted()
	{
		if(cc!=null)
		{
			cc.terminate();
		}
	}
	
	/**
	 * 
	 */
	@PlanPrecondition
	public boolean checkPrecondition(SendRescueForce goal)
	{
		return goal.getRescueForce() instanceof IClearChemicalsService;
	}
}
