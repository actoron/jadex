package jadex.bdi.examples.cleanerworld_classic.cleaner;

import jadex.bdi.examples.cleanerworld_classic.Chargingstation;
import jadex.bdi.examples.cleanerworld_classic.Location;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Go to the charging station and load the battery.
 */
public class LoadBatteryPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Hack! Should be done with goal..
	    // todo: test if goal state (-> in_process) could be used 
		//getBeliefbase().getBelief("is_loading").setFact(new Boolean(true));

		// Move to station.
		IGoal findstation = createGoal("querychargingstation");
//		System.out.println("Findstation start: "+findstation);
		dispatchSubgoalAndWait(findstation);
		Chargingstation station = (Chargingstation)findstation.getParameter("result").getValue();
//		System.out.println("Findstation end: "+station);

		if(station!=null)
		{
			IGoal moveto = createGoal("achievemoveto");
			Location location = station.getLocation();
			moveto.getParameter("location").setValue(location);
//			System.out.println("Created: "+location+" "+this);
			dispatchSubgoalAndWait(moveto);
//			System.out.println("Reached: "+location+" "+this);

			location = (Location)getBeliefbase().getBelief("my_location").getFact();
			double	charge	= ((Double)getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue();

			while(location.getDistance(station.getLocation())<0.01 && charge<1.0)
			{
				waitFor(100);
				charge	= ((Double)getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue();
				charge	= Math.min(charge + 0.01, 1.0);
				getBeliefbase().getBelief("my_chargestate").setFact(Double.valueOf(charge));
				location = (Location)getBeliefbase().getBelief("my_location").getFact();
				IGoal dg = createGoal("get_vision_action");
				dispatchSubgoalAndWait(dg);
			}
		}

//		getLogger().info("Loading finished.");
		//getBeliefbase().getBelief("is_loading").setFact(new Boolean(false));
	}

}
