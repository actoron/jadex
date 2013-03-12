package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
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
		Integer noClusterStations = (Integer) getBeliefbase().getBelief("noClusterStations").getFact();
		
		System.out.println(getComponentDescription() + " CalculateProposedStationsPlan received " + receivedCoordData);
		
		// only one plan instance should do the actual calculation when all answer have been received
		startAtomic();
		getBeliefbase().getBeliefSet("receivedCoordData").addFact(receivedCoordData);
		
		if (getBeliefbase().getBeliefSet("receivedCoordData").getFacts().length == noClusterStations) {
			System.out.println(getComponentDescription() + " CalculateProposedStationsPlan MASTER!");
			
			ClusterStationCoordData[] data = (ClusterStationCoordData[]) getBeliefbase().getBeliefSet("receivedCoordData").getFacts();
			getBeliefbase().getBeliefSet("receivedCoordData").removeFacts();
			System.out.println(data.length);
			calculateProposedStations(data);
 		}
		endAtomic();
	}

	private void calculateProposedStations(ClusterStationCoordData[] data) {
		String stationId = (String) getBeliefbase().getBelief("stationID").getFact();
		
		// TODO The actual calculation
		
		IInternalEvent event = createInternalEvent("inform_alternatives");
		ClusterStationCoordData coordData = new ClusterStationCoordData();
		coordData.setState(ClusterStationCoordData.STATE_ALTERNATIVES);
		coordData.setSuperStationId(stationId);
		event.getParameter("coordData").setValue(coordData);
		dispatchInternalEvent(event);
	}
}