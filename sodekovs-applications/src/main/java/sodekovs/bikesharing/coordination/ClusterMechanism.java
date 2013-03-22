/**
 * 
 */
package sodekovs.bikesharing.coordination;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.ArrayList;
import java.util.List;

import sodekovs.bikesharing.data.clustering.SuperCluster;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.helper.Constants;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Station cluster based coordination mechanism.
 * 
 * @author Thomas Preisler
 */
public class ClusterMechanism extends CoordinationMechanism {
	
	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;
	
	/** The application environment used for proximity calculation */
	protected ContinuousSpace2D appSpace = null;

	private SuperCluster superCluster = null;

	/** The number of published events */
	protected Integer eventNumber = null;

	public ClusterMechanism(CoordinationSpace space) {
		super(space);

		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");
		this.eventNumber = 0;
		
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
		CoordinationInfo coordInfo = (CoordinationInfo) obj;
		ClusterStationCoordData coordData = (ClusterStationCoordData) coordInfo.getValueByName(Constants.VALUE);
		if (coordData.getState().equals(ClusterStationCoordData.STATE_POLLING) || coordData.getState().equals(ClusterStationCoordData.STATE_ALTERNATIVES)) {
			informClusterStations(coordInfo);
		} else if (coordData.getState().equals(ClusterStationCoordData.STATE_REPLY)) {
			informSuperStation(coordInfo);
		}
	}
	private void informSuperStation(CoordinationInfo coordInfo) {
		ClusterStationCoordData coordData = (ClusterStationCoordData) coordInfo.getValueByName(Constants.VALUE);
		String superStationId = coordData.getSuperStationId();
		
		List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();
		
		for (ISpaceObject spaceObj : appSpace.getSpaceObjectsByType("bikestation")) {
			String stationId = (String) spaceObj.getProperty("stationID");
			if (stationId.equals(superStationId)) {
				receiver.add(appSpace.getOwner(spaceObj.getId()));
			}
		}
		
		space.publishCoordinationEvent(coordInfo, receiver, getRealisationName(), eventNumber++);
	}

	private void informClusterStations(CoordinationInfo coordInfo) {
		ClusterStationCoordData coordData = (ClusterStationCoordData) coordInfo.getValueByName(Constants.VALUE);
		List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();
		
		// only inform cluster stations
		List<String> clusterStations = superCluster.getClusterStationIDs(coordData.getSuperStationId());
		for (ISpaceObject spaceObj: appSpace.getSpaceObjectsByType("bikestation")) {
			String stationID = (String) spaceObj.getProperty("stationID");
			if (clusterStations.contains(stationID)) {
				receiver.add(appSpace.getOwner(spaceObj.getId()));
			}
		}
		
		space.publishCoordinationEvent(coordInfo, receiver, getRealisationName(), eventNumber++);
	}
}