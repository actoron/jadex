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
		Integer noClusterStations = (Integer) getBeliefbase().getBelief("noClusterStations").getFact();
		
		System.out.println(getComponentDescription() + " CalculateProposedStationsPlan received " + receivedCoordData);
		
		//TODO Anstelle des atomic Blocks werden die daten nur in den beliefset geschrieben, dann wird noch ein plan geschrieben der initial ausgeführt wird und per condition oder timeout wartet bis
		// alle antworten eingesammelt wurden oder der timeout eintritt ehe er die proposed stations berechnet
		startAtomic();
		getBeliefbase().getBeliefSet("receivedCoordData").addFact(receivedCoordData);
		
		if (getBeliefbase().getBeliefSet("receivedCoordData").getFacts().length == noClusterStations) {
			System.out.println(getComponentDescription() + " CalculateProposedStationsPlan MASTER!");
			
			ClusterStationCoordData[] data = (ClusterStationCoordData[]) getBeliefbase().getBeliefSet("receivedCoordData").getFacts();
			getBeliefbase().getBeliefSet("receivedCoordData").removeFacts();
			System.out.println(data.length);
 		}
		endAtomic();
	}
}