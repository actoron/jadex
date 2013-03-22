/**
 * 
 */
package sodekovs.bikesharing.bikestation;

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
		ClusterStationCoordData receivedCoordData = (ClusterStationCoordData) getParameter("coordData").getValue();
		String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
		
		System.out.println(getComponentDescription() + " ReceiveAlternativesPlan received " + receivedCoordData);
		
		// if there are no proposed alternatives calculated by the super station for this station then the values will explicitly be set to null!
		String proposedArrivalStation = receivedCoordData.getProposedArrivalStations().get(stationID);
		String proposedDepartureStation = receivedCoordData.getProposedDepartureStations().get(stationID);
		
		getBeliefbase().getBelief("proposed_arrival_station").setFact(proposedArrivalStation);
		getBeliefbase().getBelief("proposed_departure_station").setFact(proposedDepartureStation);
	}
}