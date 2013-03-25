/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.coordination.StateCoordinationStationData;

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
		Double emptyThreshold = (Double) getBeliefbase().getBelief("empty_threshold").getFact();
		Double fullThreshold = (Double) getBeliefbase().getBelief("full_threshold").getFact();
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