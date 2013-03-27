/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.coordination.CoordinationStationData;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Plan is called whenever the stock or the capacity of a station changes. The plan changes the alternative stations if they are not needed anymore and informs other stations about the changed
 * occupancy using DeCoMAS.
 * 
 * @author Thomas Preisler
 */
public class ChangedOccupancyPlan extends Plan {

	private static final long serialVersionUID = 8462161530542243714L;

	@Override
	public void body() {
		CoordinationSpace coordSpace = (CoordinationSpace) getBeliefbase().getBelief("env").getFact();
		if (coordSpace != null) {
			Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
			Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
			String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
			Vector2Double position = (Vector2Double) getBeliefbase().getBelief("position").getFact();
			
			CoordinationMechanism mechanism = coordSpace.getActiveCoordinationMechanisms().get("decentralized_polling_request");
			Double fullThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("FULL_THRESHOLD");
			Double emptyThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("EMPTY_THRESHOLD");
			Double occupany = Double.valueOf(stock) / Double.valueOf(capacity);
	
			CoordinationStationData tuple = new CoordinationStationData(stationID, capacity, stock, position);
			System.out.println("Occupancy changed in " + stationID + " " + tuple);
	
			// reset alternative if they are not needed any more
			if (occupany < fullThreshold) {
				getBeliefbase().getBelief("proposed_arrival_station").setFact(null);
			}
			if (occupany > emptyThreshold) {
				getBeliefbase().getBelief("proposed_departure_station").setFact(null);
			}
	
			IInternalEvent event = createInternalEvent("changed_occupancy");
			event.getParameter("tuple").setValue(tuple);
			dispatchInternalEvent(event);
		}
	}
}