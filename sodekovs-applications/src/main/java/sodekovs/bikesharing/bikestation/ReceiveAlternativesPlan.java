/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import sodekovs.bikesharing.coordination.ClusterStationCoordData;

/**
 * Plan is called when the internal event "receive_alternatives" is fired by DeCoMAS when a super station informs its cluster stations about the alternatives for proposed stations.
 * 
 * @author Thomas Preisler
 */
public class ReceiveAlternativesPlan extends Plan {

	private static final long serialVersionUID = 2852258138441042932L;

	@Override
	public void body() {
		System.out.println("ReceiveAlternativesPlan started in " + getComponentName());

		while (true) {
			waitForTick();
			Object[] elements = getWaitqueue().getElements();
			// IInternalEvent event = (IInternalEvent) getWaitqueue().removeNextElement();

			if (elements != null) {
				for (Object element : elements) {
					IInternalEvent event = (IInternalEvent) element;
					ClusterStationCoordData receivedCoordData = (ClusterStationCoordData) event.getParameter("coordData").getValue();
					String stationID = (String) getBeliefbase().getBelief("stationID").getFact();

//					System.out.println(getComponentName() + " ReceiveAlternativesPlan received " + receivedCoordData);

					// if there are no proposed alternatives calculated by the super station for this station then the values will explicitly be set to null!
					String proposedArrivalStation = receivedCoordData.getProposedArrivalStations().get(stationID);
					String proposedDepartureStation = receivedCoordData.getProposedDepartureStations().get(stationID);

//					if (proposedArrivalStation != null)
//						System.out.println("break");
//					if (proposedDepartureStation != null)
//						System.out.println("break");
					
					getBeliefbase().getBelief("proposed_arrival_station").setFact(proposedArrivalStation);
					getBeliefbase().getBelief("proposed_departure_station").setFact(proposedDepartureStation);

					getWaitqueue().removeElement(element);
				}
			}
		}
	}
}