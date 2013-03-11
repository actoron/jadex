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

	public static final String CAPACITY = "capacity";
	public static final String STOCK = "stock";
	
	private String superStationId = null;
	
	private Integer state = null;
	
	private Map<String, Object> data = null;

	
	/**
	 * 
	 */
	public ClusterStationCoordData() {
		super();
		this.data = new HashMap<String, Object>();
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
	 * @return the data
	 */
	public Map<String, Object> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "ClusterStationCoordData [superStationId=" + superStationId + ", state=" + state + ", data=" + (data != null ? toString(data.entrySet(), maxLen) : null) + "]";
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (superStationId == null) {
			if (other.superStationId != null)
				return false;
		} else if (!superStationId.equals(other.superStationId))
			return false;
		return true;
	}

}