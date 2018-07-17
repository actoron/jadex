package jadex.bdiv3.examples.disastermanagement.firebrigade;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.examples.disastermanagement.ambulance.AmbulanceAgent;
import jadex.bdiv3.examples.disastermanagement.firebrigade.FireBrigadeAgent.ClearChemicals;
import jadex.bdiv3.examples.disastermanagement.firebrigade.FireBrigadeAgent.ExtinguishFire;
import jadex.bdiv3.examples.disastermanagement.movement.MovementCapa.Move;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;

/**
 *  Move to fires and extinguish them.
 */
@Plan
public class FireBrigadePlan 
{
	//-------- attributes --------

	@PlanCapability
	protected AmbulanceAgent capa;
	
	@PlanAPI
	protected IPlan rplan;

	@PlanCapability
	protected FireBrigadeAgent fireBrigadeBDI;

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
//		ISpaceObject disaster = (ISpaceObject)goal.getDisaster();
		
		while(true)
		{
			// Find nearest disaster with fire or chemicals.
			IVector2	mypos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			IVector2	targetpos	= null;
			ISpaceObject	target	= null;
			ISpaceObject[]	disasters	= space.getSpaceObjectsByType("disaster");
			for(int i=0; i<disasters.length; i++)
			{
				if(((Number)disasters[i].getProperty("fire")).intValue()>0 || ((Number)disasters[i].getProperty("chemicals")).intValue()>0)
				{
					IVector2	newpos	= (IVector2)disasters[i].getProperty(Space2D.PROPERTY_POSITION);
					if(target==null || space.getDistance(mypos, newpos).less(space.getDistance(mypos, targetpos)))
					{
						target	= disasters[i];
						targetpos	= newpos;
					}
				}
			}
			
			// Decide between fire and chemicals
			if(target!=null)
			{
				boolean	fire	= ((Number)target.getProperty("fire")).intValue()>0;
				boolean	chemicals	= ((Number)target.getProperty("chemicals")).intValue()>0;
				String	goaltype	= fire && !chemicals ? "extinguish_fire"
					: !fire && chemicals ? "clear_chemicals"
					: fire && chemicals ? Math.random()>0.5 ? "extinguish_fire" : "clear_chemicals"
					: null;
				if(goaltype!=null)
				{
					if("clear_chemicals".equals(goaltype))
					{
						ClearChemicals goal = fireBrigadeBDI.new ClearChemicals(target);
						rplan.dispatchSubgoal(goal).get();
					}
					else if("extinguish_fire".equals(goaltype))
					{
						ExtinguishFire goal = fireBrigadeBDI.new ExtinguishFire(target);
						rplan.dispatchSubgoal(goal).get();
					}
				}
			}
			
			// If no fire or chemicals and not home: move to home base
			else if(space.getDistance(mypos, home).greater(Vector1Int.ZERO))
			{
				Move move = capa.getMoveCapa().new Move(home);
				rplan.dispatchSubgoal(move).get();
			}
			
			// If no fire or chemicals and at home: wait a little before checking again
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
//		System.out.println("Plan failed: "+this);
//		getException().printStackTrace();
//	}
}
