/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.coordination.OccupancyTuple;

/**
 * @author thomas
 *
 */
public class ChangedOccupancyPlan extends Plan {

	private static final long serialVersionUID = 8462161530542243714L;

	@Override
	public void body() {
		Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
		Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
		String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
		Vector2Double position = (Vector2Double) getBeliefbase().getBelief("position").getFact();
		
		OccupancyTuple tuple = new OccupancyTuple();
		tuple.setCapacity(capacity);
		tuple.setStationId(stationID);
		tuple.setStock(stock);
		tuple.setPosition(position);
		
		System.out.println("Occupancy changed in " + stationID + " " + tuple);
		
		IInternalEvent event = createInternalEvent("changed_occupancy");
		event.getParameter("tuple").setValue(tuple);
		dispatchInternalEvent(event);
	}
}