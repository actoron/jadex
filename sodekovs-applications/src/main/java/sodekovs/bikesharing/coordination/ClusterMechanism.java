/**
 * 
 */
package sodekovs.bikesharing.coordination;

import sodekovs.bikesharing.data.clustering.SuperCluster;
import sodekovs.util.misc.XMLHandler;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Station cluster based coordination mechanism.
 * 
 * @author Thomas Preisler
 */
public class ClusterMechanism extends CoordinationMechanism {

	private SuperCluster superCluster = null;

	/** The number of published events */
	protected Integer eventNumber = null;

	public ClusterMechanism(CoordinationSpace space) {
		super(space);

		// this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.eventNumber = 0;
	}

	@Override
	public void start() {
		String clusterFile = getMechanismConfiguration().getProperty("cluster-file");
		this.superCluster = (SuperCluster) XMLHandler.parseXMLFromXMLFile(clusterFile, SuperCluster.class);
		if (this.superCluster == null) {
			throw new RuntimeException("Could not parse " + clusterFile + " it appears to be null");
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		// TODO Auto-generated method stub
		CoordinationInfo coordInfo = (CoordinationInfo) obj;
		// TODO Vielleicht brauchen wir hier noch ein Hilfsobjekt dass die Nachrichten kapselt damit das Medium weiﬂ was fuer eine Nachricht es empfangen hat und wie es sie entsprechend weiterleiten
		// soll
	}
}