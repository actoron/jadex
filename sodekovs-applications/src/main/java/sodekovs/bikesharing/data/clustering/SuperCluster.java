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
	 * @param cluster
	 *            the cluster to set
	 */
	public void setCluster(List<Cluster> cluster) {
		this.cluster = cluster;
	}

	/**
	 * Returns the {@link List} of all station ids for the given super station
	 * id.
	 * 
	 * @param superStationId
	 * @return
	 */
	public List<String> getClusterStationIDs(String superStationId) {
		List<String> clusterStations = new ArrayList<String>();

		for (Cluster cluster : this.cluster) {
			Station superStation = cluster.getSuperStation();
			if (superStation != null) {
				if (superStation.getName().equals(superStationId)) {
					for (Station station : cluster.getStations()) {
						clusterStations.add(station.getName());
					}
				}
			}
		}

		return clusterStations;
	}

	/**
	 * Returns the {@link Cluster} which contains a station with the given
	 * station id.
	 * 
	 * @param stationId
	 * @return
	 */
	public Cluster getCluster(String stationId) {
		for (Cluster cluster : this.cluster) {
			for (Station station : cluster.getStations()) {
				if (station.getName().equals(stationId)) {
					return cluster;
				}
			}
		}

		return null;
	}

	/**
	 * Returns a {@link List} with the station ids from all the stations in the
	 * given {@link Cluster}.
	 * 
	 * @param cluster
	 * @return
	 */
	public List<String> getStationIDs(Cluster cluster) {
		List<String> stationIds = new ArrayList<String>();

		for (Station station : cluster.getStations()) {
			stationIds.add(station.getName());
		}

		return stationIds;
	}
}
