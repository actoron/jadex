/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
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
		ClusterStationCoordData receivedCoordData = (ClusterStationCoordData) getParameter("coordData").getValue();
		String superStationId = receivedCoordData.getSuperStationId();
		
		System.out.println(getComponentDescription() + " GatherOccupancyPlan received " + receivedCoordData);
		
		Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
		Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
		
		ClusterStationCoordData coordData = new ClusterStationCoordData();
		coordData.setSuperStationId(superStationId);
		coordData.setState(ClusterStationCoordData.STATE_REPLY);
		coordData.getData().put(ClusterStationCoordData.CAPACITY, capacity);
		coordData.getData().put(ClusterStationCoordData.STOCK, stock);
		
		IInternalEvent event = createInternalEvent("reply_cluster_stations");
		event.getParameter("coordData").setValue(coordData);
		dispatchInternalEvent(event);
	}
}