package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.math.IVector1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sodekovs.bikesharing.coordination.ClusterStationCoordData;
import sodekovs.bikesharing.coordination.CoordinationStationData;

/**
 * Plan is called when the internal event "calculate_proposed_stations" is fired by DeCoMAS when a super station receives a information about the stocks and capacities of its cluster stations.
 * 
 * @author Thomas Preisler
 */
public class CalculateProposedStationsPlan extends Plan {

	private static final long serialVersionUID = 587076168340446686L;

	private static final double FULL_THRESHOLD = 0.75;
	private static final double EMPTY_THRESHOILD = 0.10;

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
			calculateProposedStations(data);
		}
		endAtomic();
	}

	private void calculateProposedStations(ClusterStationCoordData[] data) {
		String superstationID = (String) getBeliefbase().getBelief("stationID").getFact();

		List<CoordinationStationData> fullStations = new ArrayList<CoordinationStationData>();
		List<CoordinationStationData> emptyStations = new ArrayList<CoordinationStationData>();
		List<CoordinationStationData> normalStations = new ArrayList<CoordinationStationData>();

		// preprocess stations
		for (ClusterStationCoordData clusterStationCoordData : data) {
			Integer stock = clusterStationCoordData.getStationData().getStock();
			Integer capacity = clusterStationCoordData.getStationData().getCapacity();

			Double occupancy = Double.valueOf(stock) / Double.valueOf(capacity);
			if (occupancy >= FULL_THRESHOLD) {
				fullStations.add(clusterStationCoordData.getStationData());
			} else if (occupancy <= EMPTY_THRESHOILD) {
				emptyStations.add(clusterStationCoordData.getStationData());
			} else {
				normalStations.add(clusterStationCoordData.getStationData());
			}
		}

		// proposed_arrival_station
		Map<String, String> proposedArrivalStations = new HashMap<String, String>();
		for (CoordinationStationData stationData : fullStations) {
			List<CoordinationStationData> stations = new ArrayList<CoordinationStationData>();
			stations.addAll(normalStations);
			stations.addAll(emptyStations);

			String proposedArrivalStation = getNearestSuitableStation(stationData, stations);
			proposedArrivalStations.put(stationData.getStationID(), proposedArrivalStation);
		}

		// proposed_departure_station
		Map<String, String> proposedDepartureStations = new HashMap<String, String>();
		for (CoordinationStationData stationData : emptyStations) {
			List<CoordinationStationData> stations = new ArrayList<CoordinationStationData>();
			stations.addAll(normalStations);
			stations.addAll(fullStations);

			String proposedDepartureStation = getNearestSuitableStation(stationData, stations);
			proposedDepartureStations.put(stationData.getStationID(), proposedDepartureStation);
		}

		// the answer
		IInternalEvent event = createInternalEvent("inform_alternatives");
		
		ClusterStationCoordData coordData = new ClusterStationCoordData();
		coordData.setState(ClusterStationCoordData.STATE_ALTERNATIVES);
		coordData.setSuperStationId(superstationID);
		coordData.setProposedArrivalStations(proposedArrivalStations);
		coordData.setProposedDepartureStations(proposedDepartureStations);
		
		event.getParameter("coordData").setValue(coordData);
		dispatchInternalEvent(event);
	}

	/**
	 * Returns the nearest of the given List of suitable stations. Does not prioritize among the suitable stations, only returns the nearest.
	 * @param stationData
	 * @param suitableStations
	 * @return
	 */
	private String getNearestSuitableStation(CoordinationStationData stationData, List<CoordinationStationData> suitableStations) {
		String proposedStation = null;
		IVector1 smallestDistance = null;

		for (CoordinationStationData otherStationData : suitableStations) {
			IVector1 distance = stationData.getPosition().getDistance(otherStationData.getPosition());

			if (smallestDistance == null) {
				smallestDistance = distance;
				proposedStation = otherStationData.getStationID();
			} else if (distance.less(smallestDistance)) {
				smallestDistance = distance;
				proposedStation = otherStationData.getStationID();
			}
		}

		return proposedStation;
	}
}