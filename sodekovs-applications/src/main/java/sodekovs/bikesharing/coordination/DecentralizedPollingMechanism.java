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
import java.util.List;

import sodekovs.bikesharing.data.clustering.Cluster;
import sodekovs.bikesharing.data.clustering.SuperCluster;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.helper.Constants;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Coordination mechanism for the decentralized polling coordination.
 * 
 * @author Thomas Preisler
 */
public class DecentralizedPollingMechanism extends CoordinationMechanism {
	
	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	/** The application environment used for proximity calculation */
	protected ContinuousSpace2D appSpace = null;

	private SuperCluster superCluster = null;

	/** The number of published events */
	protected Integer eventNumber = null;
	
	protected SpaceObject decentralizedPollingCoordination = null;

	public DecentralizedPollingMechanism(CoordinationSpace space) {
		super(space);
		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");
		this.eventNumber = 0;

		this.decentralizedPollingCoordination = (SpaceObject) appSpace.getSpaceObjectsByType("decentralizedPollingCoordination")[0];
		
		this.superCluster = (SuperCluster) appSpace.getProperty("StationCluster");
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
		StateCoordinationStationData data = (StateCoordinationStationData) coordInfo.getValueByName(Constants.VALUE);
		
		if (data.getState().equals(StateCoordinationStationData.REQUEST)) {
//			decentralizedPollingCoordination.setProperty("request", (Integer) decentralizedPollingCoordination.getProperty("request") + 1);
			decentralizedPollingCoordination.incrementProperty("request", 1);
			handleRequest(coordInfo);
		} else if (data.getState().equals(StateCoordinationStationData.REPLY)) {
//			decentralizedPollingCoordination.setProperty("reply", (Integer) decentralizedPollingCoordination.getProperty("reply") + 1);
			decentralizedPollingCoordination.incrementProperty("reply", 1);
			handleReply(coordInfo);
		}
	}

	private void handleReply(CoordinationInfo coordInfo) {
		StateCoordinationStationData data = (StateCoordinationStationData) coordInfo.getValueByName(Constants.VALUE);
		
		List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();

		for (ISpaceObject spaceObj : appSpace.getSpaceObjectsByType("bikestation")) {
			String stationID = (String) spaceObj.getProperty("stationID");
			if (data.getOriginatorID().equals(stationID)) {
				receiver.add(appSpace.getOwner(spaceObj.getId()));
				break;
			}
		}
		
		space.publishCoordinationEvent(coordInfo, receiver, getRealisationName(), eventNumber++);
	}

	private void handleRequest(CoordinationInfo coordInfo) {
		StateCoordinationStationData data = (StateCoordinationStationData) coordInfo.getValueByName(Constants.VALUE);
		Cluster cluster = superCluster.getCluster(data.getStationID());
		List<String> stationIDs = superCluster.getStationIDs(cluster);
		
		List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();

		for (ISpaceObject spaceObj : appSpace.getSpaceObjectsByType("bikestation")) {
			String stationID = (String) spaceObj.getProperty("stationID");
			if (stationIDs.contains(stationID)) {
				receiver.add(appSpace.getOwner(spaceObj.getId()));
			}
		}

		space.publishCoordinationEvent(coordInfo, receiver, getRealisationName(), eventNumber++);
	}
}