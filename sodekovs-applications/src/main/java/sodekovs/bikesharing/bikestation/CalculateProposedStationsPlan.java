package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sodekovs.bikesharing.coordination.ClusterStationCoordData;
import sodekovs.bikesharing.coordination.CoordinationStationData;
import sodekovs.bikesharing.data.clustering.SuperCluster;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Plan is called when the internal event "calculate_proposed_stations" is fired by DeCoMAS when a super station receives a information about the stocks and capacities of its cluster stations.
 * 
 * @author Thomas Preisler
 */
public class CalculateProposedStationsPlan extends Plan {

	private static final long serialVersionUID = 587076168340446686L;

	public void body() {
		Boolean isSuperStation = (Boolean) getBeliefbase().getBelief("isSuperStation").getFact();

		if (isSuperStation) {
			ContinuousSpace2D environment = (ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact();
			String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
			SuperCluster superCluster = (SuperCluster) environment.getProperty("StationCluster");
			List<String> clusterStationIds = superCluster.getClusterStationIDs(stationID);
			int noClusterStations = clusterStationIds.size();

			while (true) {
				waitForTick();
				if (getWaitqueue().size() >= noClusterStations) {
					System.out.println("CalculateProposedStationsPlan in " + getComponentName() + " received all " + noClusterStations + " answers");
					Object[] elements = getWaitqueue().getElements();

					List<ClusterStationCoordData> clusterStationCoordData = new ArrayList<ClusterStationCoordData>();
					for (Object element : elements) {
						IInternalEvent event = (IInternalEvent) element;
						ClusterStationCoordData coordData = (ClusterStationCoordData) event.getParameter("coordData").getValue();
						clusterStationCoordData.add(coordData);
						getWaitqueue().removeElement(element);
					}

					calculateProposedStations(clusterStationCoordData);

					// ClusterStationCoordData receivedCoordData = (ClusterStationCoordData) getParameter("coordData").getValue();
					// Integer noClusterStations = (Integer) getBeliefbase().getBelief("noClusterStations").getFact();
					//
					// System.out.println(getComponentDescription() + " CalculateProposedStationsPlan received " + receivedCoordData);
					//
					// // only one plan instance should do the actual calculation when all answer have been received
					// startAtomic();
					// getBeliefbase().getBeliefSet("receivedCoordData").addFact(receivedCoordData);
					//
					// if (getBeliefbase().getBeliefSet("receivedCoordData").getFacts().length == noClusterStations) {
					// System.out.println(getComponentDescription() + " CalculateProposedStationsPlan MASTER!");
					//
					// ClusterStationCoordData[] data = (ClusterStationCoordData[]) getBeliefbase().getBeliefSet("receivedCoordData").getFacts();
					// getBeliefbase().getBeliefSet("receivedCoordData").removeFacts();
					// calculateProposedStations(data);
					// }
					// endAtomic();
				}
			}
		}
	}

	private void calculateProposedStations(List<ClusterStationCoordData> data) {
		CoordinationSpace coordSpace = (CoordinationSpace) getBeliefbase().getBelief("env").getFact();
		CoordinationMechanism mechanism = coordSpace.getActiveCoordinationMechanisms().get("reply_cluster_stations");
		Double fullThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("FULL_THRESHOLD");
		Double emptyThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("EMPTY_THRESHOLD");

		String superstationID = (String) getBeliefbase().getBelief("stationID").getFact();

		List<CoordinationStationData> fullStations = new ArrayList<CoordinationStationData>();
		List<CoordinationStationData> emptyStations = new ArrayList<CoordinationStationData>();
		List<CoordinationStationData> normalStations = new ArrayList<CoordinationStationData>();

		// preprocess stations
		for (ClusterStationCoordData clusterStationCoordData : data) {
			Integer stock = clusterStationCoordData.getStationData().getStock();
			Integer capacity = clusterStationCoordData.getStationData().getCapacity();

			Double occupancy = Double.valueOf(stock) / Double.valueOf(capacity);
			if (occupancy >= fullThreshold) {
				fullStations.add(clusterStationCoordData.getStationData());
			} else if (occupancy <= emptyThreshold) {
				emptyStations.add(clusterStationCoordData.getStationData());
			} else {
				normalStations.add(clusterStationCoordData.getStationData());
			}
		}

		// new behavior
		// proposed_arrival_station
		Map<String, String> proposedArrivalStations = new HashMap<String, String>();
		for (CoordinationStationData stationData : fullStations) {
			String proposedArrivalStation = getNearestSuitableStation(stationData, emptyStations);
			if (proposedArrivalStation == null) {
				proposedArrivalStation = getNearestSuitableStation(stationData, normalStations);
			}
			
			proposedArrivalStations.put(stationData.getStationID(), proposedArrivalStation);
		}

		// proposed_departure_station
		Map<String, String> proposedDepartureStations = new HashMap<String, String>();
		for (CoordinationStationData stationData : emptyStations) {
			String proposedDepartureStation = getNearestSuitableStation(stationData, fullStations);
			if (proposedDepartureStation == null) {
				proposedDepartureStation = getNearestSuitableStation(stationData, normalStations);
			}
			
			proposedDepartureStations.put(stationData.getStationID(), proposedDepartureStation);
		}
		
//		// proposed_arrival_station
//		Map<String, String> proposedArrivalStations = new HashMap<String, String>();
//		for (CoordinationStationData stationData : fullStations) {
//			List<CoordinationStationData> stations = new ArrayList<CoordinationStationData>();
//			stations.addAll(normalStations);
//			stations.addAll(emptyStations);
//
//			String proposedArrivalStation = getNearestSuitableStation(stationData, stations);
//			proposedArrivalStations.put(stationData.getStationID(), proposedArrivalStation);
//		}
//
//		// proposed_departure_station
//		Map<String, String> proposedDepartureStations = new HashMap<String, String>();
//		for (CoordinationStationData stationData : emptyStations) {
//			List<CoordinationStationData> stations = new ArrayList<CoordinationStationData>();
//			stations.addAll(normalStations);
//			stations.addAll(fullStations);
//
//			String proposedDepartureStation = getNearestSuitableStation(stationData, stations);
//			proposedDepartureStations.put(stationData.getStationID(), proposedDepartureStation);
//		}

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
	 * 
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