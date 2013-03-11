/**
 * 
 */
package sodekovs.bikesharing.data.clustering;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author thomas
 *
 */
@XmlRootElement(name = "superCluster")
public class SuperCluster {

	private List<Cluster> cluster = null;
	
	public SuperCluster() {
		this.cluster = new ArrayList<Cluster>();
	}
	
	public SuperCluster(List<Cluster> cluster) {
		this.cluster = cluster;
	}

	/**
	 * @return the cluster
	 */
	@XmlElementWrapper(name = "cluster")
	@XmlElement(name = "cluster")
	public List<Cluster> getCluster() {
		return cluster;
	}

	/**
	 * @param cluster the cluster to set
	 */
	public void setCluster(List<Cluster> cluster) {
		this.cluster = cluster;
	}
	
	public List<String> getClusterStationIDs(String superStationId) {
		List<String> clusterStations = new ArrayList<String>();
		
		for (Cluster cluster : this.cluster) {
			Station superStation = cluster.getSuperStation();
			if (superStation.getName().equals(superStationId)) {
				for (Station station : cluster.getStations()) {
					clusterStations.add(station.getName());
				}
			}
		}
		
		return clusterStations;
	}
}
