/**
 * 
 */
package sodekovs.bikesharing.coordination;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sodekovs.bikesharing.data.clustering.Cluster;
import sodekovs.bikesharing.data.clustering.SuperCluster;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.helper.Constants;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Tuple based decentralized coordination mechanism. The mechanism receives information about changed occupancies in the bike stations and stores them. It informs the other stations situated in the
 * same cluster as the sending stations if the occupancy state of the station has changed. The state is determined by global coordination parameters which define thresholds for empty or full stations.
 * 
 * @author Thomas Preisler
 */
public class TupleBasedMechanism extends CoordinationMechanism {

	private static final Integer FULL = 1;
	private static final Integer NORMAL = 0;
	private static final Integer EMPTY = -1;

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	/** The application environment used for proximity calculation */
	protected ContinuousSpace2D appSpace = null;

	private SuperCluster superCluster = null;

	private Map<String, CoordinationStationData> occupancyTuples = null;
	
	protected SpaceObject tupleCoordination = null;

	/** The number of published events */
	protected Integer eventNumber = null;

	public TupleBasedMechanism(CoordinationSpace space) {
		super(space);
		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");
		this.eventNumber = 0;
		
		this.tupleCoordination = (SpaceObject) appSpace.getSpaceObjectsByType("tupleCoordination")[0];

		this.superCluster = (SuperCluster) appSpace.getProperty("StationCluster");

		this.occupancyTuples = new HashMap<String, CoordinationStationData>();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		if (superCluster == null) {
			superCluster = (SuperCluster) appSpace.getProperty("StationCluster");
		}
		
		CoordinationInfo coordInfo = (CoordinationInfo) obj;
		// get the tuple from the coordination info
		CoordinationStationData tuple = (CoordinationStationData) coordInfo.getValueByName(Constants.VALUE);
		// get the old/previous stored tuple
		CoordinationStationData oldTuple = occupancyTuples.get(tuple.getStationID());

		// store the new tuple
		occupancyTuples.put(tuple.getStationID(), tuple);

		// check if the state of the tuple has changed if the tuple was previously stored
		if (oldTuple == null || getState(oldTuple) != getState(tuple)) {
			// if the state has changed or it was not stored previously...
			// ...inform the other cluster stations
//			tupleCoordination.setProperty("stateChanged", (Integer) tupleCoordination.getProperty("stateChanged") + 1);
			tupleCoordination.incrementProperty("stateChanged", 1);
			
			Cluster cluster = superCluster.getCluster(tuple.getStationID());
			List<String> stationIDs = superCluster.getStationIDs(cluster);
			informStations(stationIDs, coordInfo);
		}
	}

	/**
	 * Informs all stations referenced in the list of station ids about the {@link CoordinationInfo}.
	 * 
	 * @param stationIDs
	 * @param coordInfo
	 */
	private void informStations(List<String> stationIDs, CoordinationInfo coordInfo) {
		List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();

		for (ISpaceObject spaceObj : appSpace.getSpaceObjectsByType("bikestation")) {
			String stationID = (String) spaceObj.getProperty("stationID");
			if (stationIDs.contains(stationID)) {
				receiver.add(appSpace.getOwner(spaceObj.getId()));
			}
		}

		space.publishCoordinationEvent(coordInfo, receiver, getRealisationName(), eventNumber++);
	}

	/**
	 * Returns the state of a tuple as an integer constant.
	 * 
	 * @param tuple
	 * @return
	 */
	private Integer getState(CoordinationStationData tuple) {
		Double fullThreshold = getMechanismConfiguration().getDoubleProperty("FULL_THRESHOLD");
		Double emptyThreshold = getMechanismConfiguration().getDoubleProperty("EMPTY_THRESHOLD");
		Double occupancy = tuple.getOccupancy();

		if (occupancy >= fullThreshold) {
			return FULL;
		} else if (occupancy <= emptyThreshold) {
			return EMPTY;
		} else {
			return NORMAL;
		}
	}
}