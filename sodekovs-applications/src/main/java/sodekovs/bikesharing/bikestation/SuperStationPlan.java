/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import sodekovs.bikesharing.coordination.ClusterStationCoordData;

/**
 * Plan handles the periodically polling of stations in the super stations cluster via decomas.
 * 
 * @author Thomas Preisler
 */
public class SuperStationPlan extends Plan {

	private static final long serialVersionUID = -1433197689807683447L;

	private static final int PERIOD = 10000;

	public void body() {
		Boolean isSuperStation = (Boolean) getBeliefbase().getBelief("isSuperStation").getFact();
		String stationID = (String) getBeliefbase().getBelief("stationID").getFact();

		// only relevant for super station
		if (isSuperStation) {
			while (true) {
				// wait for some time
				waitFor(PERIOD);
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