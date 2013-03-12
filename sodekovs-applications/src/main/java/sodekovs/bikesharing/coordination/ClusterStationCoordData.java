/**
 * 
 */
package sodekovs.bikesharing.coordination;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author thomas
 *
 */
public class ClusterStationCoordData {
	
	public static final int STATE_POLLING = 0;
	public static final int STATE_REPLY = 1;
	public static final int STATE_ALTERNATIVES = 2;
	
	private String superStationId = null;
	
	private Integer state = null;
	
	private CoordinationStationData stationData = null;
	
	private Map<String, String> proposedArrivalStations = null;
	
	private Map<String, String> proposedDepartureStations = null;
	
	/**
	 * 
	 */
	public ClusterStationCoordData() {
		super();
		this.proposedArrivalStations = new HashMap<String, String>();
		this.proposedDepartureStations = new HashMap<String, String>();
	}

	/**
	 * @return the superStationId
	 */
	public String getSuperStationId() {
		return superStationId;
	}

	/**
	 * @param superStationId the superStationId to set
	 */
	public void setSuperStationId(String superStationId) {
		this.superStationId = superStationId;
	}

	/**
	 * @return the state
	 */
	public Integer getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * @return the stationData
	 */
	public CoordinationStationData getStationData() {
		return stationData;
	}

	/**
	 * @param stationData the stationData to set
	 */
	public void setStationData(CoordinationStationData stationData) {
		this.stationData = stationData;
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the proposedArrivalStations
	 */
	public Map<String, String> getProposedArrivalStations() {
		return proposedArrivalStations;
	}

	/**
	 * @param proposedArrivalStations the proposedArrivalStations to set
	 */
	public void setProposedArrivalStations(Map<String, String> proposedArrivalStations) {
		this.proposedArrivalStations = proposedArrivalStations;
	}

	/**
	 * @return the proposedDepartureStations
	 */
	public Map<String, String> getProposedDepartureStations() {
		return proposedDepartureStations;
	}

	/**
	 * @param proposedDepartureStations the proposedDepartureStations to set
	 */
	public void setProposedDepartureStations(Map<String, String> proposedDepartureStations) {
		this.proposedDepartureStations = proposedDepartureStations;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "ClusterStationCoordData [superStationId=" + superStationId + ", state=" + state + ", stationData=" + stationData + ", proposedArrivalStations="
				+ (proposedArrivalStations != null ? toString(proposedArrivalStations.entrySet(), maxLen) : null) + ", proposedDepartureStations="
				+ (proposedDepartureStations != null ? toString(proposedDepartureStations.entrySet(), maxLen) : null) + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proposedArrivalStations == null) ? 0 : proposedArrivalStations.hashCode());
		result = prime * result + ((proposedDepartureStations == null) ? 0 : proposedDepartureStations.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((stationData == null) ? 0 : stationData.hashCode());
		result = prime * result + ((superStationId == null) ? 0 : superStationId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterStationCoordData other = (ClusterStationCoordData) obj;
		if (proposedArrivalStations == null) {
			if (other.proposedArrivalStations != null)
				return false;
		} else if (!proposedArrivalStations.equals(other.proposedArrivalStations))
			return false;
		if (proposedDepartureStations == null) {
			if (other.proposedDepartureStations != null)
				return false;
		} else if (!proposedDepartureStations.equals(other.proposedDepartureStations))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (stationData == null) {
			if (other.stationData != null)
				return false;
		} else if (!stationData.equals(other.stationData))
			return false;
		if (superStationId == null) {
			if (other.superStationId != null)
				return false;
		} else if (!superStationId.equals(other.superStationId))
			return false;
		return true;
	}

}