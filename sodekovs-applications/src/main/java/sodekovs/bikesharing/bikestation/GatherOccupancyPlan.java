/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.Plan;
import sodekovs.bikesharing.coordination.ClusterStationCoordData;

/**
 * Plan is called when the internal event "gather_occupancy_rate" is fired by DeCoMAS when a station receives a polling request
 * from its clusters super station.
 * 
 * @author Thomas Preisler
 */
public class GatherOccupancyPlan extends Plan {

	private static final long serialVersionUID = -5135934580760735869L;

	public void body() {
		ClusterStationCoordData coordData = (ClusterStationCoordData) getParameter("coordData").getValue();
		
		System.out.println(getComponentDescription() + " GatherOccupancyPlan received " + coordData);
	}
}