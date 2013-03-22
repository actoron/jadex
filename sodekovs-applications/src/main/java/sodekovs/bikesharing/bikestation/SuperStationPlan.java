/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;

import java.util.List;

import sodekovs.bikesharing.coordination.ClusterStationCoordData;
import sodekovs.bikesharing.data.clustering.SuperCluster;

/**
 * Plan handles the periodically polling of stations in the super stations cluster via decomas.
 * 
 * @author Thomas Preisler
 */
public class SuperStationPlan extends Plan {

	private static final long serialVersionUID = -1433197689807683447L;

	private static final int NO_TICKS = 200;

	public void body() {
		Boolean isSuperStation = (Boolean) getBeliefbase().getBelief("isSuperStation").getFact();
		String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
		
		// only relevant for super station
		if (isSuperStation) {
			ContinuousSpace2D environment = (ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact();
			SuperCluster superCluster = (SuperCluster) environment.getProperty("StationCluster");
			
			List<String> clusterStationIds = superCluster.getClusterStationIDs(stationID);
			getBeliefbase().getBelief("noClusterStations").setFact(clusterStationIds.size());
			
			while (true) {
				// wait for some time
				for (int i = 0; i < NO_TICKS; i++) {
//					waitFor(NO_TICKS);
					waitForTick();
				}
				IInternalEvent pollEvent = createInternalEvent("poll_cluster_stations");
				
				ClusterStationCoordData data = new ClusterStationCoordData();
				data.setSuperStationId(stationID);
				data.setState(ClusterStationCoordData.STATE_POLLING);
				
				pollEvent.getParameter("coordData").setValue(data);
				dispatchInternalEvent(pollEvent);
			}
		}
	}
}