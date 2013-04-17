/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.coordination.ClusterStationCoordData;
import sodekovs.bikesharing.coordination.CoordinationStationData;

/**
 * Plan is called when the internal event "gather_occupancy_rate" is fired by DeCoMAS when a station receives a polling request from its clusters super station.
 * 
 * @author Thomas Preisler
 */
public class GatherOccupancyPlan extends Plan {

	private static final long serialVersionUID = -5135934580760735869L;

	public void body() {
		System.out.println("GatherOccupancyPlan started in " + getComponentName());

		while (true) {
			waitForTick();
			Object[] elements = getWaitqueue().getElements();
			// IInternalEvent event = (IInternalEvent) getWaitqueue().removeNextElement();

			if (elements != null) {
				for (Object element : elements) {
					IInternalEvent event = (IInternalEvent) element;
					ClusterStationCoordData receivedCoordData = (ClusterStationCoordData) event.getParameter("coordData").getValue();
					String superStationId = receivedCoordData.getSuperStationId();
					String stationId = (String) getBeliefbase().getBelief("stationID").getFact();
					Vector2Double position = (Vector2Double) getBeliefbase().getBelief("position").getFact();

					System.out.println(getComponentName() + " GatherOccupancyPlan received " + receivedCoordData);

					Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
					Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();

					ClusterStationCoordData coordData = new ClusterStationCoordData();
					coordData.setSuperStationId(superStationId);
					coordData.setState(ClusterStationCoordData.STATE_REPLY);
					CoordinationStationData stationData = new CoordinationStationData(stationId, capacity, stock, position);
					coordData.setStationData(stationData);

					IInternalEvent replyEvent = createInternalEvent("reply_cluster_stations");
					replyEvent.getParameter("coordData").setValue(coordData);
					dispatchInternalEvent(replyEvent);
					
					getWaitqueue().removeElement(element);
				}
			}
		}
	}
}