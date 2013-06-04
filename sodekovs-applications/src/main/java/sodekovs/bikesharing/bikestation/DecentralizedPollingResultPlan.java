/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.ArrayList;
import java.util.List;

import sodekovs.bikesharing.coordination.StateCoordinationStationData;
import sodekovs.bikesharing.data.clustering.Cluster;
import sodekovs.bikesharing.data.clustering.SuperCluster;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Plan is called when a decentralized polling reply was received. If all replies are received the station calculates alternative departure and arrival stations.
 * 
 * @author Thomas Preisler
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

		while (true) {
			waitForTick();
			if (getWaitqueue().size() >= noStations) {
//				System.out.println("DecentralizedPollingResultPlan in " + getComponentName() + " received all " + noStations + " answers");
				List<StateCoordinationStationData> answers = new ArrayList<StateCoordinationStationData>();
				Object[] elements = getWaitqueue().getElements();
				for (Object element : elements) {
					IInternalEvent event = (IInternalEvent) element;
					StateCoordinationStationData data = (StateCoordinationStationData) event.getParameter("data").getValue();
					answers.add(data);
					getWaitqueue().removeElement(element);
				}
				
				calculateProposedStations(answers);
			}
		}
	}

	private void calculateProposedStations(List<StateCoordinationStationData> answers) {
		CoordinationSpace coordSpace = (CoordinationSpace) getBeliefbase().getBelief("env").getFact();
		CoordinationMechanism mechanism = coordSpace.getActiveCoordinationMechanisms().get("decentralized_polling_reply");
		Double fullThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("FULL_THRESHOLD");
		Double emptyThreshold = mechanism.getMechanismConfiguration().getDoubleProperty("EMPTY_THRESHOLD");
		Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
		Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
		Double occupancy = Double.valueOf(stock) / Double.valueOf(capacity);
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();

		// full
		if (occupancy >= fullThreshold) {
//			getBeliefbase().getBelief("proposed_departure_station").setFact(null);
			myself.setProperty("proposed_departure_station", null);

			List<StateCoordinationStationData> suitableStations = new ArrayList<StateCoordinationStationData>();
			for (StateCoordinationStationData data : answers) {
				if (data.getOccupancy() < fullThreshold) {
					suitableStations.add(data);
				}
			}

			String nearestStationId = getNearest(suitableStations);
//			getBeliefbase().getBelief("proposed_arrival_station").setFact(nearestStationId);
			myself.setProperty("proposed_arrival_station", nearestStationId);
		}
		// empty
		else if (occupancy <= emptyThreshold) {
//			getBeliefbase().getBelief("proposed_arrival_station").setFact(null);
			myself.setProperty("proposed_arrival_station", null);

			List<StateCoordinationStationData> suitableStations = new ArrayList<StateCoordinationStationData>();
			for (StateCoordinationStationData data : answers) {
				if (data.getOccupancy() > emptyThreshold) {
					suitableStations.add(data);
				}
			}

			String nearestStationId = getNearest(suitableStations);
//			getBeliefbase().getBelief("proposed_departure_station").setFact(nearestStationId);
			myself.setProperty("proposed_departure_station", nearestStationId);
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