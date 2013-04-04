package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.Plan;

/**
 * Plan is called when the internal event "calculate_proposed_stations" is fired by DeCoMAS when a super station receives a information about the stocks and capacities of its cluster stations.
 * 
 * @author Thomas Preisler
 */
public class TestPlan extends Plan {


	public void body() {
		
		System.out.println("#TEstPlan Bike Station: # + " +getBeliefbase().getBelief("stationID").getFact() + "- " +getBeliefbase().getBelief("capacity").getFact() );

	}
}