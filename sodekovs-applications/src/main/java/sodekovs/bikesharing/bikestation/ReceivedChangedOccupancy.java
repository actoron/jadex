package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.coordination.OccupancyTuple;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * This plan is called whenever the station receives information about a state occupancy state in another station in the same cluster.
 * 
 * @author Thomas Preisler
 */
public class ReceivedChangedOccupancy extends Plan {

	private static final long serialVersionUID = -8242825974497308907L;

	@Override
	public void body() {
		startAtomic();

		String proposedArrivalStation = (String) getBeliefbase().getBelief("proposed_arrival_station").getFact();
		String proposedDepartureStation = (String) getBeliefbase().getBelief("proposed_departure_station").getFact();
		String stationID = (String) getBeliefbase().getBelief("stationID").getFact();

		OccupancyTuple tuple = (OccupancyTuple) getParameter("tuple").getValue();

		System.out.println("ReceivedChangedOccupancy called in " + stationID + " with " + tuple);
		
		// get the occupancy of the station
		Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
		Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
		Double occupancy = Double.valueOf(stock) / Double.valueOf(capacity);

		// get the coordination space
		CoordinationSpace coordSpace = (CoordinationSpace) getBeliefbase().getBelief("env").getFact();
		// and the mechanism for this coordination process
		CoordinationMechanism mechanism = coordSpace.getActiveCoordinationMechanisms().get("tuple_information");
		// to get the global coordination parameters
		Double fullThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("FULL_THRESHOLD");
		Double emptyThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("EMPTY_THRESHOLD");

		// if the updated station is full and is this stations proposed arrival station delete it
		if (tuple.getOccupancy() >= fullThreshold && proposedArrivalStation.equals(tuple.getStationId())) {
			proposedArrivalStation = null;
			getBeliefbase().getBelief("proposed_arrival_station").setFact(null);
		}
		// if the updated station is empty and this stations proposed departure station delete it
		if (tuple.getOccupancy() >= emptyThreshold && proposedDepartureStation.equals(tuple.getStationId())) {
			proposedDepartureStation = null;
			getBeliefbase().getBelief("proposed_departure_station").setFact(null);
		}

		// if the station is full and the remote station is not full
		if (occupancy >= fullThreshold && tuple.getOccupancy() < fullThreshold) {
			// if there is no alternative just set it
			if (proposedArrivalStation == null) {
				getBeliefbase().getBelief("proposed_arrival_station").setFact(tuple.getStationId());
			}
			// if there is a alternative check which one is nearer
			else {
				Vector2Double oldPosition = getPosition(proposedArrivalStation);
				String nearestStation = getNearestStation(proposedArrivalStation, oldPosition, tuple.getStationId(), tuple.getPosition());
				getBeliefbase().getBelief("proposed_arrival_station").setFact(nearestStation);
			}
		}
		// or if the station is empty and the remote station is not empty
		else if (occupancy <= emptyThreshold && tuple.getOccupancy() > emptyThreshold) {
			// if there is no alternative just set it
			if (proposedDepartureStation == null) {
				getBeliefbase().getBelief("proposed_departure_station").setFact(tuple.getStationId());
			}
			// if there is a alternative check which one is nearer
			else {
				Vector2Double oldPosition = getPosition(proposedDepartureStation);
				String nearestStation = getNearestStation(proposedDepartureStation, oldPosition, tuple.getStationId(), tuple.getPosition());
				getBeliefbase().getBelief("proposed_departure_station").setFact(nearestStation);
			}
		}

		endAtomic();
	}

	/**
	 * Returns the position of station with the given stationID within the environment space.
	 * 
	 * @param stationID
	 * @return
	 */
	private Vector2Double getPosition(String stationID) {
		ContinuousSpace2D appSpace = (ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact();

		ISpaceObject[] stations = appSpace.getSpaceObjectsByType("bikestation");
		for (ISpaceObject station : stations) {
			String tmpStationID = (String) station.getProperty("stationID");
			if (stationID.equals(tmpStationID)) {
				Vector2Double position = (Vector2Double) station.getProperty("position");
				return position;
			}
		}

		return null;
	}

	/**
	 * Returns the station id of the station which is nearer to the position of this station.
	 * 
	 * @param stationID1
	 *            the id of the first station
	 * @param position1
	 *            the position of the first station
	 * @param stationID2
	 *            the id of the second station
	 * @param position2
	 *            the position of the second station
	 * @return
	 */
	private String getNearestStation(String stationID1, Vector2Double position1, String stationID2, Vector2Double position2) {
		ContinuousSpace2D appSpace = (ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact();
		Vector2Double referencePosition = (Vector2Double) getBeliefbase().getBelief("position");

		IVector1 distance1 = appSpace.getDistance(referencePosition, position1);
		IVector1 distance2 = appSpace.getDistance(referencePosition, position2);

		if (distance1.less(distance2)) {
			return stationID1;
		} else {
			return stationID2;
		}
	}
}