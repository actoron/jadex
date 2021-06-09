package jadex.bdiv3.examples.disastermanagement.ambulance;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.examples.disastermanagement.ambulance.AmbulanceAgent.TreatVictims;
import jadex.bdiv3.examples.disastermanagement.movement.MovementCapa.Move;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;

/**
 *  Move to victims and treat them.
 */
@Plan
public class AmbulancePlan
{
	//-------- attributes --------

	@PlanCapability
	protected AmbulanceAgent capa;
	
	@PlanAPI
	protected IPlan rplan;
	
//	@PlanReason
//	protected TreatVictims goal;
		
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		Space2D	space	= (Space2D)capa.getMoveCapa().getEnvironment();
		ISpaceObject myself	= capa.getMoveCapa().getMyself();
		IVector2 home = capa.getMoveCapa().getHomePosition();
		
		while(true)
		{
			// Find nearest disaster with victims.
			IVector2	mypos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			IVector2	targetpos	= null;
			ISpaceObject	target	= null;
			ISpaceObject[]	disasters	= space.getSpaceObjectsByType("disaster");
			for(int i=0; i<disasters.length; i++)
			{
				if(((Number)disasters[i].getProperty("victims")).intValue()>0)
				{
					IVector2	newpos	= (IVector2)disasters[i].getProperty(Space2D.PROPERTY_POSITION);
					if(target==null || space.getDistance(mypos, newpos).less(space.getDistance(mypos, targetpos)))
					{
						target	= disasters[i];
						targetpos	= newpos;
					}
				}
			}
			
			// Treat victims.
			if(target!=null)
			{
				TreatVictims goal = capa.new TreatVictims(target.getId());
				rplan.dispatchSubgoal(goal).get();
			}
			
			// If no victims and not home: move to home base
			else if(space.getDistance(mypos, home).greater(Vector1Int.ZERO))
			{
				Move move = capa.getMoveCapa().new Move(home);
				rplan.dispatchSubgoal(move).get();
			}
			
			// If no fire and at home: wait a little before checking again
			else
			{
				rplan.waitFor((long)(Math.random()*5000)).get();
			}
		}
	}
	
//	/**
//	 *  Called when a plan fails.
//	 */
//	public void failed()
//	{
//		System.err.println("Plan failed: "+this);
//		getException().printStackTrace();
//	}
}
