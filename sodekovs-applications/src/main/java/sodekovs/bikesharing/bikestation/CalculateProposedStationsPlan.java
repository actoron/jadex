package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.Plan;
import sodekovs.bikesharing.coordination.ClusterStationCoordData;

/**
 * Plan is called when the internal event "calculate_proposed_stations" is fired by DeCoMAS when a super station receives a information about the stocks and capacities of its cluster stations.
 * 
 * @author Thomas Preisler
 */
public class CalculateProposedStationsPlan extends Plan {

	private static final long serialVersionUID = 587076168340446686L;

	public void body() {
		ClusterStationCoordData receivedCoordData = (ClusterStationCoordData) getParameter("coordData").getValue();
		String superStationId = receivedCoordData.getSuperStationId();
		
		System.out.println(getComponentDescription() + " GatherOccupancyPlan received " + receivedCoordData);
		// TODO Auto-generated method stub

	}

}
