/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.ArrayList;
import java.util.List;

import sodekovs.bikesharing.coordination.StateCoordinationStationData;
import sodekovs.bikesharing.data.clustering.Cluster;
import sodekovs.bikesharing.data.clustering.SuperCluster;

/**
 * @author thomas
 *
 */
public class DecentralizedPollingResultPlan extends Plan {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5637876306669348645L;

	@Override
	public void body() {
		ContinuousSpace2D environment = (ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact();
		SuperCluster superCluster = (SuperCluster) environment.getProperty("StationCluster");
		String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
		Cluster cluster = superCluster.getCluster(stationID);
		int noStations = cluster.getStations().size();
		StateCoordinationStationData data = (StateCoordinationStationData) getParameter("data").getValue();
		
		System.out.println(getComponentDescription() + " DecentralizedPollingResultPlan received " + data);
		// only one plan instance should do the actual calculation when all answer have been received
		startAtomic();
		getBeliefbase().getBeliefSet("receivedDecentralizedCoordData").addFact(data);

		if (getBeliefbase().getBeliefSet("receivedDecentralizedCoordData").getFacts().length == noStations) {
			System.out.println(getComponentDescription() + " DecentralizedPollingResultPlan MASTER!");

			StateCoordinationStationData[] answers = (StateCoordinationStationData[]) getBeliefbase().getBeliefSet("receivedDecentralizedCoordData").getFacts();
			getBeliefbase().getBeliefSet("receivedDecentralizedCoordData").removeFacts();
			calculateProposedStations(answers);
		}
		endAtomic();
	}

	private void calculateProposedStations(StateCoordinationStationData[] answers) {
		Double emptyThreshold = (Double) getBeliefbase().getBelief("empty_threshold").getFact();
		Double fullThreshold = (Double) getBeliefbase().getBelief("full_threshold").getFact();
		Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
		Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
		Double occupancy = Double.valueOf(stock) / Double.valueOf(capacity);
		
		// full
		if (occupancy >= fullThreshold) {
			getBeliefbase().getBelief("proposed_departure_station").setFact(null);
			
			List<StateCoordinationStationData> suitableStations = new ArrayList<StateCoordinationStationData>();
			for (StateCoordinationStationData data : answers) {
				if (data.getOccupancy() < fullThreshold) {
					suitableStations.add(data);
				}
			}
			
			String nearestStationId = getNearest(suitableStations);
			getBeliefbase().getBelief("proposed_arrival_station").setFact(nearestStationId);
		}
		// empty
		else if (occupancy <= emptyThreshold) {
			getBeliefbase().getBelief("proposed_arrival_station").setFact(null);
			
			List<StateCoordinationStationData> suitableStations = new ArrayList<StateCoordinationStationData>();
			for (StateCoordinationStationData data : answers) {
				if (data.getOccupancy() > emptyThreshold) {
					suitableStations.add(data);
				}
			}
			
			String nearestStationId = getNearest(suitableStations);
			getBeliefbase().getBelief("proposed_departure_station").setFact(nearestStationId);
		}
	}

	private String getNearest(List<StateCoordinationStationData> stations) {
		ContinuousSpace2D environment = (ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact();
		Vector2Double position = (Vector2Double) getBeliefbase().getBelief("position").getFact();
		IVector1 shortestDistance = null;
		String nearestStation = null;
		
		for (StateCoordinationStationData data : stations) {
			if (shortestDistance == null) {
				shortestDistance = environment.getDistance(position, data.getPosition());
				nearestStation = data.getStationID();
			} else if (environment.getDistance(position, data.getPosition()).less(shortestDistance)) {
				shortestDistance = environment.getDistance(position, data.getPosition());
				nearestStation = data.getStationID();
			}
		}
		
		return nearestStation;
	}
}