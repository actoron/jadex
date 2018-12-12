package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerAgent.DropWasteAction;
import jadex.bdiv3.examples.cleanerworld.world.IEnvironment;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;



/**
 *  Pick up a piece of waste in the environment.
 */
@Plan
public class LocalDropWasteActionPlan
{
	@PlanCapability
	protected CleanerAgent capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected DropWasteAction goal;
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		IEnvironment environment = capa.getEnvironment();
		
		if(goal.getWaste()==null)
			System.out.println("waste nulls: "+goal);
		
		boolean	success	= environment.dropWasteInWastebin(goal.getWaste(), goal.getWastebin());

		if(!success)
			return new Future<Void>(new PlanFailureException());
		else
			return IFuture.DONE;
	}
}
