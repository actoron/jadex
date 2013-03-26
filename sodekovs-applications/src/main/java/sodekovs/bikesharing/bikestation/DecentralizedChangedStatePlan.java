/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.coordination.StateCoordinationStationData;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * A plan...
 * 
 * @author Thomas Preisler
 */
public class DecentralizedChangedStatePlan extends Plan {

	private static final long serialVersionUID = -1566970353465699453L;

	@Override
	public void body() {
		Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
		Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
		String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
		Vector2Double position = (Vector2Double) getBeliefbase().getBelief("position").getFact();
		CoordinationSpace coordSpace = (CoordinationSpace) getBeliefbase().getBelief("env").getFact();
		CoordinationMechanism mechanism = coordSpace.getActiveCoordinationMechanisms().get("decentralized_polling_request");
		Double fullThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("FULL_THRESHOLD");
		Double emptyThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("EMPTY_THRESHOLD");
		Double occupany = Double.valueOf(stock) / Double.valueOf(capacity);

		if (occupany >= fullThreshold || occupany <= emptyThreshold) {
			StateCoordinationStationData data = new StateCoordinationStationData(stationID, capacity, stock, position, StateCoordinationStationData.REQUEST, stationID);

			System.out.println("Occupancy state changed in " + stationID + " " + data);

			IInternalEvent event = createInternalEvent("changed_state");
			event.getParameter("data").setValue(data);
			dispatchInternalEvent(event);
		}
	}
}